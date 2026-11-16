"""Unified TOEFL ITP Level 1 full pack assembler (5 packs x 140 questions = 700 items)."""

from bank_itp.listening_part_a import build_itp_part_a_items
from bank_itp.listening_parts_bc import build_itp_parts_bc_items
from bank_itp.structure_bank import build_itp_structure_items
from bank_itp.reading_bank_1_3 import build_itp_reading_1_3
from bank_itp.reading_bank_4_5 import build_itp_reading_4_5

_META = {
    1: ("ITP-L1-01", "ITP Paket 1 — Campus Routines & Natural Systems", "Full 140-item TOEFL ITP simulation covering campus dialogues, marine/desert biology, inversions, and academic prose."),
    2: ("ITP-L1-02", "ITP Paket 2 — History, Architecture & Urban Ecology", "Full 140-item TOEFL ITP simulation across urban infrastructure, medieval crafts, restored waterways, and clause mastery."),
    3: ("ITP-L1-03", "ITP Paket 3 — Engineering, Acoustics & Glacial Systems", "Full 140-item TOEFL ITP simulation testing concert hall acoustics, glacier dynamics, infrasound, and parallel syntax."),
    4: ("ITP-L1-04", "ITP Paket 4 — Marine Conservation & Monetary History", "Full 140-item TOEFL ITP simulation spanning kelp trophic cascades, oyster breakwaters, touchstone currency history, and grammar."),
    5: ("ITP-L1-05", "ITP Paket 5 — Astronomy, Scripts & Urban Policy", "Full 140-item TOEFL ITP simulation examining exoplanet spectroscopy, Linear B/alphabet origins, and zoning reform."),
}


def _build_pack(num: int) -> dict:
    pack_id, title, theme = _META[num]
    l_a = build_itp_part_a_items(num)
    l_bc = build_itp_parts_bc_items(num)
    struct = build_itp_structure_items(num)
    passages, reading = build_itp_reading_1_3(num) if num <= 3 else build_itp_reading_4_5(num)
    questions = l_a + l_bc + struct + reading
    assert len(questions) == 140, f"{pack_id}: got {len(questions)}"
    return {
        "packId": pack_id,
        "examId": "ITP_L1",
        "edition": "2026.2",
        "title": title,
        "theme": theme,
        "status": "READY",
        "blueprintCount": 140,
        "durationMinutes": 115,
        "blueprintDurationMinutes": 115,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": passages,
        "questions": questions,
    }


ITP_PACKS = [_build_pack(i) for i in range(1, 6)]
