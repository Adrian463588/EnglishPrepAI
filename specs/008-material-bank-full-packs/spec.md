# Feature Specification: Material Bank (PDF Scraping/Dorking & OER Extraction) + Full-Length Exam Packs (1,725 Items)

**Feature Branch**: `008-material-bank-full-packs`
**Created**: 2026-11-16
**Status**: Approved for Execution
**Supersedes Sample Limits**: Elevates `E03..E07` & `E04-08/E06-07/E07-02/E07-04` from `12`-item Sample packs (`180` total) to Full Blueprint `READY` packs (`1,725` total items across `15` packs) and expands `18` curriculum modules to `30` OER-backed Material Bank modules (`>= 90` drills).

## Invariants
- **License & Provenance Integrity (`AGENTS.md` Rule 8)**: Never scrape, bundle, or redistribute copyright-protected ETS actual test questions/audio or Cambridge IELTS test papers inside Git or the APK. Downloaded open-licensed OER/CC-BY/CC-BY-SA/Public Domain PDFs are verified and stored in `material_bank/open_oer_pdfs/`, while copyrighted official specifications/handbooks downloaded locally during audit reside in `.gitignore`d `material_bank/official_reference_pdfs/` with tracked URL/SHA-256 metadata and only public factual rubric/blueprint tables cited cleanly.
- **Exact Blueprint Parity (`PRD.md`, `packs/index.json`)**:
  - TOEFL ITP Level 1 (`ITP-L1-01..05`): `140` questions per pack (`50` Listening, `40` Structure & Written Expression, `50` Reading), `115` min duration, `status = "READY"`.
  - TOEFL iBT 2026 (`IBT26-01..05`, post-Jan 21, 2026 ETS blueprint): `120` items per pack (`50` Reading, `47` Listening, `12` Writing, `11` Speaking), `90` min duration, `status = "READY"`.
  - IELTS Academic (`IELTS-A-01..05`): `85` items per pack (`40` Listening across 4 parts, `40` Reading across 3 passages, `2` Writing tasks, `3` Speaking parts), `165` min duration, `status = "READY"`.
- **Zero Duplicate / Placeholder Slop**: Every pack item must have a unique ID, distinct non-templated academic/dialogue content, valid `rationaleSource`, objective key (for objective tasks), rubric/constraints (for subjective tasks), and verifiable Indonesian-first explanation.
- **APK Budget & Audio Fidelity**: Bundled speech audio uses standard RIFF IMA-ADPCM WAV (`16 kHz`, 4-bit mono, `~8 KB/s`, decoded natively by Media3 `ImaAdPcmOutputWriter`) so full listening sections across all 15 packs remain clean and offline without bloating the APK.
- **Non-Destructive Upgrade (`AssetSeeder`)**: Upgrading bundled content from v1 (`180` sample items) to v2 (`1,725` full items + `30` modules + `material_bank.json`) must `@Upsert` catalog data in a transaction without erasing user `ExamSession`, `UserAnswer`, `StudyProgress`, `ChatThread`, or `ModelInstall`.

## Functional Requirements
- **FR-026 (Material Bank Pipeline & Storage)**: Create `tools/fetch_material_bank.py` & `tools/build_material_bank.py` backed by deep PDF dorking/search results, downloading real open-access/OER and official reference PDFs into `material_bank/`, generating `material_bank/MANIFEST.json` (URL, SHA-256, license, pages, category), and extracting clean offline chapters into `app/src/main/assets/material_bank/material_bank.json`.
- **FR-027 (In-App Material Bank & Expanded Curriculum)**: Expand curriculum to `30` bilingual modules (`10` per exam, `>= 90` drills) linked directly to `material_bank` OER sources; surface searchable offline Material Bank documents inside `LearnScreen` and index all Material Bank abstracts into `curriculum_fts` for local AI Tutor retrieval.
- **FR-028 (Full-Length 15 Packs & Listening Audio)**: Build modular, DRY content banks (`tools/bank_itp/`, `tools/bank_ibt/`, `tools/bank_ielts/`) generating `5 x 140` ITP, `5 x 120` iBT 2026, and `5 x 85` IELTS Academic packs (`1,725` items total, `status = "READY"`), plus synthesized IMA-ADPCM RIFF WAV audio files for every Listening part/turn and Listen-and-Repeat reference.
- **FR-029 (Scalable Exam UX for 85–140 Items)**: Make `ExamScreen`'s question navigator scrollable with section filter chips; keep shared multi-question audio (e.g., Part B/C, iBT Conversations/Talks, IELTS Parts 1–4) playing seamlessly across sibling questions in `ExamViewModel` without resetting position or charging duplicate Strict replays.
- **FR-030 (Strict Quality Gates & Documentation Sync)**: Pass Python integrity audit (`0` duplicate IDs/prompts, exact section totals), JUnit/Robolectric tests (`testDebugUnitTest`), `lintDebug`, R8 `assembleRelease`, physical Android device smoke QA (`SM-G988B`), updated `README.md` / `EPIC_STATUS.md` / `VERIFICATION_CHECKLIST.md` / GitHub commit+push.
