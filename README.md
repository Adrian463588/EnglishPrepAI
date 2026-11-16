# English Prep AI

<p align="left">
  <img src="docs/screenshots/app-icon-512.png" alt="English Prep AI App Icon" width="88" />
</p>

> **Versi Aplikasi:** `v1.1.1` (`versionCode = 3`) | **Minimum Android:** Android 8.0+ (API 26) | **Target SDK:** 35 | **Offline-First:** Tanpa izin `INTERNET`
>
> **Unduhan APK Terbaru (GitHub Releases):**
> - **Halaman Rilis:** [English Prep AI v1.1.1 Release](https://github.com/Adrian463588/EnglishPrepAI/releases/latest)
> - **Unduh Langsung APK (`200.6 MB`, Universal 4-ABI, Sideload QA Build):** [`EnglishPrepAI-v1.1.1-universal-debug.apk`](https://github.com/Adrian463588/EnglishPrepAI/releases/download/v1.1.1/EnglishPrepAI-v1.1.1-universal-debug.apk)
> - **SHA-256 Checksum:** `A72C06A5C38E3A81463B6F66CBC9ED27D780BEFDB373366092426570956DF0CE`
> - **Build Rilis R8 / Play Store:** R8, ProGuard, dan Resource Shrinking aktif pada varian `release` (`isMinifyEnabled = true`, `isShrinkResources = true`, menghasilkan `181.11 MB` unsigned R8 APK). Developer dapat membangun `.apk` rilis yang ditandatangani dengan `keystore.properties` pribadi lewat `./gradlew assembleRelease`.

---

## Tentang project ini

**English Prep AI** adalah aplikasi Android native berbasis **Kotlin + Jetpack Compose + Material 3 + Clean MVVM** yang dirancang untuk persiapan ujian **TOEFL ITP Level 1**, **TOEFL iBT (format pasca-21 Januari 2026)**, dan **IELTS Academic** secara **100% offline**.

### Fitur inti
- **Diferensiasi 3 format tes secara eksplisit**:
  - **TOEFL ITP Level 1**: Fokus pada Listening Comprehension, Structure & Written Expression, serta Reading Comprehension (skala estimasi lokal `310-677`; Speaking/Writing tidak dimasukkan ke tes inti).
  - **TOEFL iBT (2026)**: Mengikuti struktur pembaruan 2026 (`Reading`, `Listening`, `Writing`, `Speaking`) termasuk tipe tugas modern (`FILL_BLANK`, `SENTENCE_BUILD`, `LISTEN_REPEAT`, `WRITING_RESPONSE`, `SPEAKING_RESPONSE`) pada skala `1-6` (disertai catatan konversi `0-120` dan disclaimer simulasi adaptif).
  - **IELTS Academic**: `Listening`, `Reading`, `Writing` Tasks 1-2, dan `Speaking` Parts 1-3 (`Band 0-9`, batas kata `FILL_BLANK`, `MATCHING`, `CHOICE_MULTI`).
- **Bank Materi OER, Kurikulum Penuh & 15 Paket Tryout (`READY`, 1.725 Soal)**:
  - **Material Bank PDF (`material_bank/`)**: 17 dokumen PDF terverifikasi (10 buku OER `CC BY` / Public Domain NASA-NOAA di repo + 7 referensi spesifikasi/rubrik ETS & IELTS lokal sesuai kepatuhan hak cipta Rule 8) yang diekstrak menjadi `31` kutipan bacaan offline + indeks FTS4 di menu **Materi**.
  - **30 modul materi & 90 drill** (`ITP-MOD-01..10`, `IBT-MOD-01..10`, `IELTS-MOD-01..10`).
  - **15 paket soal orisinal berblueprint penuh (`READY`)**: 5 paket TOEFL ITP (`5 x 140 = 700` soal), 5 paket TOEFL iBT 2026 (`5 x 120 = 600` item), dan 5 paket IELTS Academic (`5 x 85 = 425` item), dengan total **1.725 butir soal** serta navigator soal terfilter seksi.
  - **390 berkas audio MS IMA-ADPCM (`57.00 MB`)** 16 kHz mono offline yang didekode native oleh **Media3 ExoPlayer 1.5.1** dengan kontinuitas rekaman antarmulti-soal, pengatur kecepatan/seek di mode Latihan, dan batas putar ulang di mode Strict.
- **Quiz engine deterministik & scoring non-resmi**:
  - Penilaian objektif deterministik murni di Kotlin untuk 10 `TaskType` (`QuizGradingEngine`), pembahasannya per butir lengkap dengan tautan modul, serta tombol `Ulangi Soal Salah`.
  - Seluruh skor dan rubrik umpan balik diberi label eksplisit non-resmi.
- **AI offline via Storage Access Framework (SAF)**:
  - **Chatbot Tutor & Writing/Speaking Coach**: Mendukung runtime lokal **Gemma 4** (`.litertlm` via `litertlm-android:0.13.1`), sanitasi *prompt injection*, serta fallback otomatis **Room FTS4 RAG** yang selalu aktif tanpa LLM dan mengutip ID modul lokal (`[ITP-MOD-01]`, dst.).
  - **Speaking Engine**: Perekaman 16 kHz PCM WAV lokal (`WavAudioRecorder`), analisis kecepatan bicara (`WPM`), kata pengisi (*filler*), dan integrasi runtime **Whisper ONNX** (`onnxruntime-android:1.22.0` + ekstraktor 80-mel `[1, 80, 3000]`).
  - **Impor Paket Soal & Cadangan JSON**: Impor aman `.litertlm`, `.onnx` folder, `.json`, dan `.zip` dengan validasi ukuran 0-byte, mismatch GGUF, anti zip-slip (`../`), anti decompression bomb, SHA-256, dan ekspor/pulih JSON pribadi.
- **DevSecOps, R8/ProGuard & RASP lokal**:
  - R8 obfuscation (`-repackageclasses 'o'`, stripping log `android.util.Log`), resource shrinking, `android:allowBackup="false"`, verifikasi tanda tangan paket ECDSA `SHA256withECDSA` (*public-key only*), enkripsi lokal `AndroidKeyStore` (`AES/GCM/NoPadding`), dan kartu transparansi **Keamanan & Integritas R8** di menu Profil.

---

## Tampilan aplikasi secara real-time per menu dan fitur

Seluruh tangkapan layar di bawah diambil langsung dari perangkat fisik **Samsung Galaxy (`SM-G988B`, Android 13, `arm64-v8a`)** yang menjalankan build terverifikasi aplikasi ini.

### Navigasi utama & pembelajaran

| Beranda (Home) | Pemilih jenis tes (Exam Switcher) | Kurikulum & materi (Learn) | Detail modul & drill |
| :---: | :---: | :---: | :---: |
| ![Beranda](docs/screenshots/01-home.png) | ![Exam Switcher](docs/screenshots/02-exam-switcher.png) | ![Kurikulum Materi](docs/screenshots/03-learn-modules.png) | ![Detail Modul](docs/screenshots/04-module-detail.png) |
| Ringkasan paket cepat, target skor aktif, materi lanjutan, responsif hingga layar tablet, dan akses 1 ketukan ke Tutor AI. | Pergantian instan antara TOEFL ITP Level 1, TOEFL iBT (2026), dan IELTS Academic. | Penyaringan materi eksklusif per seksi (`Semua`, `Tersimpan`, dll.) sesuai tes aktif. | Tujuan belajar, konsep inti, contoh terjawab, jebakan umum, drill, dan tautan Tutor AI. |

### Latihan tryout penuh, pemutar audio, scoring & navigator 140 soal

| Daftar 5 paket tryout penuh (`Ready`) | Simulasi exam & audio Media3 | Navigator 140 soal terfilter seksi | Bank Materi OER & Panduan PDF |
| :---: | :---: | :---: | :---: |
| ![Daftar Paket](docs/screenshots/05-practice-packs.png) | ![Exam Listening Audio](docs/screenshots/06-exam-listening.png) | ![Navigator 140 Soal](docs/screenshots/12-navigator-140.png) | ![Bank Materi OER](docs/screenshots/13-material-bank-oer.png) |
| Label `Ready • 140 soal`, durasi `115m`, ukuran audio terkompresi, Mode Latihan & `Full Mock Strict`. | Pemutar audio Media3 IMA-ADPCM (`0.85x-1.25x`), timer tabular `tnum`, progress strip tipis, transkrip Latihan & `Cek`. | Legenda warna navigator + filter seksi (`Listening`, `Structure`, `Reading`) pada grid 5 kolom >=48dp. | Ekstrak OER & Public Domain dengan animasi ekspansi halus dan tautan Tutor AI. |

### Hasil skor, analitik progres, AI offline & DevSecOps R8

| Skor & pembahasan per butir | Analitik progres lokal | Tutor AI Offline (Gemma 4 / FTS4 RAG) | Profil & impor AI via SAF | Keamanan R8 & cadangan JSON |
| :---: | :---: | :---: | :---: | :---: |
| ![Hasil dan Pembahasan](docs/screenshots/07-results.png) | ![Analitik Progres](docs/screenshots/08-progress-analytics.png) | ![Tutor AI RAG](docs/screenshots/09-ai-chat-rag.png) | ![Profil AI SAF](docs/screenshots/10-profile-ai-saf.png) | ![Profil Keamanan R8](docs/screenshots/11-profile-security-r8.png) |
| Skor mentah non-resmi, filter butir (`Semua`/`Salah`/`Dilewati`/`Benar`), & `Ulangi Soal Salah`. | Akurasi objektif lokal, kartu `fillMaxWidth` seragam, bar teranimasi, deteksi modul lemah `<80%`. | Pencarian FTS4 offline (modul + Material Bank) dengan auto-scroll & gelembung asimetris. | Impor file `.litertlm` / folder Whisper ONNX / paket soal via SAF + katalog Hugging Face. | Status runtime R8/ProGuard aktif, switch baris penuh `toggleable`, SHA-256 sertifikat, & cadangan JSON. |

---

## Susunan struktur project

```text
EnglishPrepAI/
├── .github/workflows/
│   └── android-devsecops.yml              # CI gate: secret/manifest hygiene, unit test, lint, R8 release
├── .specify/memory/
│   └── constitution.md                    # Konstitusi Spec Kit proyek (offline-first, Clean MVVM, a11y)
├── specs/
│   ├── 001-core-catalog-curriculum/       # Spec, plan, tasks, acceptance: Kurikulum & paket bawaan
│   ├── 002-quiz-audio-exam-engine/        # Spec, plan, tasks, acceptance: Quiz engine, Media3, Timer
│   ├── 003-offline-ai-saf-gemma4-whisper/ # Spec, plan, tasks, acceptance: Gemma 4, Whisper ONNX, SAF
│   ├── 004-progress-analytics-uiux-a11y/  # Spec, plan, tasks, acceptance: Analytics, Compose a11y
│   ├── 005-devsecops-r8-hardening/        # Spec, plan, tasks, acceptance: R8/ProGuard, RASP, Keystore
│   ├── 006-github-repo-docs-release/      # Spec, plan, tasks, acceptance: Repo GitHub, README & Release
│   ├── 007-epic-audit-polish-gates/       # Spec, plan, tasks, acceptance: Audit EPIC-00..21 & anti-slop craft
│   ├── 008-material-bank-full-packs/      # Spec, plan, tasks, acceptance: Bank Materi PDF, 30 modul, 1.725 soal
│   └── 009-uiux-responsive-icon-polish/   # Spec, plan, tasks, acceptance: Audit UI/UX responsif & ikon adaptif aplikasi
├── docs/
│   ├── qa/                                # EPIC_STATUS.md (matrix bukti) & LICENSE_AND_SBOM_AUDIT.md
│   ├── reviews/                           # Laporan tinjauan BMAD (Analyst, Architect, UX, QA, Security, Release, Closure, Material Bank, UI/UX Icon)
│   ├── screenshots/                       # 13 tangkapan layar nyata dari SM-G988B + pratinjau app-icon-512.png
│   ├── REFERENCES.md                      # Katalog model Hugging Face, dataset terbuka & referensi GitHub
│   └── VERIFICATION_CHECKLIST.md          # Checklist bukti uji unit, lint, R8 & perangkat fisik
├── material_bank/
│   ├── open_oer_pdfs/                     # 10 PDF OER CC-BY / Public Domain US Gov (di-commit)
│   ├── official_reference_pdfs/           # 7 PDF spesifikasi ETS/IELTS (di-gitignore per Rule 8, lokal saja)
│   ├── extracted/ & material_bank.json    # 31 kutipan bab terindeks + MANIFEST.json (SHA-256)
├── content/
│   └── CURRICULUM.md                      # Sumber desain materi 30 modul + 90 soal drill
├── packs/
│   ├── index.json                         # Indeks 15 paket tryout penuh READY (ITP 140, iBT 120, IELTS 85)
│   └── *.json                             # 15 paket soal orisinal berstatus READY (1.725 total butir)
├── tools/
│   ├── fetch_material_bank.py             # Dorker/downloader PDF + verifikasi SHA-256 & MANIFEST.json
│   ├── build_material_bank.py             # Ekstraktor PyMuPDF + generator material_bank.json
│   ├── ima_adpcm_wav.py                   # Encoder MS IMA-ADPCM (16 kHz, 4-bit mono) untuk Media3
│   ├── curriculum_data.py / curriculum_extra_oer.py
│   ├── bank_itp/ bank_ibt/ bank_ielts/    # Generator bank soal modular 1.725 butir
│   ├── packs_itp.py / packs_ibt.py / packs_ielts.py
│   ├── generate_app_icon.py               # Generator ikon launcher adaptif + monochrome + 5 densitas mipmap
│   └── generate_assets.py                 # Generator 390 berkas audio IMA-ADPCM & sinkronisasi aset
└── app/
    ├── build.gradle.kts                   # Konfigurasi Android SDK 35, R8 release minify, signing opsional
    ├── proguard-rules.pro                 # Aturan R8 obfuscation, log stripping, JNI/DTO keeps
    ├── schemas/.../1.json                 # Ekspor skema Room SQLite v1 untuk audit migrasi
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml        # Tanpa INTERNET permission; allowBackup="false"
        │   ├── assets/{audio,curriculum,material_bank,packs}/
        │   ├── res/drawable/ic_launcher*.xml & mipmap-*/  # Ikon adaptif vektor, monochrome Android 13+, 5 mipmap PNG
        │   ├── res/xml/{backup_rules,data_extraction_rules}.xml
        │   └── java/com/englishprep/ai/
        │       ├── data/
        │       │   ├── datastore/         # UserPreferencesRepository (DataStore Preferences)
        │       │   ├── imports/           # SafSecurityValidator
        │       │   ├── inference/         # LiteRtLmGemma4Engine
        │       │   ├── media/             # Media3AudioEngine, LocalTtsSpeaker
        │       │   ├── repository/        # Implementasi ExamCatalog, SessionProgress, AiAndImport
        │       │   ├── room/              # Entities (17 tabel/FTS4), Daos (@Upsert), EnglishPrepDatabase, AssetSeeder
        │       │   ├── security/          # AndroidAppIntegrityInspector, AndroidKeystoreSealer
        │       │   ├── stt/               # WavAudioRecorder, WhisperOnnxSpeechEngine
        │       │   └── work/              # StudyReminderWorker
        │       ├── di/                    # DatabaseModule, RepositoryModule (Hilt DI)
        │       ├── domain/
        │       │   ├── ai/                # GroundedTutorPromptBuilder, RubricFeedbackAnalyzer
        │       │   ├── engines/           # Kontrak interface AudioPlayer, LocalLlmEngine, SpeechToTextEngine
        │       │   ├── grading/           # QuizGradingEngine, ExamScoreEstimator, SpeakingMetricsAnalyzer
        │       │   ├── models/            # DomainModels, AudioSource
        │       │   ├── repositories/      # Kontrak repository
        │       │   └── security/          # SecurityContracts, DetachedSignatureVerifier, AesGcmCodec
        │       └── ui/
        │           ├── components/        # Komponen Material 3 reusable (AppButton, AudioPlayerBar, dll.)
        │           ├── feature/           # 9 layar + ViewModel UDF (home, learn, practice, exam, results, chat, progress, profile)
        │           ├── navigation/        # Type-safe Compose Navigation routes
        │           └── theme/             # Palette deep teal, Tokens (MotionTokens, AppSpacing), Theme
        └── test/                          # 40 unit & Robolectric integration/UI accessibility tests
```

---

## Arsitektur sistem

Aplikasi memakai single-module Clean MVVM dengan aliran dependensi searah (`ui -> domain <- data`) dan StateFlow Unidirectional Data Flow (UDF):

```mermaid
flowchart LR
    UI["Compose UI + ViewModels"] --> Domain["Pure Kotlin Domain (Grading, Estimator, Security, Contracts)"]
    Data["Data Layer (Room FTS4, DataStore, Media3, LiteRT-LM, ORT, SAF, AndroidKeyStore)"] --> Domain
    UI --> DI["Hilt DI Graph"]
    DI --> Data
```

---

## Cara menggunakan aplikasi & panduan developer

### A. Untuk pengguna (instalasi APK & fitur AI opsional)
1. Unduh **`EnglishPrepAI-v1.1.1-universal-debug.apk`** dari tautan rilis di bagian atas README dan pasang pada perangkat Android 8.0+ (API 26 atau lebih baru).
2. Aplikasi langsung berfungsi penuh tanpa internet: pilih target ujian di kartu atas **Beranda** (`TOEFL ITP Level 1`, `TOEFL iBT (2026)`, atau `IELTS Academic`), buka **Materi** untuk teori 30 modul + Bank Materi OER PDF, atau masuk ke **Latihan** untuk mengerjakan 15 paket tryout penuh (`140`/`120`/`85` soal) beserta audio listening IMA-ADPCM bawaan.
3. *(Opsional)* Mengaktifkan **Chatbot Gemma 4** offline:
   - Unduh file `.litertlm` resmi (misalnya `gemma-4-E2B-it.litertlm`, ~2.6 GB) lewat browser eksternal dari koleksi [Hugging Face LiteRT Gemma 4](https://huggingface.co/collections/google/gemma-4).
   - Buka tab **Profil > Impor Gemma 4 (.litertlm)** lalu pilih file via Android SAF.
4. *(Opsional)* Mengaktifkan **Transkripsi Speaking Whisper ONNX** offline:
   - Unduh folder model Whisper Tiny English ONNX (`encoder_model.onnx`, `decoder_model.onnx`, `vocab.json`) dari Hugging Face (`onnx-community/whisper-tiny.en`).
   - Buka tab **Profil > Impor Folder Whisper ONNX** lalu pilih foldernya via SAF.

### B. Untuk developer (build, test & release R8)
Prasyarat: **JDK 17**, Android SDK (Compile SDK 35), dan Python 3 (opsional bila ingin meng-generate ulang pack/audio).

```bash
# 1. Clone repository
git clone https://github.com/Adrian463588/EnglishPrepAI.git
cd EnglishPrepAI

# 2. Jalankan seluruh unit test & Robolectric Compose UI test (40 tests)
./gradlew testDebugUnitTest

# 3. Jalankan pemeriksaan Android Lint
./gradlew lintDebug

# 4. Bangun APK Debug siap instal ke emulator/perangkat
./gradlew assembleDebug

# 5. Bangun APK Release terobfuskasi R8 + shrunk resources
./gradlew assembleRelease
```

Untuk menandatangani build **Release** dengan keystore produksi Anda sendiri (jangan pernah commit `.jks` atau `keystore.properties` ke Git):

```properties
# Simpan sebagai keystore.properties di root project (otomatis diabaikan .gitignore)
storeFile=path/to/release-key.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=YOUR_KEY_ALIAS
keyPassword=YOUR_KEY_PASSWORD
expectedReleaseSha256=YOUR_CERT_SHA256_UPPERCASE_HEX
```

Untuk meregenerasi ulang konten 30 modul, 15 paket soal (1.725 item), dan 390 berkas audio WAV IMA-ADPCM offline:

```bash
python tools/build_material_bank.py
python tools/generate_assets.py
```

---

## Spesifikasi skema database (`english_prep.db` v1 + DataStore)

Basis data lokal dikelola oleh **Room 2.7.2** (`exportSchema = true`, skema tersimpan pada [1.json](app/schemas/com.englishprep.ai.data.room.EnglishPrepDatabase/1.json)) dengan operasi `@Upsert` non-destruktif agar pembaruan status sesi maupun thread percakapan tidak memicu penghapusan `ON DELETE CASCADE` pada anak relasi.

### Database entity-relationship diagram

```mermaid
erDiagram
    exam_profiles ||--o{ course_modules : "owns"
    exam_profiles ||--o{ content_packs : "owns"
    content_packs ||--o{ passages : "contains"
    content_packs ||--o{ media_assets : "contains"
    content_packs ||--o{ questions : "contains"
    questions ||--o{ question_options : "has_options"
    exam_sessions ||--o{ user_answers : "records"
    exam_sessions ||--o{ evaluations : "scores"
    exam_sessions ||--o{ speech_recordings : "stores"
    chat_threads ||--o{ chat_messages : "contains"
```

### Daftar 17 entitas Room SQLite

| No | Tabel SQLite | Primary Key | Foreign Key / Indeks | Kolom Utama & Keterangan |
|---|---|---|---|---|
| 1 | `exam_profiles` | `id` (`String`) | - | `name`, `edition`, `scoringPolicyVersion`, `formatReferenceUrl` (profil `ITP_L1`, `IBT_2026`, `IELTS_ACADEMIC`). |
| 2 | `course_modules` | `id` (`String`) | FK `examId -> exam_profiles(id)` `CASCADE`; Index `examId`, `section` | `parentId`, `title`, `objective`, `theory`, `workedExample`, `commonMistake`, `difficulty`, `estimatedMinutes`, `sortIndex`, `license`, `provenance`, `audioScript`, `audioAssetPath`, `drillsJson`. |
| 3 | `curriculum_fts` | `rowid` (`@Fts4`) | Virtual table FTS4 | `moduleId`, `examId`, `title`, `body` untuk pencarian cepat RAG offline. |
| 4 | `content_packs` | `id` (`String`) | FK `examId -> exam_profiles(id)` `CASCADE`; Unique `(examId, id)` | `edition`, `title`, `theme`, `manifestVersion`, `status` (`SAMPLE`/`INCOMPLETE`/`READY`), `blueprintCount`, `availableCount`, `durationMinutes`, `blueprintDurationMinutes`, `license`, `provenance`, `importedAt`, `installedPath`, `checksum`, `audioBytes`. |
| 5 | `passages` | `id` (`String`) | FK `packId -> content_packs(id)` `CASCADE`; Index `packId` | `title`, `body`, `sourceLicense`, `readingOrder`. |
| 6 | `media_assets` | `id` (`String`) | FK `packId -> content_packs(id)` `CASCADE`; Index `packId` | `relativePathOrUri`, `kind`, `sha256`, `byteSize`, `durationMs`, `caption`, `license`. |
| 7 | `questions` | `id` (`String`) | FK `packId -> content_packs(id)` `CASCADE`; Index `(packId, section, sortIndex)` | `moduleId`, `section`, `taskType`, `prompt`, `passageId`, `audioAssetId`, `responseSchemaJson`, `correctAnswerJson`, `explanation`, `rationaleSource`, `difficulty`, `sortIndex`, `points`, `sourceLicense`. |
| 8 | `question_options` | `id` (`String`) | FK `questionId -> questions(id)` `CASCADE`; Index `questionId` | `label`, `text`, `sortIndex`. |
| 9 | `exam_sessions` | `id` (`String`) | Index `(examId, startAt)` | `examId`, `packId`, `mode` (`PRACTICE`/`STRICT`), `title`, `startAt`, `deadlineAt`, `endAt`, `lastSavedAt`, `state` (`ACTIVE`/`SUBMITTED`/`ABANDONED`), `elapsedMonotonicCheckpointMs`, `currentQuestionIndex`, `scorePolicyVersion`. |
| 10 | `user_answers` | `id` (`String`) | FK `sessionId -> exam_sessions(id)` `CASCADE`; Unique `(sessionId, questionId)` | `questionId`, `answerJson`, `updatedAt`, `isFlagged`, `submittedAt`, `autoCheckResultJson`. |
| 11 | `evaluations` | `id` (`String`) | FK `sessionId -> exam_sessions(id)` `CASCADE`; Index `sessionId` | `section`, `rawCorrect`, `rawTotal`, `scaledScore`, `officialEquivalent`, `estimateMethod`, `aiFeedbackJson`, `createdAt`. |
| 12 | `study_progress` | `id` (`String`) | Unique `(examId, moduleId)` | `examId`, `moduleId`, `attempts`, `correct`, `reviewedAt`, `masteryEstimate`, `updatedAt`. |
| 13 | `chat_threads` | `id` (`String`) | - | `examId`, `title`, `createdAt`, `updatedAt`. |
| 14 | `chat_messages` | `id` (`String`) | FK `threadId -> chat_threads(id)` `CASCADE`; Index `(threadId, createdAt)` | `role`, `text`, `citationsJson`, `modelId`, `createdAt`. |
| 15 | `model_installs` | `id` (`String`) | - | `displayName`, `family`, `modelVariant`, `runtime`, `format`, `quantization`, `sha256`, `sizeBytes`, `privateStoragePath`, `sourceDocumentUri`, `status`, `installedAt`, `licenseId`. |
| 16 | `speech_recordings` | `id` (`String`) | FK `sessionId -> exam_sessions(id)` `CASCADE`; Index `sessionId`, `questionId` | `questionId`, `privateFilePath`, `durationMs`, `transcript`, `feedbackJson`, `createdAt`. |
| 17 | `import_jobs` | `id` (`String`) | - | `kind`, `sourceUri`, `state`, `errorCode`, `createdAt`, `completedAt`. |

### Preferences DataStore (`english_prep_settings`)

Menyimpan preferensi ringan non-relasional: `selected_exam`, `language`, `audio_speed_practice`, `theme_mode`, `active_llm_model_id`, `active_stt_model_id`, `reduce_motion`, `haptics_enabled`, `reminder_enabled`, `reminder_hour`, `reminder_minute`, dan `font_scale_boost`.

---

## Ringkasan keamanan & DevSecOps

- **R8 + ProGuard Full Optimization**: Aktif pada build `release` ([`proguard-rules.pro`](app/proguard-rules.pro)) dengan `-repackageclasses 'o'`, redaksi `SourceFile`, dan stripping `android.util.Log` (`v`/`d`/`i`/`w`), mengurangi ukuran APK dari `200.6 MB` (debug) ke `181.11 MB` (unsigned release).
- **Kebijakan Privasi Offline & Backup**: `INTERNET` permission tidak pernah dideklarasikan di [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml), `android:allowBackup="false"`, serta eksklusi penuh `database`, `sharedpref`, dan `file` pada aturan backup XML.
- **RASP & Kriptografi Lokal**: `AndroidAppIntegrityInspector` membaca flag debuggable, status debugger, heuristik root dasar, status `allowBackup`, dan SHA-256 sertifikat penandatangan. Verifikasi paket eksternal bertanda tangan memakai `DetachedSignatureVerifier` (`SHA256withECDSA`), sementara kunci lokal memakai `AndroidKeystoreSealer` (`AES/GCM/NoPadding` pada `AndroidKeyStore`).
- **Catatan batas objektif**: Aplikasi yang berjalan 100% offline tanpa server tepercaya tidak dapat menjamin perlindungan mutlak terhadap pemodifikasian binary pada perangkat yang telah di-root. Kontrol di atas bertujuan meningkatkan transparansi integritas lokal dan biaya analisis balik.

---

## Daftar checklist implementasi & verifikasi

Dokumen rinci tersedia pada [VERIFICATION_CHECKLIST.md](docs/VERIFICATION_CHECKLIST.md), [EPIC_STATUS.md](docs/qa/EPIC_STATUS.md), audit lisensi pada [LICENSE_AND_SBOM_AUDIT.md](docs/qa/LICENSE_AND_SBOM_AUDIT.md), dan referensi model/dataset/repo di [REFERENCES.md](docs/REFERENCES.md).

- [x] **Spec-Driven Development**: Konstitusi Spec Kit (`.specify/memory/constitution.md`), 9 fitur terdokumentasi di `specs/001` sampai `specs/009`, serta 9 laporan tinjauan BMAD di `docs/reviews/`.
- [x] **Bank Materi PDF, 30 Modul & 15 Paket Penuh (1.725 Soal)**: 17 PDF Material Bank (`31` entri studi + indeks FTS4 di tab Materi), 30 modul (dengan bookmark & last-read), 90 drill interaktif, 15 paket soal berstatus `READY` penuh (`5x140` ITP, `5x120` iBT 2026, `5x85` IELTS Academic), serta 390 berkas audio MS IMA-ADPCM offline (`57.00 MB`).
- [x] **Mesin Inti Aplikasi**: `QuizGradingEngine` (10 tipe tugas + normalisasi `TFNG`/`YNNG`), `PackIntegrityValidator` (15 paket penuh valid), `ExamScoreEstimator` (skor mentah deterministik non-resmi `v1.1-raw-deterministic`), `Media3AudioEngine` (ExoPlayer `1.5.1` IMA-ADPCM + kontinuitas audio antarmulti-soal), `QuestionNavigatorSheetContent` (navigator terfilter 140 item + legenda), `WavAudioRecorder` + hapus rekaman, `WhisperOnnxSpeechEngine` (ORT `1.22.0`), `LiteRtLmGemma4Engine` (`0.13.1`), FTS4 RAG fallback (dengan lock Strict Mode), Room `@Upsert` DB upgrade non-destruktif + DataStore, `SafSecurityValidator`, dan `ProgressScreen`.
- [x] **UI/UX Responsif, Anti-AI-Slop & Ikon Adaptif (`specs/009`)**: Tata letak adaptif `320dp @ 2.0x` hingga tablet 2 kolom `840dp+` (`ResponsiveScreenContainer`), `FlowRow` CTA/bottom-bar anti-kliping, mikro-interaksi `SelectableOptionRow`/`AudioPlayerBar`/progress strip `3dp`/ekspansi Bank Materi, filter hasil `ResultsScreen`, switch baris penuh `toggleable`, angka tabular `tnum`, serta ikon adaptif + monochrome + 5 densitas mipmap (`docs/screenshots/app-icon-512.png`).
- [x] **DevSecOps, R8 & RASP**: `isMinifyEnabled = true`, `isShrinkResources = true`, ProGuard hardening, `allowBackup="false"`, ECDSA & AES-GCM, Workflow CI GitHub Actions (`.github/workflows/android-devsecops.yml`).
- [x] **Pengujian Terverifikasi**: 40/40 unit, DB migration/upgrade & Robolectric Compose UI tests lulus (`testDebugUnitTest`), `lintDebug` 0 error (`76` warning), `assembleDebug` & R8 `assembleRelease` sukses (`mapping.txt` terverifikasi), dan QA fisik `v1.1.1` pada `SM-G988B` (ikon adaptif, semua menu responsif, filter Materi/Results, `Ready • 140 soal`, playback IMA-ADPCM, navigator 140 item, Tutor AI Chat, R8 status, dan Bank Materi OER).
- [x] **Distribusi GitHub CLI**: Repository publik `Adrian463588/EnglishPrepAI`, deskripsi, topics, `README.md` dengan tautan unduhan & 13 tangkapan layar real-time + ikon aplikasi, serta GitHub Release `v1.1.1`.
