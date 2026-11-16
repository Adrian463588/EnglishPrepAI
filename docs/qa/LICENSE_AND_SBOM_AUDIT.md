# Dependency SBOM & License Audit — English Prep AI (`v1.1.0`)

Source manifest: `gradle/libs.versions.toml`, `app/build.gradle.kts`, `material_bank/MANIFEST.json`
Audit date: `2026-11-16`

## 1. Runtime & Jetpack / Google Dependencies

| Group : Artifact | Version | License | Role |
|---|---|---|---|
| `androidx.core:core-ktx` | `1.15.0` | Apache-2.0 | Core Android KTX extensions |
| `androidx.lifecycle:*` | `2.9.0` | Apache-2.0 | ViewModel, Lifecycle-safe Flow collection |
| `androidx.activity:activity-compose` | `1.10.1` | Apache-2.0 | Edge-to-edge + SAF activity result contracts |
| `androidx.compose:compose-bom` (+ `ui`, `material3`, `windowsize`, `material-icons`) | `2025.02.00` | Apache-2.0 | Material 3 declarative UI |
| `androidx.navigation:navigation-compose` | `2.8.8` | Apache-2.0 | Typed Compose routes & transitions |
| `com.google.dagger:hilt-android` + `hilt-compiler` | `2.58` | Apache-2.0 | Compile-time DI |
| `androidx.hilt:hilt-navigation-compose` | `1.2.0` | Apache-2.0 | Hilt ViewModel navigation scoping |
| `androidx.room:room-runtime`, `room-ktx`, `room-compiler` | `2.7.2` | Apache-2.0 | Local SQLite DB + FTS4 |
| `androidx.datastore:datastore-preferences` | `1.1.3` | Apache-2.0 | Lightweight preferences persistence |
| `androidx.documentfile:documentfile` | `1.0.1` | Apache-2.0 | SAF tree & document resolution |
| `androidx.work:work-runtime-ktx` | `2.10.0` | Apache-2.0 | Opt-in local daily study reminder |
| `androidx.media3:media3-exoplayer`, `media3-common` | `1.5.1` | Apache-2.0 | Offline audio playback & audio focus |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | `1.10.1` | Apache-2.0 | Structured concurrency |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | `1.7.3` | Apache-2.0 | Strict JSON pack & backup DTOs |
| `com.google.ai.edge.litertlm:litertlm-android` | `0.13.1` | Apache-2.0 | Optional on-device `.litertlm` Gemma 4 runtime |
| `com.microsoft.onnxruntime:onnxruntime-android` | `1.22.0` | MIT | Optional on-device Whisper `.onnx` STT runtime |

## 2. Test-Only Dependencies (`testImplementation`)

| Group : Artifact | Version | License |
|---|---|---|
| `junit:junit` | `4.13.2` | EPL-1.0 |
| `androidx.room:room-testing` | `2.7.2` | Apache-2.0 |
| `org.robolectric:robolectric` | `4.14.1` | MIT |
| `app.cash.turbine:turbine` | `1.2.0` | Apache-2.0 |
| `androidx.compose.ui:ui-test-junit4` | `BOM 2025.02.00` | Apache-2.0 |

## 3. Content, Secret & Trademark Compliance
- Zero hardcoded API keys, keystores, or passwords tracked in Git (verified via `git ls-files` and CI workflow `.github/workflows/android-devsecops.yml`).
- All 30 curriculum modules, 90 drills, 15 full-blueprint packs (`1,725` items: 700 ITP L1 + 600 iBT 2026 + 425 IELTS Academic), and 390 IMA-ADPCM 16 kHz mono WAV audio files (`57.00 MB`) are original (`EnglishPrepAI-Original-2026`).
- Material Bank PDF governance (`material_bank/MANIFEST.json`):
  - `material_bank/open_oer_pdfs/` (`10` PDFs, tracked in Git): Open Educational Resources under `CC BY 4.0` (LibreTexts / Iowa State / USA Journals), `CC BY-NC-SA 4.0` (LibreTexts ESOL), and `Public Domain (US Gov)` (NOAA, NASA). Extracted into `material_bank/extracted/*.md` and `app/src/main/assets/material_bank/material_bank.json` (`31` offline study excerpts) with author & license headers preserved.
  - `material_bank/official_reference_pdfs/` (`7` PDFs, excluded from Git via `.gitignore` per `AGENTS.md` Rule 8): ETS TOEFL ITP/iBT 2026 specifications & handbooks and IELTS public descriptors remain local-only audit references; zero copyrighted ETS/Cambridge/British Council/IDP test questions or audio tracks are copied into Git or APK assets.
- Trademark disclaimers displayed in `ProfileScreen` & `README.md`.
