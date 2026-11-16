# QA & EPIC Traceability Ledger (`ENGLISH_PREP_EPIC_CHECKLIST`)

- **Project**: English Prep AI (`com.englishprep.ai`, `v1.0.0`, `versionCode = 1`)
- **Audit Date**: `2026-11-16`
- **Tested Hardware**: Samsung Galaxy `SM-G988B` (Android 13, `arm64-v8a`, `1080x2400`)
- **Automated Verification**: `34/34` JVM/Robolectric tests pass (`0` skipped), `lintDebug` (`0` errors), R8 `assembleRelease` (`mapping.txt` verified, `154.4 MB`).

> Rule: Items requiring user-imported multi-GB `.litertlm`/`.onnx` weights or full 140/120/85-question copyrighted/external blueprint sets remain `[ ]` (`PARTIAL` / `BLOCKED_EXTERNAL`) until those external artifacts are supplied and benchmarked.

---

## Executive Summary by Epic

| Epic | Title | Priority | Status | Primary Evidence Path | Tested Env / API | Defects / Remaining Notes |
|---|---|---|---|---|---|---|
| EPIC-00 | SDD, Governance & Quality Gates | P0 | VERIFIED | `.specify/memory/constitution.md`, `specs/001`..`007`, `.github/workflows/android-devsecops.yml`, `docs/qa/LICENSE_AND_SBOM_AUDIT.md` | Windows + JDK 17 + GitHub CI | None |
| EPIC-01 | Android Foundation & Clean MVVM | P0 | VERIFIED | `app/src/main/java/com/englishprep/ai/**`, `MainActivity.kt` | `SM-G988B` API 33 + JVM | None |
| EPIC-02 | Navigation, Home & Exam Selector | P0 | VERIFIED | `HomeScreen.kt`, `HomeViewModel.kt`, `UiAndAccessibilityTest.kt`, `docs/screenshots/01-home.png`, `02-exam-switcher.png` | `SM-G988B` API 33 + Robolectric | None |
| EPIC-03 | Offline Learning Catalog & Lesson Player | P0 | VERIFIED | `LearnScreens.kt`, `LearnViewModel.kt`, `UserPreferencesRepository.kt`, `docs/screenshots/03-learn-modules.png`, `04-module-detail.png` | `SM-G988B` API 33 + Robolectric | None |
| EPIC-04 | Materi TOEFL ITP Level 1 | P0 | PARTIAL | `content/CURRICULUM.md`, `packs/ITP-L1-01..05.json`, `docs/screenshots/05-practice-packs.png` | `SM-G988B` API 33 + `AssetSeederAndFtsTest` | Sample 12/140 items/pack (`E04-08` Full 50/40/50 ready requires full-pack authoring) |
| EPIC-05 | Materi TOEFL iBT (2026) | P0 | PARTIAL | `packs/IBT26-01..05.json`, `ExamScoreEstimator.kt`, `ToeflSessionFlowIntegrationTest.kt` | `SM-G988B` API 33 + JVM | Sample 12/120 items/pack; non-adaptive raw metrics with explicit disclaimers |
| EPIC-06 | Materi IELTS Academic | P0 | PARTIAL | `packs/IELTS-A-01..05.json`, `QuizGradingEngineTest.kt` | `SM-G988B` API 33 + JVM | Sample 12/85 items/pack (`E06-07` 40/40/2/3 full ready requires full-pack authoring) |
| EPIC-07 | Original Content, Bank Soal & 15 Packs | P0 | PARTIAL | `packs/index.json`, `PackIntegrityValidator.kt`, `AssetSeederAndFtsTest.kt` | JVM + `SM-G988B` | All 15 manifests valid & honestly labeled `SAMPLE` (`E07-02`, `E07-08`) |
| EPIC-08 | Quiz Engine & Response Widgets | P0 | VERIFIED | `QuizGradingEngine.kt`, `QuizGradingEngineTest.kt`, `ExamScreen.kt`, `RoomIntegrityAndRecoveryTest.kt` | JVM + Robolectric + `SM-G988B` | None |
| EPIC-09 | Media3 Audio & Listening | P0 | VERIFIED | `Media3AudioEngine.kt`, `ExamScreen.kt`, `docs/screenshots/06-exam-listening.png` | `SM-G988B` API 33 (R8 Release) | `00:00 -> 00:03/00:11` WAV verified + Practice transcript toggle |
| EPIC-10 | Speaking Engine | P1 | PARTIAL | `WavAudioRecorder.kt`, `WhisperOnnxSpeechEngine.kt`, `SpeakingMetricsAnalyzerTest.kt`, `ExamScreen.kt` | JVM + API 33 compile/audio | Live Whisper `.onnx` SAF transcription requires external ~150 MB model import |
| EPIC-11 | Writing Engine | P1 | VERIFIED | `ExamScreen.kt`, `RubricFeedbackAnalyzer`, `QuizGradingEngineTest.kt` | JVM + Robolectric | Local autosave + live word count + non-official heuristic/LLM rubric |
| EPIC-12 | Mock Test, Timer & Session Recovery | P0 | VERIFIED | `ExamViewModel.kt`, `RoomIntegrityAndRecoveryTest.kt`, `ExamScreen.kt` | Robolectric DB reopen + `SM-G988B` | Full-length mock stays gated behind `PackStatus.READY` |
| EPIC-13 | Scoring & Explanations | P0 | VERIFIED | `ExamScoreEstimator.kt`, `ExamScoreEstimatorTest.kt`, `ResultsScreen.kt`, `docs/screenshots/07-results.png` | JVM + `SM-G988B` API 33 | Raw deterministic metrics + `policyVersion` + skipped count verified |
| EPIC-14 | Room, DataStore & Data Integrity | P0 | VERIFIED | `EnglishPrepDatabase.kt`, `Daos.kt` (`@Upsert`), `app/schemas/.../1.json`, `RoomIntegrityAndRecoveryTest.kt` | Robolectric SQLite file reopen | `@Upsert` fix verified against `ON DELETE CASCADE` wipe |
| EPIC-15 | Progress Analytics & Review System | P0 | VERIFIED | `ProgressViewModel.kt`, `ProgressScreen.kt`, `UiAndAccessibilityTest.kt`, `docs/screenshots/08-progress-analytics.png` | `SM-G988B` + Robolectric | Automatic weak module update verified on `ITP-MOD-02..05` |
| EPIC-16 | SAF, Offline Content & Model Manager | P0/P1 | VERIFIED | `SafSecurityValidator.kt`, `SafSecurityValidatorTest.kt`, `AiAndImportRepositoryImpl.kt`, `ProfileScreen.kt` | JVM (5/5 SAF security tests) + `SM-G988B` | Zip-slip, 0-byte, GGUF mismatch, disk budget & SHA-256 verified |
| EPIC-17 | Gemma 4 Local Inference Engine | P1 | PARTIAL | `LiteRtLmGemma4Engine.kt`, `ProfileScreen.kt`, `docs/REFERENCES.md` | Compiled `litertlm:0.13.1` + R8 Release | `E17-04`/`E17-05` real `.litertlm` latency/tok-s requires external ~2.6 GB user SAF import |
| EPIC-18 | AI Tutor Chatbot + Local RAG | P1 | VERIFIED | `ChatViewModel.kt`, `ChatScreen.kt`, `GroundedTutorPromptBuilder.kt`, `RagPromptSanitizerTest.kt`, `docs/screenshots/09-ai-chat-rag.png` | JVM + Robolectric + `SM-G988B` | Prompt injection guard, FTS4 `[ID]` citations, Clear chat & Strict guard verified |
| EPIC-19 | Profile, Preferences, Reminders & Privacy | P0/P2 | VERIFIED | `ProfileScreen.kt`, `UserPreferencesRepository.kt`, `StudyReminderWorker.kt`, `docs/screenshots/10-profile-ai-saf.png` | Robolectric + `SM-G988B` | OSS licenses modal & trademark disclaimer added |
| EPIC-20 | UI Design System & Anti-AI-Slop Audit | P0 | VERIFIED | `Theme.kt`, `Tokens.kt`, `AppComponents.kt`, `HomeScreen.kt`, `UiAndAccessibilityTest.kt` | Robolectric (`2.0f` font, light/dark, 48dp) + `SM-G988B` | Kicker removed on Home, single Primary CTA, shared `SelectableOptionRow` |
| EPIC-21 | Security, Privacy, QA & Release | P0/P1 | VERIFIED | `proguard-rules.pro`, `AndroidAppIntegrityInspector.kt`, `SecurityHardeningTest.kt`, `docs/screenshots/11-profile-security-r8.png` | JVM + R8 `assembleRelease` + `SM-G988B` | `allowBackup=false`, zero `INTERNET`, R8 `307 ms` cold start verified; MobSF/Frida external tooling pending |

