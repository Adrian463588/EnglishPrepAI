# Checklist Verifikasi Implementasi - English Prep AI

Status: Diverifikasi aktual pada **2026-11-17** (`v1.2.0` / `versionCode = 4` - JVM + Robolectric + Lint + Debug APK + R8 Release APK + Perangkat Fisik `SM-G988B` Android 13 `arm64-v8a` dalam Light & Dark Mode).

## 1. Spec-Driven Development & Arsitektur
- [x] **Spec Kit** terpasang & terisi: `.specify/memory/constitution.md`, `specs/001`..`010` + `specs/ui-ux-redesign` (`spec.md`, `audit.md`, `plan.md`, `tasks.md`, `acceptance.md`, `before-after.md`).
- [x] **BMAD Reviews**: `docs/reviews/bmad-{analyst-goals,architect-choices,ux-journeys,qa-coverage,security-devsecops,release-docs,epic-closure,material-bank-full-packs,uiux-responsive-icon,devsecops-ci-release,ui-ux-redesign}.md`.
- [x] **QA & SBOM Traceability Ledger**: `docs/qa/EPIC_STATUS.md` & `docs/qa/LICENSE_AND_SBOM_AUDIT.md` (menilai jujur item terverifikasi vs item yang memerlukan bobot `.litertlm`/`.onnx` eksternal, Switch Access fisik, atau tinjauan ahli manusia untuk bank soal).
- [x] **Clean MVVM + UDF + Single `:app` Module**: `ui -> domain <- data`, Hilt DI (`DatabaseModule`, `RepositoryModule`), DRY/KISS/SOLID/YAGNI/Ponytail.

## 2. Material Bank, Materi Lengkap & 15 Paket Soal Tryout Penuh
- [x] **Bank Materi PDF Offline (`material_bank/`)**: 17 PDF terverifikasi (1.652 halaman, `10` OER CC-BY/Public Domain ter-commit + `7` dokumen referensi resmi ETS/IELTS lokal via `.gitignore`) diekstrak ke `material_bank/extracted/*.md` dan `app/src/main/assets/material_bank/material_bank.json` (`31` entri studi + FTS4 RAG + pembaca di tab **Materi > Bacaan OER**).
- [x] **Susunan Materi Lengkap**: 30 modul terstruktur (`ITP-MOD-01..10`, `IBT-MOD-01..10`, `IELTS-MOD-01..10`) + 90 soal drill interaktif (`content/CURRICULUM.md`, `curriculum.json`).
- [x] **5 Paket Soal Penuh per Jenis Tes (15 Paket `READY`, 1.725 Butir)**:
  - TOEFL ITP Level 1 (`ITP-L1-01..05`): 5 x 140 = **700 butir** (`50` Listening, `40` Structure/WE, `50` Reading, `115` menit).
  - TOEFL iBT 2026 (`IBT26-01..05`): 5 x 120 = **600 butir** (`50` Reading, `47` Listening, `12` Writing, `11` Speaking, `90` menit).
  - IELTS Academic (`IELTS-A-01..05`): 5 x 85 = **425 butir** (`40` Listening, `40` Reading, `2` Writing, `3` Speaking Parts, `165` menit).
- [x] **Audio Listening Offline (IMA-ADPCM)**: 390 berkas WAV 16 kHz mono terkompresi MS IMA-ADPCM (`57.00 MB`) diputar lokal lewat Media3 ExoPlayer `1.5.1` (`ImaAdPcmOutputWriter`) dengan kontinuitas audio antarmulti-soal satu rekaman.
- [x] **Navigator Soal 140 Item**: `QuestionNavigatorSheetContent` scrollable berfilter seksi dengan target sentuh `>=48dp`.
- [x] **Diferensiasi Tes**: TOEFL ITP Level 1 tidak memaksakan Speaking/Writing ke ujian inti; TOEFL iBT 2026 & IELTS Academic memisahkan skala & tipe tugas masing-masing.

