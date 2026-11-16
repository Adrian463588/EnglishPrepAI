"""Extracts high-signal offline study chapters & public rubric digests from material_bank PDFs."""

import json
import re
from pathlib import Path
import pymupdf

ROOT = Path(__file__).resolve().parent.parent
BANK_DIR = ROOT / "material_bank"
EXTRACTED_DIR = BANK_DIR / "extracted"
ASSETS_BANK_DIR = ROOT / "app" / "src" / "main" / "assets" / "material_bank"

SKIP_HEADING_KEYWORDS = (
    "overview",
    "table of contents",
    "licensing",
    "new page",
    "introduce yourself",
    "detailed licensing",
    "glossary",
    "index",
    "autobiography",
    "about the book",
)

PREFERRED_TOPICS = (
    "grammar",
    "subject-verb",
    "verb",
    "tense",
    "perfect",
    "gerund",
    "infinitive",
    "reported speech",
    "indirect",
    "modal",
    "clause",
    "conjunctive",
    "comparative",
    "superlative",
    "passive",
    "pronoun",
    "article",
    "reading",
    "comprehension",
    "vocabulary",
    "writing",
    "argument",
    "paragraph",
    "genre",
    "listening",
    "pronunciation",
    "syllable",
    "stress",
    "coral",
    "glacier",
)


def clean_pdf_text(text: str) -> str:
    lines = []
    for raw in text.splitlines():
        s = raw.strip()
        if not s:
            continue
        if re.match(r"^\d+(\.\d+)*$", s):
            continue
        if "https://human.libretexts.org/@go/page/" in s:
            continue
        if "shared under a CC" in s or s.startswith("CC BY") or s.startswith("Access for free at"):
            continue
        if re.match(r"^(Barbier Lee|University of Kansas|San Jacinto College|Reedley College)", s):
            continue
        lines.append(s)
    return re.sub(r"\n{3,}", "\n\n", "\n".join(lines)).strip()


def score_heading(title: str, body: str) -> int:
    tl = title.lower()
    if any(k in tl for k in SKIP_HEADING_KEYWORDS):
        return -100
    score = sum(10 for k in PREFERRED_TOPICS if k in tl)
    if len(body) > 500:
        score += 5
    if body.count(".") >= 4:
        score += 5
    return score


def extract_sections_from_pdf(pdf_path: Path):
    doc = pymupdf.open(str(pdf_path))
    sections = []
    current_title = None
    current_buf = []
    start_page = 1
    scan_limit = min(doc.page_count, 260)
    skip_front = 8 if doc.page_count > 25 else 0

    for idx in range(skip_front, scan_limit):
        cleaned = clean_pdf_text(doc[idx].get_text("text"))
        if not cleaned:
            continue
        lines = cleaned.splitlines()
        first_line = lines[0] if lines else ""
        is_heading = bool(
            re.match(r"^(\d+(\.\d+)+[:.]?\s+\S|Chapter \d+|Unit \d+|Deep-Sea Corals|Abstract)", first_line, re.I)
            and "...." not in first_line
            and not any(k in first_line.lower() for k in SKIP_HEADING_KEYWORDS)
        )
        if is_heading and current_title != first_line:
            if current_title and current_buf:
                body = re.sub(r"\s+", " ", " ".join(current_buf)).strip()
                if len(body) >= 320:
                    sections.append({
                        "title": current_title[:110],
                        "pageRange": f"{start_page}-{idx}",
                        "body": body[:1400],
                        "score": score_heading(current_title, body),
                    })
            current_title = first_line[:110]
            start_page = idx + 1
            current_buf = lines[1:]
        else:
            if current_title is None:
                current_title = f"Study Digest ({pdf_path.stem.replace('_', ' ').title()})"
                start_page = idx + 1
            current_buf.append(cleaned)

    if current_title and current_buf:
        body = re.sub(r"\s+", " ", " ".join(current_buf)).strip()
        if len(body) >= 220:
            sections.append({
                "title": current_title[:110],
                "pageRange": f"{start_page}-{scan_limit}",
                "body": body[:1400],
                "score": score_heading(current_title, body),
            })

    good = [s for s in sections if s["score"] >= 0]
    good.sort(key=lambda x: x["score"], reverse=True)
    # Keep distinct titles preserving natural usefulness
    selected = []
    seen = set()
    for s in good:
        key = re.sub(r"[^a-z]", "", s["title"].lower())[:20]
        if key not in seen:
            seen.add(key)
            selected.append(s)
        if len(selected) >= 4:
            break

    if not selected and sections:
        selected = sections[:2]

    md_pages = []
    for idx in range(min(doc.page_count, 18)):
        txt = clean_pdf_text(doc[idx].get_text("text"))
        if txt:
            md_pages.append(f"## Page {idx + 1}\n\n{txt}")
    doc.close()
    return selected, "\n\n---\n\n".join(md_pages)


