"""Generates verified curriculum JSON, 15 tryout packs, and real offline 16kHz WAV audio assets."""

import hashlib
import json
import os
import shutil
import subprocess
import tempfile
import wave
from pathlib import Path

from curriculum_data import CURRICULUM_MODULES
from packs_itp import ITP_PACKS
from packs_ibt import IBT_PACKS
from packs_ielts import IELTS_PACKS

ROOT = Path(__file__).resolve().parent.parent
PACKS_ROOT = ROOT / "packs"
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
    assert len(CURRICULUM_MODULES) == 18, f"Expected 18 curriculum modules, got {len(CURRICULUM_MODULES)}"
    assert len(all_packs) == 15, f"Expected 15 packs, got {len(all_packs)}"
    mod_ids = {m["id"] for m in CURRICULUM_MODULES}
    seen_qids = set()
    for m in CURRICULUM_MODULES:
        assert len(m["drills"]) >= 3, f"Module {m['id']} needs >=3 drills"
        for d in m["drills"]:
            assert d["id"] not in seen_qids
            seen_qids.add(d["id"])
            assert d["answer"], f"Missing answer on {d['id']}"

    exam_counts = {}
    for p in all_packs:
        exam_counts[p["examId"]] = exam_counts.get(p["examId"], 0) + 1
        assert len(p["questions"]) == 12, f"Pack {p['packId']} should have 12 sample questions"
        p["status"] = "SAMPLE" if len(p["questions"]) < p["blueprintCount"] else "READY"
        for q in p["questions"]:
            assert q["id"] not in seen_qids, f"Duplicate ID {q['id']}"
            seen_qids.add(q["id"])
            assert q["rationaleSource"] in mod_ids, f"Bad rationaleSource {q['rationaleSource']}"
            if q["taskType"] not in ("WRITING_RESPONSE", "SPEAKING_RESPONSE"):
                assert q["answer"], f"Objective question {q['id']} missing answer"

    assert exam_counts == {"ITP_L1": 5, "IBT_2026": 5, "IELTS_ACADEMIC": 5}, f"Bad exam counts: {exam_counts}"
    return all_packs


def synthesize_all_wavs(jobs):
    """jobs: list of (target_wav_path, [(speaker, text), ...])"""
    ASSETS_AUDIO.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as td:
        td_path = Path(td)
        ps_lines = [
            "Add-Type -AssemblyName System.Speech",
            "$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer",
            "$fmt = New-Object System.Speech.AudioFormat.SpeechAudioFormatInfo(16000, [System.Speech.AudioFormat.AudioBitsPerSample]::Sixteen, [System.Speech.AudioFormat.AudioChannel]::Mono)",
        ]
        manifest = []
        idx = 0
        for out_path, turns in jobs:
            turn_files = []
            for spk, text in turns:
                part_wav = td_path / f"turn_{idx}.wav"
                idx += 1
                voice = VOICE_MAP.get(spk, "Microsoft David Desktop")
                safe_text = text.replace("'", "''")
                safe_path = str(part_wav).replace("'", "''")
                ps_lines.append(f"$synth.SelectVoice('{voice}')")
                ps_lines.append(f"$synth.SetOutputToWaveFile('{safe_path}', $fmt)")
                ps_lines.append(f"$synth.Speak('{safe_text}')")
                turn_files.append(part_wav)
            manifest.append((out_path, turn_files))
        ps_lines.append("$synth.Dispose()")

        script_path = td_path / "tts_batch.ps1"
        script_path.write_text("\n".join(ps_lines), encoding="utf-8")
        subprocess.run(
            ["powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", str(script_path)],
            check=True,
        )

        # 300ms silence at 16kHz 16-bit mono = 16000 * 0.3 * 2 = 9600 bytes
        silence = b"\x00" * 9600
        durations = {}
        for out_path, turn_files in manifest:
            frames = []
            params = None
            for i, tf in enumerate(turn_files):
                with wave.open(str(tf), "rb") as w:
                    if params is None:
                        params = w.getparams()
                    frames.append(w.readframes(w.getnframes()))
                    if i < len(turn_files) - 1:
                        frames.append(silence)
            raw = b"".join(frames)
            with wave.open(str(out_path), "wb") as out_w:
                out_w.setnchannels(1)
                out_w.setsampwidth(2)
                out_w.setframerate(16000)
                out_w.writeframes(raw)
            duration_ms = int((len(raw) / (16000 * 2)) * 1000)
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
    print(f"Generated {len(CURRICULUM_MODULES)} curriculum modules, {len(all_packs)} packs, {len(jobs)} WAV files ({total_audio_mb:.2f} MB)")


if __name__ == "__main__":
    main()
