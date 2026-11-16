"""Unified TOEFL iBT 2026 full pack assembler (5 packs x 120 tasks = 600 items)."""

from bank_ibt.reading_words_daily import build_ibt_words_and_daily
from bank_ibt.reading_academic import build_ibt_academic_reading
from bank_ibt.listening_responses import build_ibt_choose_responses
from bank_ibt.listening_talks_1_3 import build_ibt_talks_1_3
from bank_ibt.listening_talks_4_5 import build_ibt_talks_4_5
from bank_ibt.writing_speaking_bank import build_ibt_writing_and_speaking

_META = {
    1: ("IBT26-01", "iBT 2026 Paket 1 — Campus Operations & Environmental Science", "Full 120-item Jan 2026 iBT simulation covering soil telemetry, biochar trials, thermal lakes, Build a Sentence, Email & Interview."),
    2: ("IBT26-02", "iBT 2026 Paket 2 — Digital Humanities & Urban Transit", "Full 120-item Jan 2026 iBT simulation spanning OCR archives, Roman hot-mix concrete, bioswales, Email & Discussion writing."),
    3: ("IBT26-03", "iBT 2026 Paket 3 — Biological Systems & Acoustic Photonics", "Full 120-item Jan 2026 iBT simulation exploring mycorrhizal fungi, subsea fiber seismics, optical mirrors, and Listen & Repeat."),
    4: ("IBT26-04", "iBT 2026 Paket 4 — Behavioral Economics & Food Systems", "Full 120-item Jan 2026 iBT simulation testing choice overload, fog-harvesting, solar cold-chains, cave echoes, and synthetic interviews."),
    5: ("IBT26-05", "iBT 2026 Paket 5 — Sleep Memory, Language & Ocean Ecology", "Full 120-item Jan 2026 iBT simulation covering sleep spindles, seaweed plastics, passive cetacean sonar, and all 2026 task formats."),
}


def _build_pack(num: int) -> dict:
    pack_id, title, theme = _META[num]
    p_wd, q_wd = build_ibt_words_and_daily(num)
    p_ac, q_ac = build_ibt_academic_reading(num)
    q_lr = build_ibt_choose_responses(num)
    q_lt = build_ibt_talks_1_3(num) if num <= 3 else build_ibt_talks_4_5(num)
    q_ws = build_ibt_writing_and_speaking(num)
    questions = q_wd + q_ac + q_lr + q_lt + q_ws
    assert len(questions) == 120, f"{pack_id}: got {len(questions)}"
    return {
        "packId": pack_id,
        "examId": "IBT_2026",
        "edition": "2026.2",
        "title": title,
        "theme": theme,
        "status": "READY",
        "blueprintCount": 120,
        "durationMinutes": 90,
        "blueprintDurationMinutes": 90,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": p_wd + p_ac,
        "questions": questions,
    }


IBT_PACKS = [_build_pack(i) for i in range(1, 6)]