---

## Detailed Item Verification Checklist (`EPIC-00` .. `EPIC-21`)

### EPIC-00 - SDD, governance, dan quality gates
- [x] **E00-01**: AGENTS, PRD, DESIGN, ARSITEKTUR, DATA_SCHEMA, `content/CURRICULUM.md`, dan `packs/index.json` sinkron (`specs/001`..`007`).
- [x] **E00-02**: Pipeline Spec Kit (`spec.md`, `plan.md`, `tasks.md`, `acceptance.md`) dijalankan per fitur (`001`..`007`).
- [x] **E00-03**: Tinjauan BMAD tersimpan di `docs/reviews/bmad-*.md` tanpa menduplikasi backlog `tasks.md`.
- [x] **E00-04**: Peta traceability tersedia pada tabel matriks di dokumen ini (`docs/qa/EPIC_STATUS.md`).
- [x] **E00-05**: CI workflow `.github/workflows/android-devsecops.yml` + eksekusi lokal `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleRelease`.
- [x] **E00-06**: Audit SBOM/lisensi tersedia di `docs/qa/LICENSE_AND_SBOM_AUDIT.md`; 0 rahasia/keystore di Git (`git ls-files`).
- [x] **E00-07**: Seluruh perubahan UI dilengkapi 11 tangkapan layar aktual dari `SM-G988B` di `docs/screenshots/`.
- [x] **E00-08**: Gate kepatuhan bukti ditegakkan; item eksternal berat tetap berstatus `[ ]`.

