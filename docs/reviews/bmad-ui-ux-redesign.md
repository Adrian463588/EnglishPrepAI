# BMAD Review — UI/UX Audit & Anti-AI-Slop Redesign (`specs/ui-ux-redesign`)

## Product Analyst
- Memastikan seluruh fitur inti (`TOEFL ITP Level 1`, `TOEFL iBT 2026`, `IELTS Academic`, 1.725 soal `READY`, 30 modul, 31 digest OER, Media3 IMA-ADPCM audio, Gemma 4 / FTS4 RAG, Whisper ONNX STT, SAF import, Room persistence, dan R8 DevSecOps) **dipertahankan 100%** sembari menghapus duplikasi copy dan kepadatan kartu yang menghambat kecepatan belajar.

## System Architect
- Pertahanan terhadap overengineering: menjaga single `:app` module, type-safe `AppRoute` (menambah `AppRoute.ModelManager` bersih di graph navigasi), mempertahankan `ViewModel -> StateFlow -> Compose` tanpa mengganti dependensi eksternal stabil (`navigation-compose`, Material 3 BOM).
- Memperbaiki sinkronisasi Edge-to-Edge sistem bar (`isAppearanceLightStatusBars` / `isAppearanceLightNavigationBars`) di `MainActivity` dan mengalirkan metrik progres sesi asli (`answered/total`) dari `SessionProgressRepository` ke `HomeViewModel`.

## Mobile UX & Accessibility Specialist
- Menghapus pola "card inside card" pada Exam, Results, Lesson Detail, Progress, dan Home.
- Memangkas filter Belajar menjadi 1 baris *horizontal scroll* bersih; memindahkan detail model Hugging Face & tombol SAF berat keluar dari beranda Profil ke layar khusus `Kelola Model AI`.
- Target sentuh minimum `48dp`, *tabular numerals* (`tnum`) untuk timer & skor, dukungan *font scale* `200%`, `reduceMotion`, dan kontras WCAG AA Light & Dark teruji langsung.

## QA Reviewer
- Tidak ada klaim PASS tanpa bukti aktual: setiap perbaikan harus dibuktikan oleh `testDebugUnitTest`, `lintDebug`, `assembleRelease` R8, serta perbandingan screenshot *Before/After* pada `SM-G988B` Android 13.