## 3. Mesin Kuis, Audio, Speaking, Database & Analitik
- [x] **Quiz Engine (`QuizGradingEngine`)**: Menilai deterministik 10 tipe soal (`CHOICE_SINGLE`, `CHOICE_MULTI`, `FILL_BLANK` + batas kata IELTS, `MATCHING`, `REORDER`, `ERROR_IDENTIFICATION`, `SENTENCE_BUILD`, serta status subyektif `WRITING_RESPONSE`, `SPEAKING_RESPONSE`, `LISTEN_REPEAT`).
- [x] **Scoring & Pembahasan (`ExamScoreEstimator`, `ResultsScreen`)**: Estimasi non-resmi ITP 310-677, iBT 2026 1-6 (dengan disclaimer adaptif), IELTS Band 0-9 + pembahasan terstruktur pembatas tipis + tautan modul + `Ulangi Soal Salah`.
- [x] **Audio Engine (`Media3AudioEngine`)**: ExoPlayer tunggal lifecycle-safe, audio focus, noisy receiver, dropdown kecepatan (`1.0x v`) & seek khusus mode Latihan, batas replay mode Strict.
- [x] **Room SQLite + FTS4 + DataStore**: 17 entitas/tabel terindeks, skema terekspor (`app/schemas/.../1.json`), `@Upsert` non-destruktif agar `user_answers`/`speech_recordings`/`chat_messages` tidak terhapus `ON DELETE CASCADE`, FTS4 offline search, DataStore preferensi.
- [x] **Progress Analytics (`ProgressScreen`)**: Hierarki 4 seksi (`Ringkasan`, `Performa per Seksi`, `Fokus Perlu Ditingkatkan`, `Riwayat Sesi`), grafik tren hanya untuk `>= 2` percobaan, modul lemah (`<80%`), ekspor/pulih JSON cadangan, hapus data.

## 4. AI Offline (Gemma 4 + Whisper ONNX + SAF) & Referensi
- [x] **SAF Import & Validasi Keamanan (`SafSecurityValidator`, `AiAndImportRepositoryImpl`, `ModelManagerScreen`)**: Dipisahkan dari Profil ke layar khusus **`ModelManagerScreen`** (`AppRoute.ModelManager`) dengan status `Terpasang`, expandable `Detail teknis`, katalog Hugging Face, serta SAF import `.litertlm`, `.json`, `.zip`, dan folder Whisper ONNX.
- [x] **Chatbot Gemma 4 & RAG (`LiteRtLmGemma4Engine`, `GroundedTutorPromptBuilder`, `ChatScreen`)**: Terhubung ke `com.google.ai.edge.litertlm:litertlm-android:0.13.1`, sanitasi prompt-injection, sitasi `Sumber: ...` bersih tanpa kurung JSON, tombol hentikan generasi, dan fallback FTS4 saat model belum dipasang. *(Eksekusi inferensi bobot `.litertlm` nyata pada perangkat memerlukan unduhan berkas `~2.6 GB` dari Hugging Face oleh pengguna lewat SAF).*
- [x] **Speaking Engine (`WavAudioRecorder`, `WhisperOnnxSpeechEngine`, `SpeakingMetricsAnalyzer`)**: Rekam 16 kHz mono WAV lokal, metrik WPM/filler/kemiripan kalimat, ekstraktor fitur 80-mel `[1, 80, 3000]`, runtime `onnxruntime-android:1.22.0` + `encoder*.onnx`/`decoder*.onnx`/`vocab.json` via SAF. *(Transkripsi bobot `.onnx` nyata memerlukan unduhan model Whisper ONNX oleh pengguna lewat SAF).*
- [x] **Katalog Referensi Bank Soal, Hugging Face & GitHub**: Terdokumentasi di `docs/REFERENCES.md` dan `ModelManagerScreen`.

## 5. DevSecOps, R8/ProGuard & RASP Integritas Lokal (`specs/005`, `specs/010`)
- [x] **R8 Full Mode + ProGuard + Resource Shrinking**: `isMinifyEnabled = true`, `isShrinkResources = true`, `proguard-android-optimize.txt`, `-repackageclasses 'o'`, `-renamesourcefileattribute "App"`, stripping `android.util.Log` (`v/d/i/w`), serta keep rule spesifik `@Serializable`/JNI/Room.
- [x] **Hygiene Signing & Backup**: `.gitignore` mengecualikan `*.jks`, `*.keystore`, `*.p12`, `keystore.properties`; `android:allowBackup="false"` + aturan eksklusi XML penuh; scan rahasia across `299` berkas terpantau `0` temuan.
- [x] **RASP Integritas Lokal & Kriptografi Offline**: `AndroidAppIntegrityInspector` tampil via modal **Profil -> Keamanan & Integritas R8**; `DetachedSignatureVerifier` (`SHA256withECDSA`) & `AndroidKeystoreSealer` (`AES/GCM/NoPadding`) lulus uji JVM (`SecurityHardeningTest`).
- [x] **Pipeline DevSecOps CI & Hardening R8 Release (`specs/010`)**: `.github/workflows/android-devsecops.yml` (`contents: read`, `gradlew` `100755`, credential scan, offline ML/Material Bank check, SDK 35, unit tests, lintDebug, R8 `assembleRelease`, inspeksi `aapt2`), `isDebuggable = false`, `isJniDebuggable = false`, `vcsInfo.include = false`, `usesCleartextTraffic="false"`, serta `aapt2` membuktikan 0 `INTERNET` & 0 `application-debuggable`.

