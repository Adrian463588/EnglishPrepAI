"""Generates verified curriculum JSON (30 modules), 15 full tryout packs (1,725 items), and offline IMA-ADPCM WAV audio."""

import hashlib
import json
import os
import shutil
import subprocess
import tempfile
import wave
from pathlib import Path

from curriculum_data import CURRICULUM_MODULES
from ima_adpcm_wav import encode_pcm16_to_ima_adpcm_wav
from pack_schema_helpers import validate_pack_collection
from packs_ibt import IBT_PACKS
from packs_ielts import IELTS_PACKS
from packs_itp import ITP_PACKS

ROOT = Path(__file__).resolve().parent.parent
PACKS_ROOT = ROOT / "packs"
TTS_CACHE = ROOT / "tools" / ".tts_cache"
ASSETS_DIR = ROOT / "app" / "src" / "main" / "assets"
ASSETS_PACKS = ASSETS_DIR / "packs"
ASSETS_CURR = ASSETS_DIR / "curriculum"
ASSETS_AUDIO = ASSETS_DIR / "audio"

VOICE_MAP = {
    "W": "Microsoft Zira Desktop",
    "M": "Microsoft David Desktop",
    "N": "Microsoft David Desktop",
}


def sha256_file(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            h.update(chunk)
    return h.hexdigest()


def sha256_bytes(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def validate_all():
    all_packs = ITP_PACKS + IBT_PACKS + IELTS_PACKS
    assert len(CURRICULUM_MODULES) == 30, f"Expected 30 curriculum modules, got {len(CURRICULUM_MODULES)}"
    assert len(all_packs) == 15, f"Expected 15 packs, got {len(all_packs)}"
    mod_ids = {m["id"] for m in CURRICULUM_MODULES}
    seen_qids = set()
    for m in CURRICULUM_MODULES:
        assert len(m["drills"]) >= 3, f"Module {m['id']} needs >=3 drills"
        for d in m["drills"]:
            assert d["id"] not in seen_qids, f"Duplicate drill ID {d['id']}"
            seen_qids.add(d["id"])
            assert d["answer"], f"Missing answer on {d['id']}"

    validate_pack_collection(all_packs, mod_ids)
    for p in all_packs:
        p["status"] = "READY" if len(p["questions"]) >= p["blueprintCount"] else "SAMPLE"
        for q in p["questions"]:
            assert q["id"] not in seen_qids, f"Duplicate ID {q['id']}"
            seen_qids.add(q["id"])
    return all_packs


def _clean_tts_text(text: str) -> str:
    cleaned = (
        text.replace("—", ", ")
        .replace("–", ", ")
        .replace("\n", " ")
        .replace("\r", " ")
        .replace("’", "'")
        .replace("“", '"')
        .replace("”", '"')
    )
    return " ".join(cleaned.split())


def _job_cache_key(turns: list[tuple[str, str]]) -> str:
    payload = json.dumps({"v": 2, "turns": turns, "codec": "ima_adpcm_16k"}, ensure_ascii=False)
    return sha256_bytes(payload.encode("utf-8"))


def synthesize_all_wavs(jobs):
    """jobs: list of (target_wav_path, [(speaker, text), ...])"""
    ASSETS_AUDIO.mkdir(parents=True, exist_ok=True)
    TTS_CACHE.mkdir(parents=True, exist_ok=True)
    durations = {}
    uncached = []

    for out_path, turns in jobs:
        key = _job_cache_key(turns)
        cached_wav = TTS_CACHE / f"{key}.wav"
        cached_meta = TTS_CACHE / f"{key}.json"
        if cached_wav.exists() and cached_meta.exists():
            shutil.copyfile(cached_wav, out_path)
            meta = json.loads(cached_meta.read_text(encoding="utf-8"))
            durations[out_path.name] = int(meta["durationMs"])
        else:
            uncached.append((out_path, turns, key))

    print(f"Audio jobs: {len(jobs)} total | {len(jobs) - len(uncached)} cached | {len(uncached)} to synthesize")
    silence = b"\x00" * 9600  # 300ms silence at 16kHz 16-bit mono
    batch_size = 80

    for b_start in range(0, len(uncached), batch_size):
        batch = uncached[b_start : b_start + batch_size]
        print(f"Synthesizing batch {b_start // batch_size + 1}/{(len(uncached) + batch_size - 1) // batch_size} ({len(batch)} clips)...", flush=True)
        with tempfile.TemporaryDirectory() as td:
            td_path = Path(td)
            ps_lines = [
                "Add-Type -AssemblyName System.Speech",
                "$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer",
                "$fmt = New-Object System.Speech.AudioFormat.SpeechAudioFormatInfo(16000, [System.Speech.AudioFormat.AudioBitsPerSample]::Sixteen, [System.Speech.AudioFormat.AudioChannel]::Mono)",
            ]
            manifest = []
            idx = 0
            for out_path, turns, key in batch:
                turn_files = []
                for spk, raw_text in turns:
                    part_wav = td_path / f"turn_{idx}.wav"
                    idx += 1
                    voice = VOICE_MAP.get(spk, "Microsoft David Desktop")
                    safe_text = _clean_tts_text(raw_text).replace("'", "''")
                    safe_path = str(part_wav).replace("'", "''")
                    ps_lines.append(f"$synth.SelectVoice('{voice}')")
                    ps_lines.append(f"$synth.SetOutputToWaveFile('{safe_path}', $fmt)")
                    ps_lines.append(f"$synth.Speak('{safe_text}')")
                    turn_files.append(part_wav)
                manifest.append((out_path, turn_files, key))
            ps_lines.append("$synth.Dispose()")

            script_path = td_path / "tts_batch.ps1"
            script_path.write_text("\n".join(ps_lines), encoding="utf-8")
            subprocess.run(
                ["powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", str(script_path)],
                check=True,
            )

            for out_path, turn_files, key in manifest:
                frames = []
                for i, tf in enumerate(turn_files):
                    with wave.open(str(tf), "rb") as w:
                        frames.append(w.readframes(w.getnframes()))
                        if i < len(turn_files) - 1:
                            frames.append(silence)
                raw = b"".join(frames)
                adpcm_bytes = encode_pcm16_to_ima_adpcm_wav(raw, sample_rate=16000)
                out_path.write_bytes(adpcm_bytes)
                (TTS_CACHE / f"{key}.wav").write_bytes(adpcm_bytes)
                duration_ms = int((len(raw) / (16000 * 2)) * 1000)
                (TTS_CACHE / f"{key}.json").write_text(json.dumps({"durationMs": duration_ms}), encoding="utf-8")
                durations[out_path.name] = duration_ms

    return durations


def main():
    all_packs = validate_all()
    for d in [PACKS_ROOT, ASSETS_PACKS, ASSETS_CURR, ASSETS_AUDIO]:
        d.mkdir(parents=True, exist_ok=True)

    # Collect audio generation jobs
    jobs = []
    audio_records = {}  # audioId -> (filename, transcript)
    for m in CURRICULUM_MODULES:
        if m.get("audioScript"):
            aid = f"{m['id']}-DEMO"
            fname = f"{aid}.wav"
            jobs.append((ASSETS_AUDIO / fname, [("M", m["audioScript"])]))
            audio_records[aid] = (fname, m["audioScript"])
            m["audioAssetPath"] = f"audio/{fname}"
        else:
            m["audioAssetPath"] = None

    for p in all_packs:
        pack_assets = []
        for q in p["questions"]:
            if q.get("audioAssetId") and q.get("audioTurns"):
                aid = q["audioAssetId"]
                fname = f"{aid}.wav"
                jobs.append((ASSETS_AUDIO / fname, q["audioTurns"]))
                transcript = " ".join(f"{s}: {t}" for s, t in q["audioTurns"])
                audio_records[aid] = (fname, transcript)
                pack_assets.append({
                    "id": aid,
                    "relativePath": f"audio/{fname}",
                    "kind": "AUDIO",
                    "caption": transcript,
                    "license": "Original",
                })
        p["assets"] = pack_assets

    expected_wavs = {out_path.name for out_path, _ in jobs}
    for old_wav in ASSETS_AUDIO.glob("*.wav"):
        if old_wav.name not in expected_wavs:
            old_wav.unlink()

    durations = synthesize_all_wavs(jobs)

    # Enrich media assets with sha256, byteSize, durationMs
    for p in all_packs:
        for a in p["assets"]:
            fpath = ASSETS_DIR / a["relativePath"]
            a["sha256"] = sha256_file(fpath)
            a["byteSize"] = fpath.stat().st_size
            a["durationMs"] = durations[fpath.name]

    # Build clean pack files (strip raw audioTurns list or keep transcript cleanly in assets)
    index_entries = []
    for p in all_packs:
        pack_doc = {
            "schemaVersion": 1,
            "exam": p["examId"],
            "edition": p["edition"],
            "packId": p["packId"],
            "title": p["title"],
            "theme": p["theme"],
            "status": p["status"],
            "blueprintCount": p["blueprintCount"],
            "availableCount": len(p["questions"]),
            "durationMinutes": p["durationMinutes"],
            "blueprintDurationMinutes": p["blueprintDurationMinutes"],
            "license": p["license"],
            "provenance": p["provenance"],
            "passages": p["passages"],
            "assets": p["assets"],
            "items": [],
        }
        for idx, q in enumerate(p["questions"], start=1):
            item_clean = {k: v for k, v in q.items() if k != "audioTurns"}
            item_clean["sortIndex"] = idx
            pack_doc["items"].append(item_clean)

        raw_json = json.dumps(pack_doc, indent=2, ensure_ascii=False).encode("utf-8")
        pack_doc["checksum"] = sha256_bytes(raw_json)
        final_bytes = (json.dumps(pack_doc, indent=2, ensure_ascii=False) + "\n").encode("utf-8")

        (PACKS_ROOT / f"{p['packId']}.json").write_bytes(final_bytes)
        (ASSETS_PACKS / f"{p['packId']}.json").write_bytes(final_bytes)

        index_entries.append({
            "packId": p["packId"],
            "exam": p["examId"],
            "edition": p["edition"],
            "title": p["title"],
            "theme": p["theme"],
            "status": p["status"],
            "blueprintCount": p["blueprintCount"],
            "availableCount": len(p["questions"]),
            "durationMinutes": p["durationMinutes"],
            "blueprintDurationMinutes": p["blueprintDurationMinutes"],
            "license": p["license"],
            "provenance": p["provenance"],
            "fileName": f"{p['packId']}.json",
            "checksum": pack_doc["checksum"],
            "audioBytes": sum(a["byteSize"] for a in p["assets"]),
        })

    index_doc = {
        "schemaVersion": 1,
        "generatedAt": "2026-11-16",
        "totalPacks": len(index_entries),
        "packs": index_entries,
    }
    index_bytes = (json.dumps(index_doc, indent=2, ensure_ascii=False) + "\n").encode("utf-8")
    (PACKS_ROOT / "index.json").write_bytes(index_bytes)
    (ASSETS_PACKS / "index.json").write_bytes(index_bytes)

    curr_doc = {
        "schemaVersion": 1,
        "modules": CURRICULUM_MODULES,
    }
    (ASSETS_CURR / "curriculum.json").write_text(
        json.dumps(curr_doc, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )

    total_audio_mb = sum(f.stat().st_size for f in ASSETS_AUDIO.glob("*.wav")) / (1024 * 1024)
    total_q = sum(len(p["questions"]) for p in all_packs)
    print(f"Generated {len(CURRICULUM_MODULES)} curriculum modules, {len(all_packs)} packs ({total_q} items), {len(jobs)} WAV files ({total_audio_mb:.2f} MB)")


if __name__ == "__main__":
    main()
