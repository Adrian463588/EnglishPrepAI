# Implementation Plan: Material Bank & Full-Length 15 Exam Packs (`008-material-bank-full-packs`)

**Goal**: Acquire and parse a real, license-audited `material_bank/` via deep PDF search/dorking, expose it offline inside the Android app + FTS4 AI Tutor, expand the curriculum to 30 bilingual modules, and populate all 15 exam packages (ITP Level 1, iBT 2026, IELTS Academic) to full official blueprint lengths (`1,725` total items, status `READY`) cleanly within APK/UX budgets.

**Architecture**: Clean Python asset pipeline (`tools/fetch_material_bank.py`, `tools/build_material_bank.py`, modular `tools/bank_{itp,ibt,ielts}/`, `tools/generate_assets.py`) produces verified JSON + 16 kHz IMA-ADPCM WAV assets bundled under `app/src/main/assets/`. On Android, `AssetSeeder` performs a version-gated transactional `@Upsert` preserving all user history, `LearnScreens` exposes extracted Material Bank documents alongside the 30 modules, `ExamViewModel` keeps shared-audio tracks continuous across grouped questions, and `ExamScreen` upgrades the navigator sheet for 140-item ergonomics.

**Tech Stack**: Python 3.12 (`pymupdf/fitz`, `requests`, `wave`, `audioop`, `System.Speech`), Kotlin, Jetpack Compose Material 3, Room + FTS4, Media3 ExoPlayer `1.5.1`, Hilt, DataStore.

## Global Constraints
- Zero copyrighted ETS/Cambridge test items or proprietary audio redistributed in Git or APK (`AGENTS.md` Rule 8).
- Strictly minimal Kotlin diffs (`ponytail`), SOLID/DRY/KISS/YAGNI, zero speculative abstractions.
- Zero AI slop UI clutter; maintain `>= 48dp` targets and `200%` font scale legibility.
- Only mark checklist items `[x]` after live observed verification on latest code/build.

## Review Focus (Top 5 Failure Modes)
1. **Downloaded PDF License Violation or Broken Binary**: Verify PDF magic bytes `%PDF-`, inspect opening 5 pages for CC-BY / CC-BY-SA / OER / All-Rights-Reserved markers before allowing extraction or Git tracking.
2. **APK Size Explosion from Uncompressed PCM WAV**: Encode Listening speech tracks as 16 kHz 4-bit MS IMA-ADPCM (`0x0011`, `nBlockAlign=256`, `wSamplesPerBlock=505`) and test real ExoPlayer `Media3AudioEngine` playback on `SM-G988B`.
3. **140-Item Modal Navigator Overflow**: Replace non-scrollable `FlowRow` in `ExamScreen` with section-filtered scrollable `LazyColumn` chunked 5-column rows.
4. **Listening Part Resetting Between Sibling Questions**: Compare `nextQuestion.audioAssetId` with active asset ID in `ExamViewModel` before stopping/re-preparing ExoPlayer so multi-question Listening parts play continuously.
5. **Upgrading Users Not Getting Full Packs (`AssetSeeder` Early Return)**: Key `AssetSeeder` off a DataStore `bundled_content_version` integer + catalog pack readiness instead of `countExamProfiles() >= 3`.

---

## Phased Tasks
- **Phase 1 — Deep PDF Dorking, Download & Material Bank Extractor (`material_bank/`)**
  - Create `tools/fetch_material_bank.py` & `tools/build_material_bank.py`.
  - Download open-licensed OER grammar/reading/writing/listening PDFs (`material_bank/open_oer_pdfs/`) + official specification/band-descriptor PDFs (`material_bank/official_reference_pdfs/` gitignored binary, manifest tracked).
  - Verify SHA-256, licenses, extract chapters into `material_bank/extracted/` and compact runtime index `app/src/main/assets/material_bank/material_bank.json`.
- **Phase 2 — Curriculum Expansion (30 Modules, >=90 Drills)**
  - Expand `tools/curriculum_data.py` to 10 modules per exam (`ITP-MOD-01..10`, `IBT-MOD-01..10`, `IELTS-MOD-01..10`) grounded in Material Bank OER chapters + official 2026 blueprints.
- **Phase 3 — Full Blueprint Content Authoring (`1,725` Items Across 15 Packs)**
  - Modularize `tools/bank_itp/`, `tools/bank_ibt/`, `tools/bank_ielts/` + update `tools/packs_*.py` to export `5 x 140` ITP, `5 x 120` iBT 2026, `5 x 85` IELTS Academic (`status = "READY"`).
  - Generate shared & per-turn 16 kHz IMA-ADPCM WAV audio via `tools/generate_assets.py` + verify strict uniqueness/schema/blueprint integrity gates in Python.
- **Phase 4 — Android Runtime, UI & Seeder Updates**
  - Update `UserPreferencesRepository` (`bundledContentVersion`), `AssetSeeder` (version-gated transactional `@Upsert` + Material Bank FTS indexing), `ExamCatalogRepositoryImpl` (observe/load Material Bank entries cleanly via JSON asset), `LearnViewModel` / `LearnScreens` ("Bank Materi OER & Panduan PDF" expandable/readable library section), `ExamViewModel` (continuous shared-audio guard), and `ExamScreen` (scrollable section-filtered 140-item Navigator Sheet).
- **Phase 5 — Verification, Physical Device QA, Docs & GitHub Sync**
  - Update & run ` .\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease`.
  - Deploy Release R8 APK to physical `SM-G988B`, test Listening IMA-ADPCM playback + 140-item navigation + Material Bank browsing, refresh affected screenshots in `docs/screenshots/`.
  - Update `docs/qa/EPIC_STATUS.md`, `docs/qa/LICENSE_AND_SBOM_AUDIT.md`, `docs/VERIFICATION_CHECKLIST.md`, `CHANGELOG.md`, `README.md`, commit & push cleanly to `Adrian463588/EnglishPrepAI`, and sync GitHub Release if warranted (`v1.1.0` or updated release notes).