## 6. UI/UX Anti-AI-Slop & FigCraft Redesign (`specs/ui-ux-redesign`, `specs/ui-redesign`)
- [x] **Perbaikan P0 System Bar Light/Dark Mode**: `SideEffect` di `MainActivity.kt` menjaga ikon status/nav bar kontras jelas pada mode Terang (`#FAF9F6`) maupun Gelap (`SM-G988B`: `15-home-light.png`, `16-profile-light.png`, `docs/screenshots/redesign/home-light.png`).
- [x] **Perbaikan P0 Keyboard (IME) & Double Top Inset (`M-01`, `M-02`)**: Pemisahan `contentWindowInsets` per rute di `MainActivity.kt` mencegah double status bar band (`y=95` bersih di seluruh 5 layar sekunder) dan mencegah keyboard menutup area pesan/esai pada `ChatScreen` (`chat-ime-reply.png`) serta `ExamScreen` Writing (`exam-writing-ime.png`).
- [x] **Editorial Anti-AI-Slop, FigCraft & Tap Budget**: Sumber desain resmi `design/EnglishPrepAI-Redesign.figcraft` + `design/tokens.json` + 16 ekspor `design/exports/*.png`; Home rata kiri `16dp` + `ExamSelector` semantik gabungan, aksi bottom bar ringkas (`Kembali`/`Periksa`/`Lanjut`/`Selesai`), banner Reading tonal + single-row `LazyRow` Navigator, resolusi judul modul asli di `ProgressScreen`, `Panduan Rubrik` opsional Writing, serta jalur model `Profil > Kelola Model AI`.
- [x] **Dokumentasi & Bukti Visual Redesign**: 8 deliverable di `docs/ui-ux/` (`audit.md`..`acceptance-checklist.md`), `specs/ui-redesign/*`, `docs/reviews/bmad-ui-redesign.md`, arsip `docs/screenshots/before/`, 16 screenshot `docs/screenshots/*.png`, dan 19 tangkapan layar regresi pasca-perbaikan di `docs/screenshots/redesign/`.
- [x] **Hasil Pengujian Terakhir**:
  - `.\gradlew.bat testDebugUnitTest --console=plain`: **47/47 lulus** (`0` gagal, `0` error, `0` dilewati - mencakup 21 Compose UI/a11y tests pada `w320dp @ 2.0f`, `w360dp`, `w412dp`, `w960dp`, Light & Dark mode, `ProgressScreen` weak module title, `Writing` rubric, dan `ChatScreen`).
  - `.\gradlew.bat lintDebug --console=plain`: **0 error**, `81` warning.
  - `.\gradlew.bat assembleDebug assembleRelease --console=plain`: `app-debug.apk` (`210,575,501` byte) & `app-release-unsigned.apk` (`189,925,386` byte + `mapping.txt` `62,316,644` byte) **sukses**; `aapt2` verified `versionCode='4' versionName='1.2.0'`, `0` `INTERNET`, `0` `application-debuggable`; secret scan `0` hits across `310` tracked text files.
  - QA perangkat fisik (`RRCN3008VYE` `SM-G988B`, Android 13 `arm64-v8a` & audit awal `QSWSEMRKNFZ9LJRC` Android 16): R8 Release signed QA diverifikasi di Mode Terang & Gelap serta Keyboard IME terbuka pada Chat & Writing, ITP/IELTS Progress/Results, Navigator & Passage sheet dengan `0` crash di `logcat`.

## 7. Repository GitHub, README & Release (`specs/006`, `specs/ui-ux-redesign`)
- [x] **Repository GitHub (`gh`)**: `https://github.com/Adrian463588/EnglishPrepAI`, deskripsi & topics lengkap, 16 screenshot aktual + `app-icon-512.png`, serta `README.md` versi `v1.2.0`.
- [x] **GitHub Release `v1.2.0`**: Aset `EnglishPrepAI-v1.2.0-universal-debug.apk` (`210,392,353` byte / `200.65 MB`, SHA-256 `0073C9875E9941D1E8E792ED43DE1C3C9F742E1403D116D460B0346EC55715DC`).
