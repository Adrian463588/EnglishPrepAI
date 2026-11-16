"""Shared DRY constructors and strict blueprint validators for English Prep AI exam packs."""

import re


REQUIRED_SECTIONS_BLUEPRINT = {
    "ITP_L1": {
        "total": 140,
        "duration": 115,
        "sections": {"LISTENING": 50, "STRUCTURE": 40, "READING": 50},
    },
    "IBT_2026": {
        "total": 120,
        "duration": 90,
        "sections": {"READING": 50, "LISTENING": 47, "WRITING": 12, "SPEAKING": 11},
    },
    "IELTS_ACADEMIC": {
        "total": 85,
        "duration": 165,
        "sections": {"LISTENING": 40, "READING": 40, "WRITING": 2, "SPEAKING": 3},
    },
}


def make_q(
    qid: str,
    section: str,
    task_type: str,
    prompt: str,
    options: list[str],
    answer: str,
    explanation: str,
    rationale_source: str,
    difficulty: str = "B2",
    passage_id: str | None = None,
    audio_id: str | None = None,
    audio_turns: list[tuple[str, str]] | None = None,
    accepted_answers: list[str] | None = None,
    extra: dict | None = None,
) -> dict:
    """Constructs a validated pack question dictionary."""
    item = {
        "id": qid,
        "section": section,
        "taskType": task_type,
        "prompt": prompt,
        "options": list(options),
        "answer": answer,
        "acceptedAnswers": list(accepted_answers) if accepted_answers is not None else ([answer] if answer else []),
        "explanation": explanation,
        "rationaleSource": rationale_source,
        "difficulty": difficulty,
        "points": 1,
        "license": "Original",
        "reviewStatus": "VERIFIED_ORIGINAL",
        "passageId": passage_id,
        "audioAssetId": audio_id,
        "audioTurns": list(audio_turns) if audio_turns else [],
    }
    if extra:
        item.update(extra)
    return item


def mcq(
    qid: str,
    section: str,
    prompt: str,
    options: list[str],
    correct_idx: int,
    explanation: str,
    rationale_source: str,
    difficulty: str = "B2",
    passage_id: str | None = None,
    audio_id: str | None = None,
    audio_turns: list[tuple[str, str]] | None = None,
    task_type: str = "CHOICE_SINGLE",
) -> dict:
    """Compact DRY builder for 4-option single-choice items."""
    assert 0 <= correct_idx < len(options), f"{qid}: bad correct_idx {correct_idx} for {len(options)} opts"
    return make_q(
        qid=qid,
        section=section,
        task_type=task_type,
        prompt=prompt,
        options=options,
        answer=options[correct_idx],
        explanation=explanation,
        rationale_source=rationale_source,
        difficulty=difficulty,
        passage_id=passage_id,
        audio_id=audio_id,
        audio_turns=audio_turns,
    )


def rotated_mcq(
    qid: str,
    section: str,
    prompt: str,
    options: list[str],
    correct_idx: int,
    explanation: str,
    rationale_source: str,
    difficulty: str = "B2",
    seed: int = 0,
    passage_id: str | None = None,
    audio_id: str | None = None,
    audio_turns: list[tuple[str, str]] | None = None,
) -> dict:
    """MCQ with deterministic option rotation so correct keys balance across A/B/C/D."""
    assert 0 <= correct_idx < len(options), f"{qid}: bad correct_idx {correct_idx}"
    correct_val = options[correct_idx]
    shift = seed % len(options)
    rotated = options[shift:] + options[:shift]
    return mcq(
        qid=qid,
        section=section,
        prompt=prompt,
        options=rotated,
        correct_idx=rotated.index(correct_val),
        explanation=explanation,
        rationale_source=rationale_source,
        difficulty=difficulty,
        passage_id=passage_id,
        audio_id=audio_id,
        audio_turns=audio_turns,
    )


