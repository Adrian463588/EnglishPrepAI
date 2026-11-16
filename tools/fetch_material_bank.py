"""Downloads open-licensed OER / Public Domain PDFs and official exam spec PDFs into material_bank/."""

import hashlib
import json
from pathlib import Path
import pymupdf
import requests

ROOT = Path(__file__).resolve().parent.parent
BANK_DIR = ROOT / "material_bank"
OPEN_DIR = BANK_DIR / "open_oer_pdfs"
REF_DIR = BANK_DIR / "official_reference_pdfs"

SOURCES = [
    # --- Tier 1: Open Educational Resources / CC-BY / Public Domain (Redistributable in Git & Extracted to App) ---
    {
        "id": "MB-OER-GRAM-01",
        "tier": "open_oer",
        "examScope": "ALL",
        "skill": "STRUCTURE",
        "title": "High-Intermediate Academic Grammar for ESL Students",
        "authorOrOrg": "Rebecca Al Haider / LibreTexts",
        "license": "CC BY 4.0",
        "fileName": "libretexts_academic_grammar_esl_ccby.pdf",
        "url": "https://batch.libretexts.org/print/Letter/Finished/human-308196/Full.pdf",
    },
    {
        "id": "MB-OER-GRAM-02",
        "tier": "open_oer",
        "examScope": "ITP_L1",
        "skill": "STRUCTURE",
        "title": "ESOL Advanced Grammar (Academic Clauses & Inversion)",
        "authorOrOrg": "Allyson Marceau / LibreTexts OER",
        "license": "CC BY-NC-SA 4.0",
        "fileName": "libretexts_esol_advanced_grammar_oer.pdf",
        "url": "https://batch.libretexts.org/print/Letter/Finished/human-273114/Full.pdf",
    },
    {
        "id": "MB-OER-GRAM-03",
        "tier": "open_oer",
        "examScope": "ALL",
        "skill": "STRUCTURE",
        "title": "ESL Grammar The Way You Like It (Sentence Mastery)",
        "authorOrOrg": "Don Bissonnette / LibreTexts OER",
        "license": "CC BY-NC-SA 4.0",
        "fileName": "libretexts_esl_grammar_way_you_like_it.pdf",
        "url": "https://batch.libretexts.org/print/Letter/Finished/human-108047/Full.pdf",
    },
    {
        "id": "MB-OER-WRRD-01",
        "tier": "open_oer",
        "examScope": "IBT_2026",
        "skill": "WRITING",
        "title": "Intermediate Academic Reading and Writing Workbook",
        "authorOrOrg": "LibreTexts OER",
        "license": "CC BY 4.0",
        "fileName": "libretexts_intermediate_reading_writing.pdf",
        "url": "https://batch.libretexts.org/print/Letter/Finished/human-112829/Full.pdf",
    },
    {
        "id": "MB-OER-READ-01",
        "tier": "open_oer",
        "examScope": "ALL",
        "skill": "READING",
        "title": "ESL 304: Intermediate-High Academic Reading & Vocabulary",
        "authorOrOrg": "LibreTexts OER",
        "license": "CC BY 4.0",
        "fileName": "libretexts_esl304_reading_vocab_ccby.pdf",
        "url": "https://batch.libretexts.org/print/Letter/Finished/human-296503/Full.pdf",
    },
    {
        "id": "MB-OER-WRITE-01",
        "tier": "open_oer",
        "examScope": "IBT_2026",
        "skill": "WRITING",
        "title": "Developing & Teaching Academic Writing Courses (Module 1)",
        "authorOrOrg": "Iowa State University / AE E-Teacher",
        "license": "CC BY 4.0",
        "fileName": "iastate_academic_writing_mod1_ccby.pdf",
        "url": "https://s3.us-east-1.amazonaws.com/resources.aeeteacher.org/COP/Teaching%20English%20Academic%20Writing%20MOOC%20Module%20Packets/TEAW%20Mod%201_Downloadable%20Packet.pdf",
    },
    {
        "id": "MB-OER-LSPK-01",
        "tier": "open_oer",
        "examScope": "IELTS_ACADEMIC",
        "skill": "LISTENING",
        "title": "Listening and Speaking Strategies for English Language Learners",
        "authorOrOrg": "LibreTexts OER",
        "license": "CC BY 4.0",
        "fileName": "libretexts_listening_speaking_ccby.pdf",
        "url": "https://batch.libretexts.org/print/Letter/Finished/human-172143/Full.pdf",
    },
    {
        "id": "MB-OER-SCI-01",
        "tier": "open_oer",
        "examScope": "ALL",
        "skill": "READING",
        "title": "Deep-Sea Corals Fact Sheet (Public Domain Science Source)",
        "authorOrOrg": "NOAA Ocean Exploration",
        "license": "Public Domain (US Gov)",
        "fileName": "noaa_deep_sea_corals_factsheet_pd.pdf",
        "url": "https://oceanexplorer.noaa.gov/wp-content/uploads/2025/04/deep-sea-corals-fact-sheet.pdf",
    },
    {
        "id": "MB-OER-SCI-02",
        "tier": "open_oer",
        "examScope": "ALL",
        "skill": "READING",
        "title": "Glacier Ice Mass Fluctuations & Fault Activity Summary",
        "authorOrOrg": "NASA Technical Reports Server (NTRS)",
        "license": "Public Domain (US Gov / NASA)",
        "fileName": "nasa_glacier_ice_mass_fluctuations_pd.pdf",
        "url": "https://ntrs.nasa.gov/api/citations/20040030573/downloads/20040030573.pdf?attachment=true",
    },
    {
        "id": "MB-OER-LST-02",
        "tier": "open_oer",
        "examScope": "ALL",
        "skill": "LISTENING",
        "title": "Strategies for Developing Listening Skills in Academic Contexts",
        "authorOrOrg": "USA Journals Open Access",
        "license": "CC BY 4.0",
        "fileName": "usajournals_listening_strategies_ccby.pdf",
        "url": "https://usajournals.org/index.php/6/article/download/60/90/148",
    },
    # --- Tier 2: Official Exam Spec, Descriptors & Handbooks (Local Reference Only, Copyrighted) ---
    {
        "id": "MB-REF-ITP-01",
        "tier": "official_reference",
        "examScope": "ITP_L1",
        "skill": "ALL",
        "title": "TOEFL ITP Test Taker Handbook",
        "authorOrOrg": "Educational Testing Service (ETS)",
        "license": "ETS Reference Only (All Rights Reserved)",
        "fileName": "ets_toefl_itp_test_taker_handbook.pdf",
        "url": "https://www.ets.org/pdfs/toefl-itp-test-taker-handbook.pdf",
    },
    {
        "id": "MB-REF-IBT-01",
        "tier": "official_reference",
        "examScope": "IBT_2026",
        "skill": "ALL",
        "title": "TOEFL iBT Test: 2026 Update Specifications & Blueprint",
        "authorOrOrg": "Educational Testing Service (ETS)",
        "license": "ETS Reference Only (All Rights Reserved)",
        "fileName": "ets_toefl_ibt_specifications_2026.pdf",
        "url": "https://www.pt.ets.org/content/dam/ets-org/pdfs/toefl/toefl-ibt-test-specifications-2026.pdf",
    },
    {
        "id": "MB-REF-IBT-02",
        "tier": "official_reference",
        "examScope": "IBT_2026",
        "skill": "ALL",
        "title": "Updated TOEFL iBT Test Overview & 1-6 Scoring Concordance",
        "authorOrOrg": "Educational Testing Service (ETS)",
        "license": "ETS Reference Only (All Rights Reserved)",
        "fileName": "ets_toefl_ibt_overview_2026.pdf",
        "url": "https://www.eu.ets.org/pdfs/toefl/toefl-ibt-test-overview.pdf",
    },
    {
        "id": "MB-REF-IBT-03",
        "tier": "official_reference",
        "examScope": "IBT_2026",
        "skill": "ALL",
        "title": "TOEFL iBT Technical Manual (TOEFL-RR-106 / ERIC EJ1487502)",
        "authorOrOrg": "Manna et al. / ETS Research Series (ERIC)",
        "license": "ETS / ERIC Public Research Reference",
        "fileName": "eric_ej1487502_toefl_ibt_technical_manual.pdf",
        "url": "https://files.eric.ed.gov/fulltext/EJ1487502.pdf",
    },
    {
        "id": "MB-REF-IELTS-01",
        "tier": "official_reference",
        "examScope": "IELTS_ACADEMIC",
        "skill": "WRITING",
        "title": "IELTS Writing Band Descriptors (Public Assessment Criteria)",
        "authorOrOrg": "IELTS Partners (British Council, IDP, Cambridge)",
        "license": "IELTS Official Public Guide (Reference Only)",
        "fileName": "ielts_writing_band_descriptors.pdf",
        "url": "https://ielts.org/cdn/Guides/ielts-writing-band-descriptors.pdf",
    },
    {
        "id": "MB-REF-IELTS-02",
        "tier": "official_reference",
        "examScope": "IELTS_ACADEMIC",
        "skill": "SPEAKING",
        "title": "IELTS Speaking Band Descriptors (Public Assessment Criteria)",
        "authorOrOrg": "IELTS Partners (British Council, IDP, Cambridge)",
        "license": "IELTS Official Public Guide (Reference Only)",
        "fileName": "ielts_speaking_band_descriptors.pdf",
        "url": "https://ielts.org/cdn/ielts-guides/ielts-speaking-band-descriptors.pdf",
    },
    {
        "id": "MB-REF-IELTS-03",
        "tier": "official_reference",
        "examScope": "IELTS_ACADEMIC",
        "skill": "ALL",
        "title": "IELTS Academic & TOEFL iBT Score Concordance (Cushing 2026)",
        "authorOrOrg": "IELTS Research Reports Online Series",
        "license": "IELTS Official Public Research (Reference Only)",
        "fileName": "ielts_toefl_concordance_2026.pdf",
        "url": "https://ielts.org/cdn/ielts-downloadable-assets/ielts-research/ielts-research-reports/ielts-academic-toefl-ibt-score-concordance-content-comparison-cushing-2026.pdf",
    },
]