### EPIC-01 - Android foundation & Clean MVVM
- [x] **E01-01**: Kotlin `2.3.0` + Jetpack Compose BOM `2025.02.00` + Material 3 teruji di ARM64 (`SM-G988B`).
- [x] **E01-02**: Typed Navigation Compose (`AppRoute`) dengan back stack dan transisi terarah (`MainActivity.kt`).
- [x] **E01-03**: ViewModel mengekspose `StateFlow<UiState>`; layar Compose bebas akses DB/ML langsung.
- [x] **E01-04**: Interface domain & engine (`domain/*`) terpisah dari implementasi Room/Media3/LiteRT (`data/*`).
- [x] **E01-05**: `viewModelScope` + `collectAsStateWithLifecycle()` + error boundary teruji.
- [x] **E01-06**: Hilt DI minimal (`DatabaseModule`, `RepositoryModule`), single `:app` module.
- [x] **E01-07**: DataStore khusus preferensi (`UserPreferencesRepository`); Room menyimpan entitas relasional + FTS4.
- [x] **E01-08**: Process death/DB reopen (`RoomIntegrityAndRecoveryTest`), light/dark (`UiAndAccessibilityTest`), startup offline (`SM-G988B`).

### EPIC-02 - Navigation, Home & exam selector
- [x] **E02-01** .. **E02-07**: Home, switcher 3 ujian terpisah, persistensi DataStore, tap budget `<=2`/`<=3` teruji di `UiAndAccessibilityTest` & `01-home.png` / `02-exam-switcher.png`.

### EPIC-03 - Offline learning catalog & lesson player
- [x] **E03-01** .. **E03-07**: Filter skill + `Tersimpan`, struktur materi lengkap (objective, theory, worked example, mistakes, drills), bilingual ID-EN, bookmark + last-read persisten (`UserPreferencesRepository`), metadata versi/lisensi (`LearnScreens.kt`), jalan tanpa LLM/STT.

### EPIC-04, EPIC-05, EPIC-06, EPIC-07 - Konten 3 Ujian & 15 Paket
- [x] **E04-01..07**, **E05-01..08**, **E06-01..06/08**, **E07-01,03,05,06,07,08**: Kurikulum `ITP L1`, `iBT 2026`, `IELTS Academic` terpisah; 15 manifest di `packs/index.json` (total 180 butir + 62 WAV); validasi struktur via `PackIntegrityValidator` (`AssetSeederAndFtsTest`); label `Sample` & skor mentah jujur (`ExamScoreEstimator`).
- [ ] **E04-08** / **E06-07** / **E07-02** / **E07-04**: Paket bawaan masih berstatus `Sample` (`12/140`, `12/120`, `12/85`) dan membutuhkan penulisan item lengkap + review linguistik/akademik manusia sebelum status dapat dinaikkan ke `Full Mock Ready`.

### EPIC-08 & EPIC-09 - Quiz Engine & Media3 Audio
- [x] **E08-01..08**: 10 `TaskType`, normalisasi `TFNG`/`YNNG` & batas kata IELTS (`QuizGradingEngineTest`), autosave/DB reopen (`RoomIntegrityAndRecoveryTest`), konfirmasi sebelum submit, Strict vs Practice feedback, scoring deterministik murni.
- [x] **E09-01..07**: `Media3AudioEngine` memainkan 62 WAV 16 kHz mono offline (`06-exam-listening.png`), kontrol Practice vs Strict replay limit, focus/noisy receiver, transkrip opsional mode Latihan, fallback error asset tanpa crash.

