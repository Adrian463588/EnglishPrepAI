# Changelog

## 1.2.0 - 2026-11-17
### Fixed & Redesigned (`specs/ui-ux-redesign`)
- **P0 System Bar Contrast Fix (`MainActivity.kt`)**: Added reactive `SideEffect` syncing `WindowCompat.getInsetsController` (`isAppearanceLightStatusBars = !useDark`, `isAppearanceLightNavigationBars = !useDark`), fixing invisible white status bar icons on `#FAF9F6` Warm Paper when `ThemeMode.LIGHT` is active on a Dark-mode OS.
- **Editorial Material 3 System (`docs/design-system.md`)**: Eliminated nested card-inside-card boxes, promotional hero subtitles, repeated exam scale copy, and raw internal database IDs (`ITP-MOD-01`, `CHOICE_SINGLE`, `ITP01-L01`) from primary learner UI surfaces.
- **Screen-by-Screen UX Redesign**:
  - **Home (`HomeScreen.kt`, `HomeViewModel.kt`)**: Minimal header, compact pill exam switcher, reactive Room session progress (`X dari Y soal`), ONE focal Continue/Start CTA, hairline `Progres belajar` & `Pelajaran berikutnya` rows, and direct `Tutor AI ->` link (`onOpenChat(null)`).
  - **Learn & Module Detail (`LearnScreens.kt`, `LearnViewModel.kt`)**: Single-row horizontal `LazyRow` filter strip (`Structure`), single `[Bacaan OER (31)]` mode switch, divider-separated module rows, and calm editorial reader using `section.label` TopBar without card stacks.
  - **Practice & Exam (`PracticeScreen.kt`, `ExamScreen.kt`, `AppComponents.kt`)**: Tonal `PackageRow`, compact `AudioPlayerBar` with `[1.0x v]` dropdown in Practice mode, borderless question prompt layout, symmetric bottom action bar (`Sebelumnya` / `Periksa` / `Berikutnya`) with `imePadding()` & `navigationBarsPadding()`, and preserved Practice/Strict security rules.
  - **Profile & Dedicated Model Manager (`ProfileScreen.kt`, `ModelManagerScreen.kt`, `AppDestinations.kt`)**: Reorganized Profile into 4 grouped sections (`Preferensi`, `Konten Offline`, `AI Offline`, `Privasi & Data`) with interactive `Keamanan & Integritas R8 >` modal and scrollable OSS license dialog; extracted AI weights/SAF importer/HF catalog into `AppRoute.ModelManager` reachable in 1 tap from Profile or Chat.
  - **AI Tutor, Progress & Results (`ChatScreen.kt`, `ProgressScreen.kt`, `ResultsScreen.kt`)**: Clean `Tutor AI` header with clickable model-status link, formatted `Sumber: ...` citations, 4-section Progress hierarchy gating trend charts to `>= 2` sessions, and divider-separated Results review rows.
- **Verification (`v1.2.0` / `versionCode = 4`)**: `43/43` unit & Robolectric Compose UI tests pass (`UiAndAccessibilityTest`: 17 tests across `320dp @ 2.0f`, `360dp`, `412dp`, `960dp`, Light & Dark modes, `48dp` targets, `ModelManager`), `lintDebug` `0` errors (`80` warnings), `assembleDebug` (`210,392,353` bytes / `200.65 MB`, SHA-256 `0073C9875E9941D1E8E792ED43DE1C3C9F742E1403D116D460B0346EC55715DC`), R8 `assembleRelease` (`189,909,002` bytes / `181.11 MB` + `mapping.txt`), `aapt2` verified no `INTERNET`/`debuggable`, and live Dark + Light physical QA on `SM-G988B` across 16 refreshed screenshots with `0` crashes in `logcat`.

## 1.1.1 — 2026-11-16
### Added & Improved
- **DevSecOps Hardening & GitHub Actions CI/CD Fix (`specs/010`)**:
  - Fixed executable permission bit (`100755`) on `gradlew` (resolving past `./gradlew: Permission denied` CI failures) and overhauled `.github/workflows/android-devsecops.yml` with Node 24 actions (`actions/checkout@v7`, `actions/setup-java@v6`, `android-actions/setup-android@v4`, `gradle/actions/setup-gradle@v6`, `actions/upload-artifact@v7`), least-privilege `permissions: contents: read`, credential/secret regex scanner, offline AI/ML & Material Bank integrity check, sequential Gradle steps, and compiled APK inspection via `aapt2`.
  - Hardened `app/build.gradle.kts` (`isDebuggable = false`, `isJniDebuggable = false`, `vcsInfo { include = false }`, `DebugProbesKt.bin` exclusion) and `AndroidManifest.xml` (`android:usesCleartextTraffic="false"`), verified `181.11 MB` (`189,908,998` bytes) with zero `INTERNET` or `application-debuggable` flags in compiled Release APK.
  - Added author attribution signature (`Dibuat oleh Adrian Syah Abidin`) to `README.md`.
