# Checklist Verifikasi Implementasi — English Prep AI

Status: Diverifikasi aktual pada **2026-11-16** (`v1.1.1` / `versionCode = 3` — JVM + Robolectric + Lint + Debug APK + R8 Release APK + Perangkat Fisik `SM-G988B` Android 13 `arm64-v8a`).

## 1. Spec-Driven Development & Arsitektur
- [x] **Spec Kit** terpasang & terisi: `.specify/memory/constitution.md`, `specs/001`..`009` (`spec.md`, `plan.md`, `tasks.md`, `acceptance.md`).
- [x] **BMAD Reviews**: `docs/reviews/bmad-{analyst-goals,architect-choices,ux-journeys,qa-coverage,security-devsecops,release-docs,epic-closure,material-bank-full-packs,uiux-responsive-icon}.md`.
- [x] **QA & SBOM Traceability Ledger**: `docs/qa/EPIC_STATUS.md` & `docs/qa/LICENSE_AND_SBOM_AUDIT.md` (menilai jujur item terverifikasi vs item yang memerlukan bobot `.litertlm`/`.onnx` eksternal, Switch Access fisik, atau tinjauan ahli manusia untuk bank soal baru).
- [x] **Clean MVVM + UDF + Single `:app` Module**: `ui -> domain <- data`, Hilt DI (`DatabaseModule`, `RepositoryModule`), DRY/KISS/SOLID/YAGNI/Ponytail.

## 2. Material Bank, Materi Lengkap & 15 Paket Soal Tryout Penuh
- [x] **Bank Materi PDF Offline (`material_bank/`)**: 17 PDF terverifikasi (1.652 halaman, `10` OER CC-BY/Public Domain ter-commit + `7` dokumen referensi resmi ETS/IELTS lokal via `.gitignore`) diekstrak ke `material_bank/extracted/*.md` dan `app/src/main/assets/material_bank/material_bank.json` (`31` entri studi + FTS4 RAG + pembaca di tab **Materi**).
- [x] **Susunan Materi Lengkap**: 30 modul terstruktur (`ITP-MOD-01..10`, `IBT-MOD-01..10`, `IELTS-MOD-01..10`) + 90 soal drill interaktif (`content/CURRICULUM.md`, `curriculum.json`).
- [x] **5 Paket Soal Penuh per Jenis Tes (15 Paket `READY`, 1.725 Butir)**:
  - TOEFL ITP Level 1 (`ITP-L1-01..05`): 5 x 140 = **700 butir** (`50` Listening, `40` Structure/WE, `50` Reading, `115` menit).
  - TOEFL iBT 2026 (`IBT26-01..05`): 5 x 120 = **600 butir** (`50` Reading, `47` Listening, `12` Writing, `11` Speaking, `90` menit).
  - IELTS Academic (`IELTS-A-01..05`): 5 x 85 = **425 butir** (`40` Listening, `40` Reading, `2` Writing, `3` Speaking Parts, `165` menit).
- [x] **Audio Listening Offline (IMA-ADPCM)**: 390 berkas WAV 16 kHz mono terkompresi MS IMA-ADPCM (`57.00 MB`) diputar lokal lewat Media3 ExoPlayer `1.5.1` (`ImaAdPcmOutputWriter`) dengan kontinuitas audio antarmulti-soal satu rekaman.
- [x] **Navigator Soal 140 Item**: `QuestionNavigatorSheetContent`scrollable berfilter seksi dengan target sentuh >=48dp.
- [x] **Diferensiasi Tes**: TOEFL ITP Level 1 tidak memaksakan Speaking/Writing ke ujian inti; TOEFL iBT 2026 & IELTS Academic memisahkan skala & tipe tugas masing-masing.

## 3. Mesin Kuis, Audio, Speaking, Database & Analitik
- [x] **Quiz Engine (`QuizGradingEngine`)**: Menilai deterministik 10 tipe soal (`CHOICE_SINGLE`, `CHOICE_MULTI`, `FILL_BLANK` + batas kata IELTS, `MATCHING`, `REORDER`, `ERROR_IDENTIFICATION`, `SENTENCE_BUILD`, serta status subyektif `WRITING_RESPONSE`, `SPEAKING_RESPONSE`, `LISTEN_REPEAT`).
- [x] **Scoring & Pembahasan (`ExamScoreEstimator`, `ResultsScreen`)**: Estimasi non-resmi ITP 310–677, iBT 2026 1–6 (dengan disclaimer adaptif), IELTS Band 0–9 + pembahasan per butir + tautan modul + `Ulangi Soal Salah`.
- [x] **Audio Engine (`Media3AudioEngine`)**: ExoPlayer tunggal lifecycle-safe, audio focus, noisy receiver, kecepatan & seek khusus mode Latihan, batas replay mode Strict.
- [x] **Room SQLite + FTS4 + DataStore**: 17 entitas/tabel terindeks, skema terekspor (`app/schemas/.../1.json`), `@Upsert` non-destruktif agar `user_answers`/`speech_recordings`/`chat_messages` tidak terhapus `ON DELETE CASCADE`, FTS4 offline search, DataStore preferensi.
- [x] **Progress Analytics (`ProgressScreen`)**: Akurasi per seksi, grafik tren Canvas, daftar modul lemah (<80%) yang diperbarui otomatis saat `submitExam` maupun latihan modul, riwayat sesi, ekspor/pulih JSON cadangan, hapus data.