OFFICIAL_DIGESTS = {
    "MB-REF-ITP-01": (
        "TOEFL ITP Level 1 Test Structure & Timing",
        "5-8",
        "Official ETS Level 1 specification: Section 1 Listening Comprehension (50 questions, ~35 minutes: Part A short dialogues, "
        "Part B extended conversations, Part C mini-lectures); Section 2 Structure & Written Expression (40 questions, 25 minutes: "
        "1-15 Sentence Completion, 16-40 Error Identification); Section 3 Reading Comprehension (50 questions, 55 minutes across "
        "academic texts). Total: 140 questions in 115 minutes; scaled score range 310-677.",
    ),
    "MB-REF-IBT-01": (
        "TOEFL iBT 2026 Blueprint (Effective Jan 21, 2026)",
        "2-7",
        "Official ETS Jan 21, 2026 adaptive structure (~90 min): Reading 50 items (30 Academic Reading + 20 Daily Life & Complete "
        "the Words); Listening 47 items (25-29 Conversations & Choose a Response + 18-22 Lectures & Announcements); Writing 12 items "
        "(10 Build a Sentence + 1 Write an Email + 1 Write for an Academic Discussion); Speaking 11 items (7 Listen and Repeat + 4 Take an Interview).",
    ),
    "MB-REF-IBT-02": (
        "TOEFL iBT 2026 Score Reporting & CEFR Alignment (1.0-6.0)",
        "26-28",
        "Updated TOEFL iBT reports section and overall proficiency on a 1.0-6.0 half-band scale mapped directly to CEFR: "
        "Band 6.0 (C2) corresponds to legacy ~114-120; Band 5.0 (C1) ~ 95-106; Band 4.0 (B2) ~ 72-85; Band 3.0 (B1) ~ 44-57.",
    ),
    "MB-REF-IBT-03": (
        "TOEFL iBT Construct Framework & Writing/Speaking Rubrics",
        "8-62",
        "ETS Research Report RR-106 details machine scoring (Selected Response, Complete the Words C-test, Build a Sentence) alongside "
        "criterion-based evaluation for Write an Email, Academic Discussion, Listen & Repeat intelligibility, and Interview discourse coherence.",
    ),
    "MB-REF-IELTS-01": (
        "IELTS Academic Writing Band Descriptors (Task 1 & Task 2)",
        "1-9",
        "Public assessment criteria across 4 equal dimensions: Task Achievement (Task 1 requires a clear Overview of trends/comparisons "
        "without copying prompt phrases verbatim) / Task Response (Task 2 demands a consistent stance addressing all prompt parts); "
        "Coherence & Cohesion; Lexical Resource; Grammatical Range & Accuracy across Bands 1-9.",
    ),
    "MB-REF-IELTS-02": (
        "IELTS Speaking Public Band Descriptors (Parts 1, 2 & 3)",
        "1-4",
        "Evaluated equally across 4 dimensions: Fluency & Coherence (natural speech rate, cohesive linking, topic extension), "
        "Lexical Resource (accurate paraphrase and collocation), Grammatical Range & Accuracy, and Pronunciation "
        "(segmental clarity, rhythm, word/sentence stress, intonation).",
    ),
    "MB-REF-IELTS-03": (
        "IELTS Academic vs TOEFL iBT 2026 Construct Comparison",
        "10-16",
        "IELTS Partners comparative research (2026): IELTS Academic tests Listening (40 items across 4 parts, ~30m), "
        "Academic Reading (40 items across 3 long texts, 60m), Writing Task 1 (>=150 words) + Task 2 (>=250 words, 60m), "
        "and 3-part interactive Speaking (11-14m), reported on 0.5-increment Band 0-9 scores.",
    ),
}