def download_all():
    OPEN_DIR.mkdir(parents=True, exist_ok=True)
    REF_DIR.mkdir(parents=True, exist_ok=True)
    headers = {"User-Agent": "Mozilla/5.0 (compatible; EnglishPrepAI-MaterialCollector/1.0)"}

    manifest = []
    for src in SOURCES:
        out_dir = OPEN_DIR if src["tier"] == "open_oer" else REF_DIR
        dest = out_dir / src["fileName"]
        if not dest.exists() or dest.stat().st_size < 1024:
            print(f"Downloading {src['id']} -> {src['url']}")
            try:
                r = requests.get(src["url"], headers=headers, timeout=45, allow_redirects=True)
                r.raise_for_status()
                if not r.content.startswith(b"%PDF"):
                    print(f"  WARNING: {src['id']} did not return a PDF (first bytes={r.content[:32]!r}); skipping.")
                    continue
                dest.write_bytes(r.content)
            except Exception as exc:
                print(f"  ERROR downloading {src['id']}: {exc}")
                continue

        raw = dest.read_bytes()
        sha = hashlib.sha256(raw).hexdigest()
        doc = pymupdf.open(str(dest))
        page_count = doc.page_count
        sample_text = " ".join(doc[i].get_text("text")[:400] for i in range(min(3, page_count)))
        doc.close()

        entry = {
            **src,
            "localRelativePath": str(dest.relative_to(ROOT)).replace("\\", "/"),
            "byteSize": len(raw),
            "sha256": sha,
            "pageCount": page_count,
            "redistributable": src["tier"] == "open_oer",
            "previewExcerpt": " ".join(sample_text.split())[:350],
        }
        manifest.append(entry)
        print(f"  OK: {dest.name} | {len(raw)/1048576:.2f} MB | {page_count} pages | {sha[:12]}")

    manifest_path = BANK_DIR / "MANIFEST.json"
    manifest_path.write_text(json.dumps({"sources": manifest}, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"\nSaved {len(manifest)} verified PDFs to {manifest_path}")


if __name__ == "__main__":
    download_all()