- **Custom Android Adaptive & Monochrome App Icon (`specs/009`)**: Replaced placeholder icon with bespoke vector/radial-teal open-book/audio/bookmark/check artwork (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`, `ic_launcher_monochrome.xml`, `mipmap-anydpi-v26/*`) plus symmetric 4x-supersampled raster mipmap icons across `mdpi`..`xxxhdpi` and `docs/screenshots/app-icon-512.png` via `tools/generate_app_icon.py`. Removed unused `values/ic_launcher_background.xml` so `IconLauncherShape` and `UnusedResources` lint warnings dropped to 0.
- **Menu-by-Menu Responsive & Adaptive UI/UX Polish**:
  - `AppComponents.kt`: Added `rememberIsExpandedWidth()`, `ResponsiveScreenContainer`, `TextStyle.tabular()` (`tnum`), centered `AppButton` text wrapping, `SelectableOptionRow` press scale (`0.985f`) + `LocalIndication.current` ripple, and `AudioPlayerBar` title ellipsis + `tnum` timestamps + animated Surface speed pills.
  - `HomeScreen` & `PracticeScreen`: 2-column Expanded tablet/landscape layouts (`840dp+`), `FlowRow` Hero CTAs safe at `320dp @ 2.0x`, `skipPartiallyExpanded` switcher sheet.
  - `LearnViewModel` & `LearnScreens`: Fixed mutually-exclusive `Semua`/`Tersimpan` filter chip selection, `AnimatedVisibility` + rotating chevron on `MaterialBankRow`, and single-line ellipsis on `ModuleDetailScreen` `TopAppBar`.
  - `ExamScreen`: Tabular timer (`tnum`), thin `3dp` progress strip, wrapping bottom action bar (`fontScale >= 1.5f` on narrow phones), `skipPartiallyExpanded` passage sheet, and Navigator color legend.
  - `ResultsScreen` & `ChatScreen`: `Semua`/`Salah`/`Dilewati`/`Benar` `FilterChip` bar over 85-140 review items; `ChatScreen` TopBar ellipsis, auto-scroll on stream/reply, and asymmetric bubbles.
  - `ProgressScreen` & `ProfileScreen`: Fixed `fillMaxWidth()` on `"Akurasi per Seksi"` card, animated section bars, paired Trend Chart in Expanded view, whole-row `Modifier.toggleable` switches, and updated `"1.725 butir READY"` compliance copy.
- **Verification**: `40/40` JVM + Robolectric Compose UI tests pass (0 failures, 0 skipped), `lintDebug` `0` errors (`76` warnings), `assembleDebug` (`210,344,064` bytes / `200.6 MB`), R8 `assembleRelease` (`189,909,942` bytes / `181.11 MB` + `mapping.txt`), and full live click-through on physical `SM-G988B` (`versionCode = 3`) with refreshed screenshots and zero crashes.

## 1.1.0 — 2026-11-16
### Added
- **Material Bank PDF Pipeline & In-App Reader (`specs/008`)**: Added `tools/fetch_material_bank.py`, `tools/build_material_bank.py`, and `material_bank/` (`17` verified PDFs, `1,652` pages -> `31` clean study digests in `app/src/main/assets/material_bank/material_bank.json`, FTS4-indexed by `AssetSeeder` & browsable inside `LearnScreen` with license attribution and 1-tap Tutor AI ask). Copyrighted official ETS/IELTS reference PDFs stay excluded via `.gitignore` per Rule 8.
- **Expanded Curriculum (30 Modules, 90 Drills)**: Expanded `tools/curriculum_data.py`, `tools/curriculum_extra_oer.py`, and `content/CURRICULUM.md` from `18` to `30` bilingual modules (`10` per exam).
- **Full-Blueprint 15 READY Packs (1,725 Total Items)**:
  - `ITP-L1-01..05`: `5 x 140 = 700` items (`50` Listening, `40` Structure/WE, `50` Reading; `115 min`).
  - `IBT26-01..05`: `5 x 120 = 600` items (`50` Reading, `47` Listening, `12` Writing, `11` Speaking; `90 min`; Jan 21, 2026 blueprint).
  - `IELTS-A-01..05`: `5 x 85 = 425` items (`40` Listening, `40` Reading, `2` Writing, `3` Speaking; `165 min`).
- **MS IMA-ADPCM Offline Audio Compression (`tools/ima_adpcm_wav.py`)**: Compressed all `390` offline 16 kHz mono WAV tracks into `57.00 MB` (~4x smaller than uncompressed PCM16) decoded natively by Media3 `WavExtractor` (`ImaAdPcmOutputWriter`).
- **Exam UX & Non-Destructive DB Upgrade**:
  - Updated `ExamViewModel` to preserve active audio playback cleanly across multi-question groups sharing the same `audioAssetId` (Part B/C, iBT Conversations/Talks, IELTS Parts 1-4).
  - Extracted scrollable, section-filtered 5-column `QuestionNavigatorSheetContent` supporting 85, 120, and 140-item mocks with `>=48dp` targets.
  - Updated `CatalogDao` & `AssetSeeder` to atomically upgrade legacy 12-item DB installs to full `140`/`120`/`85` `READY` packs without wiping user sessions/progress.
- **Verification**: `36/36` unit/integration/Compose Robolectric tests pass, `lintDebug` `0` errors (`78` warnings), `assembleDebug` (`210,216,727` bytes / `200.5 MB`), R8 `assembleRelease` (`189,804,162` bytes / `181.0 MB`), and physical `SM-G988B` `v1.1.0` R8 Release upgrade verified live (`Ready • 140 soal`, IMA-ADPCM playback `00:04/00:11`, 140-item navigator jump to Reading Q91, and Material Bank reader).

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
