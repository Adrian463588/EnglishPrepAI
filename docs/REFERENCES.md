# Referensi Bank Soal, Model AI Offline (Hugging Face), dan Repository GitHub

Dokumen ini merangkum referensi **terverifikasi** yang relevan secara teknis dan legal untuk pengembangan lanjutan **English Prep AI**. Tidak ada soal berhak cipta ETS/British Council/Cambridge yang disalin ke aplikasi; seluruh 15 paket bawaan dibuat orisinal dan diberi status `Sample`.

---

## 1. Model AI Offline (Hugging Face -> Import via SAF)

### A. Gemma 4 Chatbot & Writing/Speaking Coach (`.litertlm` via LiteRT-LM `0.13.1`)
- **Koleksi Resmi Gemma 4 (Hugging Face)**: `https://huggingface.co/collections/google/gemma-4` (dirilis 2 April 2026)

| Model | Repository Hugging Face | Format & Kuantisasi | Estimasi Ukuran & RAM | Catatan Teknis |
|---|---|---|---|---|
| **Gemma 4 E2B IT** *(Disarankan untuk HP 6–8 GB RAM)* | `https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm` | `.litertlm` (`gemma-4-E2B-it.litertlm`, mixed 2/4/8-bit) | ~2.58 GB disk, RAM runtime ~1.5–2.2 GB | Didukung native oleh `com.google.ai.edge.litertlm:litertlm-android:0.13.1` (CPU XNNPack / GPU) |
| **Gemma 4 E4B IT** *(Untuk HP 8–12+ GB RAM)* | `https://huggingface.co/litert-community/gemma-4-E4B-it-litert-lm` | `.litertlm` (quantized mobile bundle) | ~4+ GB disk, RAM runtime ~3+ GB | Lebih kuat untuk evaluasi esai IELTS Task 2 & iBT Academic Discussion |

### B. Whisper ONNX Offline Speech-to-Text (`.onnx` via ONNX Runtime `1.22.0`)
| Model | Repository Hugging Face | File yang Dibutuhkan di Folder SAF | Estimasi Ukuran | Catatan Teknis |
|---|---|---|---|---|
| **Whisper Tiny.en ONNX** *(Disarankan untuk STT cepat offline)* | `https://huggingface.co/onnx-community/whisper-tiny.en` (atau `https://huggingface.co/Xenova/whisper-tiny.en`) | `encoder_model.onnx` (atau quantized int8), `decoder_model.onnx`, `vocab.json` (plus `merges.txt` bila tersedia) | ~75–150 MB | Input 16 kHz mono WAV -> 80-mel spectrogram `[1, 80, 3000]` -> decode teks Inggris |
| **Sherpa-ONNX Whisper Tiny.en** *(Alternatif bila memakai AAR `k2-fsa/sherpa-onnx`)* | `https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny.en` | `tiny.en-encoder.int8.onnx`, `tiny.en-decoder.int8.onnx`, `tiny.en-tokens.txt` | ~55 MB | Teroptimasi C++ Sherpa-ONNX + Silero VAD |

---

## 2. Bank Soal, Dataset Terbuka, dan Panduan Format Resmi

> **Peringatan Lisensi**: Jangan menyalin bank soal berhak cipta ETS TOEFL atau Cambridge IELTS ke dalam repositori publik/komersial. Gunakan sumber resmi hanya untuk mengkalibrasi struktur/blueprint, dan gunakan dataset terbuka atau paket pihak ketiga berlisensi sah melalui importer SAF.

| Sumber | URL / ID | Cakupan & Lisensi | Pemanfaatan yang Tepat |
|---|---|---|---|
| **ETS TOEFL ITP Test Content** | `https://www.ets.org/toefl/itp/test-content.html` | Blueprint resmi Listening (50), Structure (40), Reading (50), skala 310–677 | Acuan jumlah soal, durasi, dan jenis keterampilan (bukan disalin soalnya) |
| **ETS TOEFL iBT Content (Post Jan 21, 2026)** | `https://www.ets.org/toefl/test-takers/ibt/about/content.html?version=after-jan-2026` | Format adaptif 2026: Complete the Words, Daily Life, Build a Sentence, Write an Email, Academic Discussion, Listen & Repeat, Take an Interview, skala 1–6 | Acuan tipe soal modern iBT 2026 |
| **IELTS Academic Format** | `https://ielts.org/take-a-test/test-types/ielts-academic-test` | Listening 4 parts (40), Reading 3 passages (40), Writing Task 1 & 2, Speaking Parts 1–3, band 0–9 | Acuan rubrik dan variasi soal (TFNG, Matching, Summary Completion) |
| **TOEFL-QA (Spoken QA)** | `https://github.com/iamyuanchung/TOEFL-QA` | Korpus akademik spoken comprehension & MCQ (cek lisensi repo sebelum distribusi) | Referensi pola pertanyaan *lecture comprehension* |
| **ETS TOEFL-Spell** | `https://github.com/EducationalTestingService/TOEFL-Spell` | Korpus anotasi kesalahan ejaan esai non-native (riset) | Referensi latihan *error identification* & koreksi ejaan |
| **IELTS Essay Scoring Dataset** | `https://huggingface.co/KevSun/IELTS_essay_scoring` | Kumpulan esai + skor sub-kriteria (TA/CC/LR/GRA) | Referensi kalibrasi rubrik evaluasi Writing (non-resmi) |
| **TOEFL Synonym Match (`wordlink`)** | `https://huggingface.co/datasets/Genius-Society/wordlink` | Pasangan sinonim kosakata TOEFL | Referensi latihan kosakata & *vocabulary-in-context* |