## 4. AI Offline (Gemma 4 + Whisper ONNX + SAF) & Referensi
- [x] **SAF Import & Validasi Keamanan (`SafSecurityValidator`, `AiAndImportRepositoryImpl`)**: `ACTION_OPEN_DOCUMENT` (`.litertlm`, `.json`, `.zip`) dan `ACTION_OPEN_DOCUMENT_TREE` (folder Whisper ONNX), penolakan 0-byte, mismatch format GGUF, zip-slip `../`, decompression bomb, cek disk, SHA-256, salin atomik ke `files/models/`.
- [x] **Chatbot Gemma 4 & RAG (`LiteRtLmGemma4Engine`, `GroundedTutorPromptBuilder`, `ChatScreen`)**: Terhubung ke `com.google.ai.edge.litertlm:litertlm-android:0.13.1`, sanitasi prompt-injection, kutipan `[ID]`, tombol hentikan generasi, dan fallback FTS4 saat model belum dipasang. *(Catatan uji: fallback FTS & kompilasi LiteRT-LM 0.13.1 terverifikasi; eksekusi inferensi bobot `.litertlm` nyata pada perangkat memerlukan unduhan berkas `~2.6 GB` dari Hugging Face oleh pengguna lewat SAF).*
- [x] **Speaking Engine (`WavAudioRecorder`, `WhisperOnnxSpeechEngine`, `SpeakingMetricsAnalyzer`)**: Rekam 16 kHz mono WAV lokal, metrik WPM/filler/kemiripan kalimat, ekstraktor fitur 80-mel `[1, 80, 3000]`, runtime `onnxruntime-android:1.22.0` + `encoder*.onnx`/`decoder*.onnx`/`vocab.json` via SAF. *(Catatan uji: fitur mel & kompilasi ORT terverifikasi; transkripsi dengan berkas bobot `.onnx` nyata memerlukan unduhan model Whisper ONNX oleh pengguna lewat SAF).*
- [x] **Katalog Referensi Bank Soal, Hugging Face & GitHub**: Terdokumentasi di `docs/REFERENCES.md` (termasuk `huggingface.co/collections/google/gemma-4` & repo resmi Android/Edge AI/STT) dan `Profile -> Katalog Rujukan Model Hugging Face`.

## 5. DevSecOps, R8/ProGuard & RASP Integritas Lokal (`specs/005`)
- [x] **R8 Full Mode + ProGuard + Resource Shrinking**: `isMinifyEnabled = true`, `isShrinkResources = true`, `proguard-android-optimize.txt`, `-repackageclasses 'o'`, `-renamesourcefileattribute "App"`, stripping `android.util.Log` (`v/d/i/w`), serta keep rule spesifik `@Serializable`/JNI/Room (`QuizGradingEngine -> o.Bd0`, `SafSecurityValidator -> o.ei0`, `AndroidAppIntegrityInspector -> o.a3` pada `mapping.txt`).
- [x] **Hygiene Signing & Backup**: `.gitignore` mengecualikan `*.jks`, `*.keystore`, `*.p12`, `keystore.properties`; `app/build.gradle.kts` mendukung `keystore.properties` opsional + `EXPECTED_RELEASE_SIGNING_SHA256` tanpa hardcode rahasia; `android:allowBackup="false"` + aturan eksklusi XML penuh (`ALLOW_BACKUP` hilang dari `dumpsys package`).
- [x] **RASP Integritas Lokal & Kriptografi Offline**: `AndroidAppIntegrityInspector` (`SecurityPostureEvaluator` — cek `FLAG_DEBUGGABLE`, debugger, SHA-256 sertifikat penandatangan, heuristik root, status `allowBackup`) tampil pada **Profil -> Keamanan & Integritas R8**; `DetachedSignatureVerifier` (`SHA256withECDSA`) & `AndroidKeystoreSealer` (`AES/GCM/NoPadding`) lulus uji JVM (`SecurityHardeningTest`). *(Catatan: pemeriksaan integritas sisi-klien offline berfungsi sebagai transparansi lokal; proteksi absolut tetap memerlukan kontrol server bila online).*
- [x] **Pipeline DevSecOps CI**: `.github/workflows/android-devsecops.yml` (hygiene rahasia/manifest, `testDebugUnitTest`, `lintDebug`, `assembleRelease` R8 gate).