def main():
    manifest = json.loads((BANK_DIR / "MANIFEST.json").read_text(encoding="utf-8"))
    EXTRACTED_DIR.mkdir(parents=True, exist_ok=True)
    ASSETS_BANK_DIR.mkdir(parents=True, exist_ok=True)

    app_entries = []
    readme_rows = []

    for src in manifest["sources"]:
        sid = src["id"]
        pdf_path = ROOT / src["localRelativePath"]
        if src["tier"] == "open_oer":
            chosen, md_full = extract_sections_from_pdf(pdf_path)
            md_path = EXTRACTED_DIR / f"{sid}.md"
            md_header = (
                f"# {src['title']}\n\n"
                f"- **ID**: `{sid}` | **Exam Scope**: `{src['examScope']}` | **Skill**: `{src['skill']}`\n"
                f"- **Author/Org**: {src['authorOrOrg']} | **License**: `{src['license']}`\n"
                f"- **PDF**: `{src['localRelativePath']}` ({src['pageCount']} pages)\n"
                f"- **Source URL**: {src['url']}\n\n---\n\n"
            )
            extracted_summary = "\n\n".join(
                f"## {sec['title']} (p. {sec['pageRange']})\n\n{sec['body']}" for sec in chosen
            )
            md_path.write_text(md_header + extracted_summary + "\n\n---\n\n" + md_full[:24000], encoding="utf-8")
            for idx, sec in enumerate(chosen, start=1):
                app_entries.append({
                    "id": f"{sid}-SEC-{idx}",
                    "sourceId": sid,
                    "examScope": src["examScope"],
                    "skill": src["skill"],
                    "sourceTitle": src["title"],
                    "chapterTitle": sec["title"],
                    "pageRange": sec["pageRange"],
                    "pdfFileName": src["fileName"],
                    "authorOrOrg": src["authorOrOrg"],
                    "license": src["license"],
                    "sourceUrl": src["url"],
                    "redistributable": True,
                    "excerpt": sec["body"],
                })
        else:
            title, page_range, body = OFFICIAL_DIGESTS[sid]
            md_digest = (
                f"# {src['title']}\n\n"
                f"- **Source ID**: `{sid}` | **Org**: {src['authorOrOrg']}\n"
                f"- **License**: `{src['license']}` (Specification/rubric metadata only; copyrighted sample items excluded)\n"
                f"- **URL**: {src['url']}\n\n## {title} (p. {page_range})\n\n{body}\n"
            )
            (EXTRACTED_DIR / f"{sid}.md").write_text(md_digest, encoding="utf-8")
            app_entries.append({
                "id": f"{sid}-SPEC-1",
                "sourceId": sid,
                "examScope": src["examScope"],
                "skill": src["skill"],
                "sourceTitle": src["title"],
                "chapterTitle": title,
                "pageRange": page_range,
                "pdfFileName": src["fileName"],
                "authorOrOrg": src["authorOrOrg"],
                "license": src["license"],
                "sourceUrl": src["url"],
                "redistributable": False,
                "excerpt": body,
            })

        readme_rows.append(
            f"| `{sid}` | {src['examScope']} | {src['skill']} | `{src['fileName']}` | {src['pageCount']} | {src['license']} | `{src['sha256'][:12]}` |"
        )

    out_json = {
        "schemaVersion": 1,
        "generatedAt": "2026-11-16",
        "totalPdfs": len(manifest["sources"]),
        "totalEntries": len(app_entries),
        "entries": app_entries,
    }
    out_bytes = (json.dumps(out_json, indent=2, ensure_ascii=False) + "\n").encode("utf-8")
    (ASSETS_BANK_DIR / "material_bank.json").write_bytes(out_bytes)
    (BANK_DIR / "material_bank.json").write_bytes(out_bytes)

    readme = (
        "# Material Bank — PDF Sources & Extracted Study Digest\n\n"
        "Koleksi 17 PDF hasil pencarian mendalam (OER CC-BY / Public Domain US Gov + Spesifikasi Resmi ETS & IELTS) beserta "
        "ekstraksi terstruktur ke `extracted/*.md` dan `app/src/main/assets/material_bank/material_bank.json` untuk "
        "konsumsi offline di aplikasi English Prep AI.\n\n"
        "## Daftar PDF Terverifikasi\n\n"
        "| ID | Ujian | Skill | File | Hal | Lisensi | SHA-256 (12) |\n"
        "|---|---|---|---|---:|---|---|\n"
        + "\n".join(readme_rows)
        + f"\n\nTotal: **{len(manifest['sources'])} PDF** ({sum(s['pageCount'] for s in manifest['sources'])} halaman) -> **{len(app_entries)}** bab/dokumen offline.\n"
    )
    (BANK_DIR / "README.md").write_text(readme, encoding="utf-8")
    print(f"Built refined material bank: {len(manifest['sources'])} PDFs -> {len(app_entries)} in-app entries ({len(out_bytes)/1024:.1f} KB JSON)")


if __name__ == "__main__":
    main()
