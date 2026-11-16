# Changelog

## 1.0.0 — 2026-11-16
### Added
- **Spec Kit & BMAD Documentation**: Constitution (`.specify/memory/constitution.md`), 4 feature spec bundles (`specs/001`..`004` with `spec.md`, `plan.md`, `tasks.md`, `acceptance.md`), 4 BMAD review reports (`docs/reviews/*.md`), and curated reference catalog (`docs/REFERENCES.md`).
- **Curriculum & 15 Tryout Packages**: `content/CURRICULUM.md` + `app/src/main/assets/curriculum/curriculum.json` (18 modules, 54 drills), `packs/index.json` + 15 original tryout packs (`ITP-L1-01..05`, `IBT26-01..05`, `IELTS-A-01..05`, 180 total questions) with honest `SAMPLE` status badges, plus 62 synthesized 16 kHz mono WAV audio assets (`23.70 MB`).
- **Offline Android Compose App (`:app`)**:
  - Single-module MVVM + Hilt + Room (`english_prep.db` v1 with schema export + FTS4) + DataStore + WorkManager daily study reminder.
  - Pure Kotlin `QuizGradingEngine` & `ExamScoreEstimator` covering all 10 task types with explicit non-official score disclaimers.
  - `Media3AudioEngine` (ExoPlayer `1.5.1`) with audio focus, noisy-audio pause, practice speed/seek, and strict replay limits.
  - `LiteRtLmGemma4Engine` (`litertlm-android:0.13.1`) + `GroundedTutorPromptBuilder` + prompt-injection sanitizer + Room FTS4 RAG fallback.
  - `WavAudioRecorder` (16 kHz PCM WAV) + `WhisperOnnxSpeechEngine` (`onnxruntime-android:1.22.0`) + 80-mel spectrogram extractor + `SpeakingMetricsAnalyzer`.
  - `SafSecurityValidator` & `AiAndImportRepositoryImpl` supporting SAF model/folder/pack imports, zip-slip/bomb rejection, SHA-256 verification, and JSON backup export/restore.
  - 5-destination adaptive Compose Material 3 UI (`Home`, `Learn`, `Practice`, `Progress`, `Profile`) + `Exam`, `ModuleDetail`, `Results`, `Chat`.
  - Added `NavHost` directional transitions, `AnimatedContent` per-question slides, animated `SelectableOptionRow`, and global `hapticsEnabled` wiring respecting `reduceMotion`.
  - Fixed Room DAO `@Upsert` to prevent `ON DELETE CASCADE` wipes on `user_answers`, `speech_recordings`, and `chat_messages` when updating sessions/threads, and aggregated objective tryout accuracy into `StudyProgressEntity` on submission.
  - Expanded `docs/REFERENCES.md` with the official Hugging Face Gemma 4 collection and Android/Edge AI/STT repositories.
  - Added Spec 005 DevSecOps & R8 hardening (`specs/005-devsecops-r8-hardening`, `docs/reviews/bmad-security-devsecops.md`, `.github/workflows/android-devsecops.yml`), tightened `proguard-rules.pro` (`-repackageclasses 'o'`, log stripping, targeted DTO keeps), `android:allowBackup="false"`, optional `keystore.properties` release signing, `SecurityPostureEvaluator` / `AndroidAppIntegrityInspector`, `DetachedSignatureVerifier` (`SHA256withECDSA`), `AndroidKeystoreSealer` (`AES/GCM/NoPadding`), and Profile UI integrity summary.
  - Added Spec 007 EPIC-00..21 audit fixes (`specs/007-epic-audit-polish-gates`, `docs/qa/EPIC_STATUS.md`, `docs/qa/LICENSE_AND_SBOM_AUDIT.md`): honest raw-score policy (`v1.1-raw-deterministic`) without uncalibrated ITP/iBT/IELTS scaled claims, `TFNG`/`YNNG` normalization, 15-pack `PackIntegrityValidator`, module bookmarking & last-read filter, Practice audio transcript toggle, pre-submit unanswered warning, speaking recording delete, Chat Clear + Strict Mock lock, Home anti-AI-slop `craft-floor` refinement, OSS license modal, and `RoomIntegrityAndRecoveryTest`.
- **Verification**:
  - 34/34 JVM/Robolectric/Compose UI unit & integration tests passed (`SecurityHardeningTest`, `ToeflSessionFlowIntegrationTest`, `RoomIntegrityAndRecoveryTest`, 8 Compose `UiAndAccessibilityTest`); `lintDebug` passed with 0 errors (78 warnings); `assembleDebug` (`174,932,472` bytes) and R8 `assembleRelease` (`154,405,955` bytes + obfuscated `mapping.txt`) succeeded.
  - Physical device verification on Samsung `SM-G988B` (Android 13, `arm64-v8a`) on updated R8 Release (`195–366 ms` cold start): verified Home polish, Learn bookmarks/metadata, Practice `Sample` strict labeling, Audio + pre-submit confirmation dialog, `ResultsScreen` raw score (`Skor Mentah: 7/12 (58%)` + `Dilewati: 1`), `ProgressScreen` (`58%`, automatic weak modules), and zero crashes in `logcat`.