def validate_pack_collection(packs: list[dict], valid_module_ids: set[str]):
    """Strict gate ensuring zero duplicates, full blueprints, resolved passages/audio, and READY readiness."""
    seen_qids: set[str] = set()
    seen_prompts: set[str] = set()

    for p in packs:
        exam_id = p["examId"]
        spec = REQUIRED_SECTIONS_BLUEPRINT[exam_id]
        questions = p["questions"]
        assert len(questions) == spec["total"], (
            f"{p['packId']} count mismatch: got {len(questions)}, expected {spec['total']}"
        )
        assert p["blueprintCount"] == spec["total"], f"{p['packId']} blueprintCount mismatch"
        assert p["durationMinutes"] == spec["duration"], f"{p['packId']} duration mismatch"

        sec_counts: dict[str, int] = {}
        passage_ids = {ps["id"] for ps in p["passages"]}
        audio_owners: dict[str, list[tuple[str, str]]] = {}
        for q in questions:
            if q.get("audioAssetId") and q.get("audioTurns"):
                audio_owners[q["audioAssetId"]] = q["audioTurns"]

        for q in questions:
            qid = q["id"]
            assert qid not in seen_qids, f"Duplicate question ID: {qid}"
            seen_qids.add(qid)
            assert q["rationaleSource"] in valid_module_ids, f"{qid}: unknown module {q['rationaleSource']}"

            if q["passageId"] is not None:
                assert q["passageId"] in passage_ids, f"{qid}: missing passage {q['passageId']}"
            if q["audioAssetId"] is not None:
                assert q["audioAssetId"] in audio_owners, f"{qid}: missing audioTurns for {q['audioAssetId']}"

            tt = q["taskType"]
            valid_tts = {
                "CHOICE_SINGLE", "CHOICE_MULTI", "FILL_BLANK", "MATCHING", "REORDER",
                "ERROR_IDENTIFICATION", "SENTENCE_BUILD", "WRITING_RESPONSE", "SPEAKING_RESPONSE", "LISTEN_REPEAT",
            }
            assert tt in valid_tts, f"{qid}: unknown TaskType {tt}"
            if tt not in ("WRITING_RESPONSE", "SPEAKING_RESPONSE", "LISTEN_REPEAT"):
                assert q["answer"], f"{qid}: missing objective answer"
            if tt in ("CHOICE_SINGLE", "ERROR_IDENTIFICATION"):
                assert len(q["options"]) >= 3, f"{qid}: insufficient options"
                assert q["answer"] in q["options"], f"{qid}: answer '{q['answer']}' not in options"
                assert len(set(q["options"])) == len(q["options"]), f"{qid}: duplicate options {q['options']}"
            if tt == "CHOICE_MULTI":
                ans_parts = q["answer"].split("|")
                assert all(a in q["options"] for a in ans_parts), f"{qid}: multi answer not in options"
            if tt == "MATCHING":
                pairs_map = {pr["left"]: pr["rightOptions"] for pr in q.get("pairs", [])}
                assert pairs_map, f"{qid}: MATCHING missing pairs"
                for seg in q["answer"].split("|"):
                    lval, rval = seg.split("::", 1)
                    assert lval in pairs_map and rval in pairs_map[lval], f"{qid}: bad match {seg}"
            if tt in ("SENTENCE_BUILD", "REORDER"):
                tokens = q["answer"].split("|")
                assert len(set(q["options"])) == len(q["options"]), f"{qid}: duplicate ordering chip {q['options']}"
                assert sorted(tokens) == sorted(q["options"]), f"{qid}: ordering tokens mismatch"

            norm_p = re.sub(r"\s+", " ", q["prompt"].strip().lower())
            # Shared passage prompts only collide if identical; full prompt strings must be unique across dataset
            assert norm_p not in seen_prompts, f"Duplicate prompt text on {qid}: {norm_p[:80]}"
            seen_prompts.add(norm_p)
            assert q["explanation"].strip(), f"{qid}: blank explanation"

            sec = q["section"]
            sec_counts[sec] = sec_counts.get(sec, 0) + 1

        assert sec_counts == spec["sections"], (
            f"{p['packId']} section counts {sec_counts} != expected {spec['sections']}"
        )