---

## 3. Repository GitHub Referensi Arsitektur (Terverifikasi)

1. **`rockerritesh/kitten-tts-android`** (`https://github.com/rockerritesh/kitten-tts-android`) — Referensi utama pipeline audio lokal Kotlin + ONNX Runtime + Whisper speech recognition & evaluasi bacaan speaking.
2. **`winstonhoyle/practice-speaking`** (`https://github.com/winstonhoyle/practice-speaking`) — Referensi Android Kotlin + Jetpack Compose + Room + MVVM untuk percakapan speaking tutor dengan AI & TTS.
3. **`bmo1177/SRLappMobile`** (`https://github.com/bmo1177/SRLappMobile`) — Referensi Kotlin + Jetpack Compose untuk latihan berbahasa, AI practice, dan algoritma *spaced repetition*.
4. **`a1mohamad/toefl-vocabs-android-app`** (`https://github.com/a1mohamad/toefl-vocabs-android-app`) — Referensi Android Kotlin + Jetpack Compose offline untuk TOEFL vocabulary dengan prioritas kata terlemah dan automated Compose UI screenshot test.
5. **`Luxshan2000/ielts-prep` (BandReady)** (`https://github.com/Luxshan2000/ielts-prep`) — Referensi alur simulasi IELTS lengkap (Listening, Reading, Writing, Speaking) dan prompt rubrik AI lokal.
6. **`maxwell1500/OpenLingo`** (`https://github.com/maxwell1500/OpenLingo`) — Referensi arsitektur *quiz engine* multi-tipe soal (listening, matching, fill-blank, adaptive drill).
7. **`tianjianfeng/VocabApp`** (`https://github.com/tianjianfeng/VocabApp`) — Referensi Compose + Room + DataStore sederhana untuk progres belajar & flashcard offline.
8. **`MoHamed-B-M/flambo`** (`https://github.com/MoHamed-B-M/flambo`) — Referensi rekaman suara lokal + STT offline di Jetpack Compose.
9. **`tomerdvir/toefl`** (`https://github.com/tomerdvir/toefl`) & **`aimerfeng/ists`** (`https://github.com/aimerfeng/ists`) — Referensi timer simulasi, review hasil, dan analitik mock test.

---

## 4. Repository Resmi Android & On-Device Runtime (Referensi Teknis Utama)

- **`android/nowinandroid`** (`https://github.com/android/nowinandroid`) — Referensi arsitektur Clean MVVM, Material 3, Room, Hilt, dan Gradle conventions resmi Android.
- **`android/compose-samples`** (`https://github.com/android/compose-samples`) & **`android/media-samples`** (`https://github.com/android/media-samples`) — Referensi Compose state management, adaptive layout, dan Media3 ExoPlayer audio playback.
- **`google-ai-edge/ai-edge-gallery`** (`https://github.com/google-ai-edge/ai-edge-gallery`) & **`google-ai-edge/LiteRT`** (`https://github.com/google-ai-edge/LiteRT`) — Referensi runtime `.litertlm` Gemma 4 E2B/E4B lokal di Android.
- **`k2-fsa/sherpa-onnx`** (`https://github.com/k2-fsa/sherpa-onnx`) & **`ggerganov/whisper.cpp`** (`https://github.com/ggerganov/whisper.cpp`) — Referensi eksekusi Whisper ONNX/native STT dan VAD offline di Android.

---

## 5. Material Bank PDF Terverifikasi (`material_bank/`)

Hasil crawling dan dorking terverifikasi (`tools/fetch_material_bank.py`, `material_bank/MANIFEST.json`) menghasilkan **17 dokumen PDF** (total 1.652 halaman -> **31** kutipan materi offline di tab **Materi** & indeks FTS4):
- **10 PDF Open OER & Public Domain (`material_bank/open_oer_pdfs/`, di-commit ke repo)**:
  - Grammar & Sentence Craft (`CC BY 4.0` / `CC BY-NC-SA 4.0`): LibreTexts *High-Intermediate Academic Grammar*, *ESOL Advanced Grammar*, *ESL Grammar The Way You Like It*.
  - Academic Reading, Writing, Listening & Speaking (`CC BY 4.0`): LibreTexts *Intermediate Reading & Writing*, *ESL 304 Reading & Vocab*, *Iowa State Academic Writing Mod 1*, *Listening & Speaking*, *USA Journals Listening Strategies*.
  - Science Passages (`Public Domain US Gov`): NOAA *Deep-Sea Corals Fact Sheet*, NASA *Glacier Ice Mass Fluctuations*.
- **7 PDF Spesifikasi Resmi Publik (`material_bank/official_reference_pdfs/`, dikecualikan dari Git via `.gitignore` per Rule 8)**:
  - ETS *TOEFL ITP Test Taker Handbook*, *TOEFL iBT Specifications (Jan 21, 2026)*, *TOEFL iBT Overview*, *ERIC EJ1487502 Technical Manual*, *IELTS Writing & Speaking Band Descriptors*, dan *IELTS-TOEFL Concordance 2026* dipakai khusus untuk kalibrasi jumlah soal, waktu, format tugas, dan deskriptor rubrik.
