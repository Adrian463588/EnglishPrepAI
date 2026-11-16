"""Unified IELTS Academic full pack assembler (5 packs x 85 tasks = 425 items)."""

from bank_ielts.listening_1_3 import build_ielts_listening_1_3
from bank_ielts.listening_4_5 import build_ielts_listening_4_5
from bank_ielts.reading_bank_1_3 import build_ielts_reading_1_3
from bank_ielts.reading_bank_4_5 import build_ielts_reading_4_5
from bank_ielts.writing_speaking_bank import build_ielts_writing_and_speaking

_META = {
    1: ("IELTS-A-01", "IELTS Paket 1 — Housing, Energy & Cold-Climate Architecture", "Full 85-item IELTS Academic simulation spanning Campus Housing, Arboretum Volunteer, Dissertation Tutorial, Deep Sea Plankton, Solar Retrofits, Spanish Toldos & Consumer Energy Feedback."),
    2: ("IELTS-A-02", "IELTS Paket 2 — Marine Restoration, Design & Literacy Science", "Full 85-item IELTS Academic simulation covering Bike Rentals, Ceramic Museum, Botany Proposal, Salt Marsh Carbon, Oyster Reefs, Scandinavian Libraries & Phonics Neuroscience."),
    3: ("IELTS-A-03", "IELTS Paket 3 — Transit Heritage, Timber & Acoustic Ecology", "Full 85-item IELTS Academic simulation spanning Dorm Laundry, Canal Lock Trails, Engineering Capstone, Subterranean Bees, Arctic Cables, CLT Skyscrapers & Acoustic Reefs."),
    4: ("IELTS-A-04", "IELTS Paket 4 — Ancient Trade, Bioacoustics & Reflective Cities", "Full 85-item IELTS Academic simulation covering Lab Access, Woodland Bridge, Soil Microbiome, Glacial Mills, Roman Levantine Glass, Coral Playback & Thermochromic Roofs."),
    5: ("IELTS-A-05", "IELTS Paket 5 — Paleoclimate Ice, Corridors & Soundscapes", "Full 85-item IELTS Academic simulation spanning Kayak Rentals, Arboretum Fungi, Hydroponics Thesis, Migratory Whales, Alpine Ice Bubbles, Wildflower Verges & Acoustic Architecture."),
}


def _build_pack(num: int) -> dict:
    pack_id, title, theme = _META[num]
    q_list = build_ielts_listening_1_3(num) if num <= 3 else build_ielts_listening_4_5(num)
    p_read, q_read = build_ielts_reading_1_3(num) if num <= 3 else build_ielts_reading_4_5(num)
    q_ws = build_ielts_writing_and_speaking(num)
    questions = q_list + q_read + q_ws
    assert len(questions) == 85, f"{pack_id}: got {len(questions)}"
    return {
        "packId": pack_id,
        "examId": "IELTS_ACADEMIC",
        "edition": "2026.2",
        "title": title,
        "theme": theme,
        "status": "READY",
        "blueprintCount": 85,
        "durationMinutes": 165,
        "blueprintDurationMinutes": 165,
        "license": "Original",
        "provenance": "EnglishPrepAI-Original-2026",
        "passages": p_read,
        "questions": questions,
    }


IELTS_PACKS = [_build_pack(i) for i in range(1, 6)]
