# Checklist Verifikasi Implementasi — English Prep AI

Status: Diverifikasi aktual pada **2026-11-16** (JVM + Robolectric + Lint + Debug APK + Perangkat Fisik `SM-G988B` Android 13 `arm64-v8a`).

## 1. Spec-Driven Development & Arsitektur
- [x] **Spec Kit** terpasang & terisi: `.specify/memory/constitution.md`, `specs/001`..`007` (`spec.md`, `plan.md`, `tasks.md`, `acceptance.md`).
- [x] **BMAD Reviews**: `docs/reviews/bmad-{analyst-goals,architect-choices,ux-journeys,qa-coverage,security-devsecops,release-docs,epic-closure}.md`.
- [x] **QA & SBOM Traceability Ledger**: `docs/qa/EPIC_STATUS.md` & `docs/qa/LICENSE_AND_SBOM_AUDIT.md` (menilai jujur item terverifikasi vs item yang memerlukan bobot `.litertlm`/`.onnx` eksternal atau penulisan full-pack 140/120/85 butir).
- [x] **Clean MVVM + UDF + Single `:app` Module**: `ui -> domain <- data`, Hilt DI (`DatabaseModule`, `RepositoryModule`), DRY/KISS/SOLID/YAGNI/Ponytail.

## 2. Materi, Soal Tryout & Media Offline
- [x] **Susunan Materi Lengkap**: 18 modul terstruktur (`ITP-MOD-01..06`, `IBT-MOD-01..06`, `IELTS-MOD-01..06`) + 54 soal drill interaktif (`content/CURRICULUM.md`, `curriculum.json`).
- [x] **5 Paket Soal per Jenis Tes (15 Paket Total)**: `ITP-L1-01..05`, `IBT26-01..05`, `IELTS-A-01..05` (total 180 butir soal orisinal) dengan label jujur `Sample • 12/<blueprint> soal`.
- [x] **Audio Listening Offline**: 62 berkas WAV 16 kHz mono (`23.70 MB`) disintesis dan diuji putar offline lewat Media3 ExoPlayer `1.5.1`.
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
- [x] **Anti-AI-Slop & Micro-Interactions**: Desain editorial tenang beraksen *deep teal*, transisi `NavHost` + `AnimatedContent` antar soal + sorotan opsi animatif `SelectableOptionRow`, kontrol `hapticsEnabled` & *reduce motion* terhubung di `SemanticStatusColors`, tanpa gradien mencolok, tanpa deretan chip di atas pesan Chat, tanpa tombol mengambang di layar ujian, `imePadding()`, target sentuh `>=48dp`, *font scale* `2.0x` teruji.
- [x] **Izin & Offline-First**: `dumpsys package` memastikan `android.permission.INTERNET` **tidak** diminta (`RECORD_AUDIO`, `POST_NOTIFICATIONS`, `WAKE_LOCK`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED` saja); `RECORD_AUDIO` dan `POST_NOTIFICATIONS` diminta just-in-time.
- [x] **Hasil Pengujian Terakhir**:
  - `.\gradlew.bat testDebugUnitTest`: **34/34 lulus** (0 gagal, 0 error, 0 dilewati — termasuk `ToeflSessionFlowIntegrationTest`, `RoomIntegrityAndRecoveryTest`, `SecurityHardeningTest`, dan 8 `UiAndAccessibilityTest`).
  - `.\gradlew.bat lintDebug`: **0 error**, 78 warning.
  - `.\gradlew.bat assembleDebug` (`174,932,472` byte) & `.\gradlew.bat assembleRelease` (`154,405,955` byte — **menyusut ~20.5 MB via R8 + shrinkResources**): **sukses** dengan `mapping.txt` terverifikasi.
  - Uji perangkat fisik (`SM-G988B`, Android 13 `arm64-v8a`): R8 Release (`195–366 ms` cold start) memverifikasi Home polish, Bookmark & Last Read modul, Sample strict guard, Audio Media3 + transkrip mode Latihan, dialog konfirmasi belum dijawab, `ResultsScreen` skor mentah (`Skor Mentah: 7/12 (58%)` + `Dilewati: 1` + `v1.1-raw-deterministic`), `ProgressScreen` (`58%`, modul lemah `ITP-MOD-02..05`), dan status R8 tanpa crash di `logcat`.

## 7. Repository GitHub, README & Release (`specs/006`)
- [x] **Repository GitHub (`gh`)**: `https://github.com/Adrian463588/EnglishPrepAI` (publik, branch default `main`), terisi deskripsi resmi & 15 topics (`android`, `jetpack-compose`, `kotlin`, `toefl`, `ielts`, `offline-first`, `on-device-ai`, `gemma-4`, `whisper-onnx`, `r8-proguard`, `devsecops`, `clean-architecture`, `mvvm`, `room-database`, `spec-driven-development`).
- [x] **README.md & Tangkapan Layar Real-Time**: Tautan unduhan + versi `v1.0.0` di bagian paling atas, rincian proyek, 11 tangkapan layar perangkat fisik `SM-G988B` (`docs/screenshots/*.png`), struktur direktori, arsitektur Mermaid, cara pakai/build, spesifikasi 17 entitas Room + DataStore beserta diagram ER Mermaid, serta checklist verifikasi.
- [x] **GitHub Release `v1.0.0`**: Diterbitkan melalui `gh release create v1.0.0` dengan aset `EnglishPrepAI-v1.0.0-universal-debug.apk` (`174,821,885` byte, SHA-256 `FDBFD27005EF2CED76CC90C233F3806B0217996D9010DA9AC0775B118E0025E1`).