## 6. UI/UX Best Practices, Tap Budget & Pengujian Aktual
- [x] **Tap Budget dari Beranda**: `<=1` tap ke Lanjutkan/Mulai Paket Utama & Tanya AI; `2` tap ke Ganti Jenis Ujian atau Mulai Paket Tryout; `<=3` tap ke Detail Modul Materi.
- [x] **Anti-AI-Slop, Responsivitas & Ikon Aplikasi (`specs/009`)**: Desain editorial tenang beraksen *deep teal*, ikon aplikasi adaptif + monochrome + 5 densitas mipmap (`ic_launcher*.xml`, `mipmap-{mdpi..xxxhdpi}/ic_launcher*.png`, `docs/screenshots/app-icon-512.png`), `ResponsiveScreenContainer` & tata letak 2 kolom (`840dp+` Home/Practice/Progress), `FlowRow` CTA/bottom-bar anti-kliping untuk `320dp @ 2.0x`, mikro-interaksi (`SelectableOptionRow` press scale + ripple, `AudioPlayerBar` speed pills, progress strip `3dp`, `MaterialBankRow` animasi ekspansi + chevron), eksklusivitas chip `Semua`/`Tersimpan`, filter chip `ResultsScreen`, switch row `toggleable`, angka tabular `tnum`, `imePadding()`, target sentuh `>=48dp`.
- [x] **Izin & Offline-First**: `dumpsys package` memastikan `android.permission.INTERNET` **tidak** diminta (`RECORD_AUDIO`, `POST_NOTIFICATIONS`, `WAKE_LOCK`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED` saja); `RECORD_AUDIO` dan `POST_NOTIFICATIONS` diminta just-in-time.
- [x] **Hasil Pengujian Terakhir**:
  - `.\gradlew.bat testDebugUnitTest`: **40/40 lulus** (0 gagal, 0 error, 0 dilewati — termasuk `AssetSeederAndFtsTest`, `ToeflSessionFlowIntegrationTest`, `RoomIntegrityAndRecoveryTest`, `SecurityHardeningTest`, dan 14 Compose `UiAndAccessibilityTest` termasuk uji `w320dp-h640dp @ 2.0f`, `w960dp-h720dp`, filter chip hasil, switch baris penuh, dan teks `1.725 butir READY`).
  - `.\gradlew.bat lintDebug`: **0 error**, 76 warning (`IconLauncherShape` & `UnusedResources` nol).
  - `.\gradlew.bat assembleDebug` (`210,344,064` byte / `200.6 MB`, SHA-256 `A72C06A5C38E3A81463B6F66CBC9ED27D780BEFDB373366092426570956DF0CE`) & `.\gradlew.bat assembleRelease` (`189,909,942` byte / `181.11 MB` — **menyusut ~19.5 MB via R8 + shrinkResources**): **sukses** dengan `mapping.txt` (`61,494,600` byte) terverifikasi.
  - Uji perangkat fisik (`SM-G988B`, Android 13 `arm64-v8a`): instalasi `v1.1.1` (`versionCode = 3`) R8 Release mengonfirmasi Home, Materi chip eksklusif & Bank Materi OER `Baca v`, Latihan `Ready • 140 soal`, Exam timer tabular & progress strip `3dp`, Navigator legend (`Aktif`, `Terjawab`, `* Ditandai ragu`), Results `1/140 (1%)` + FilterChip `Semua`/`Salah`/`Dilewati`/`Benar`, Tutor AI Chat ellipsis & auto-scroll, Progress `fillMaxWidth`, serta Profil `R8 Aktif` & teks `1.725 butir READY` dengan nol crash di `logcat`.

## 7. Repository GitHub, README & Release (`specs/006`, `specs/008`, `specs/009`)
- [x] **Repository GitHub (`gh`)**: `https://github.com/Adrian463588/EnglishPrepAI` (publik, branch default `main`), deskripsi & topics terisi, 13 screenshot nyata + `app-icon-512.png` di `docs/screenshots/`, `README.md` versi `v1.1.1`.
- [x] **GitHub Release `v1.1.1`**: Diterbitkan melalui `gh release create v1.1.1` dengan aset `EnglishPrepAI-v1.1.1-universal-debug.apk` (`210,344,064` byte / `200.6 MB`, SHA-256 `A72C06A5C38E3A81463B6F66CBC9ED27D780BEFDB373366092426570956DF0CE`).