### EPIC-10 & EPIC-11 - Speaking & Writing Engines
- [x] **E10-01..04,06..08** & **E11-01..06**: `RECORD_AUDIO` just-in-time + fallback manual, 16 kHz mono WAV lokal, playback + hapus rekaman (`ExamViewModel.deleteSpeakingRecording`), autosave tulisan, hitung kata live, penyesuaian IELTS Task 1-2 & iBT 2026, tanpa auto-submit paksa.
- [ ] **E10-05** (live transcription accuracy on real Whisper weights): Kontrak adapter `WhisperOnnxSpeechEngine` + 80-mel extractor `[1, 80, 3000]` terverifikasi, namun akurasi transkripsi nyata memerlukan file `.onnx` yang diimpor pengguna lewat SAF.

### EPIC-12, EPIC-13, EPIC-14, EPIC-15 - Mock, Scoring, Room Integrity & Analytics
- [x] **E12-01..07**: Pemisahan mode, aturan profil, monotonic timer checkpoint (`RoomIntegrityAndRecoveryTest`), konfirmasi submit & sisa soal kosong, gating paket Ready.
- [x] **E13-01..07**: Raw score & accuracy deterministik tanpa konversi skala palsu (`v1.1-raw-deterministic`), `Dijawab/Dilewati` jelas, breakdown per seksi, review & `Ulangi Soal Salah` (`07-results.png`).
- [x] **E14-01..07**: 17 entitas Room + schema v1 teruji (`app/schemas/.../1.json`), `@Upsert` non-destruktif, atomic pack import, JSON backup export/restore & delete.
- [x] **E15-01..06**: Progress per seksi, modul lemah `<80%` (`08-progress-analytics.png`), anti-duplikasi progress (`RoomIntegrityAndRecoveryTest`), empty state jujur.

### EPIC-16, EPIC-17, EPIC-18, EPIC-19 - SAF, Gemma 4, AI Tutor RAG & Profile
- [x] **E16-01..10**: `SafSecurityValidatorTest` (5/5), copy SAF ke private `files/models/`, hapus model konfirmasi, tanpa call jaringan tersembunyi.
- [x] **E17-01..03,06,07**: Kontrak `LiteRtLmGemma4Engine` (`0.13.1`), validasi `.litertlm` vs GGUF, fallback FTS4 offline mulus.
- [ ] **E17-04,05**: Benchmark RAM puncak, latency first-token, dan tokens/detik model Gemma 4 `.litertlm` nyata pada perangkat fisik memerlukan pengunduhan bobot model ~2.6 GB oleh pengguna lewat SAF.
- [x] **E18-01..08**: Composer minimal, stop/clear thread, kutipan `[ID]` FTS4 RAG (`09-ai-chat-rag.png`), sanitasi prompt injection (`RagPromptSanitizerTest`), penguncian Tutor saat Simulasi Strict aktif (`strictMockActive`).
- [x] **E19-01..06**: Bahasa, tema, font scale, reduce motion, haptik, opt-in reminder lokal (`StudyReminderWorker`), backup/delete, dialog lisensi OSS & disclaimer merek dagang.

### EPIC-20 & EPIC-21 - Design System, Anti-AI-Slop & DevSecOps Release
- [x] **E20-01..10**: Design tokens (`Tokens.kt`, `Theme.kt`), tata letak Home tanpa eyebrow kicker & 1 CTA utama, list row bersih, shared `SelectableOptionRow`, `reduceMotion`, `48dp` target, `200%` font scale (`UiAndAccessibilityTest` 8/8), 11 screenshot fisik `SM-G988B`.
- [x] **E21-01..03,05..09**: Airplane-mode core P0 verified, `34/34` tes + `lintDebug` (0 error) + `assembleRelease` R8 (`mapping.txt`, `-repackageclasses 'o'`, log stripping, `allowBackup=false`, ECDSA/Keystore `SecurityHardeningTest`), zero `INTERNET` permission.
- [ ] **E21-04 (Manual Switch Access / physical external TalkBack device pass)**: Otomasi Compose accessibility (`48dp`, semantics `heading()`, `Role`, `contentDescription`, `fontScale = 2.0f`) lulus di JVM/Robolectric, sedangkan audit manual Switch Access fisik belum dijalankan sebagai sesi terpisah.
