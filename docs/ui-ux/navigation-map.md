# Navigation Map & User Flow (`docs/ui-ux/navigation-map.md`)

- **Router Architecture**: Type-safe Navigation Compose (`AppRoute` destinations in `MainActivity.kt`), Single Activity + Material 3 `NavigationBar` on compact screens (`< 840dp`) and `NavigationRail` on expanded screens (`>= 840dp`).
- **Top-Level Navigation Destinations (`TopLevelDestination`)**:
  1. `Beranda` (`AppRoute.Home`)
  2. `Materi` (`AppRoute.Learn`)
  3. `Latihan` (`AppRoute.Practice`)
  4. `Progres` (`AppRoute.Progress`)
  5. `Profil` (`AppRoute.Profile`)

## Route Graph & WindowInsets Ownership

```text
Top-Level Scaffold (WindowInsets.safeDrawing.exclude(WindowInsets.ime))
 ├── Beranda (AppRoute.Home)
 │     ├── ExamSelector bottom sheet -> Switch active track (ITP L1 / iBT 2026 / IELTS Academic)
 │     ├── Resume / Start CTA -> Exam(sessionId)
 │     ├── Semua paket CTA -> Switch tab to Latihan (AppRoute.Practice)
 │     ├── Next Lesson row -> ModuleDetail(moduleId)
 │     └── Tutor AI row -> Chat(contextModuleId = null)
 ├── Materi (AppRoute.Learn)
 │     ├── Filter chips (Semua / Listening / Structure / Reading / Writing / Speaking / Bacaan OER)
 │     ├── Module row -> ModuleDetail(moduleId)
 │     └── OER digest action -> Chat(contextModuleId = sourceId)
 ├── Latihan (AppRoute.Practice)
 │     └── Mode Latihan / Full Mock Strict CTA -> Exam(sessionId)
 ├── Progres (AppRoute.Progress)
 │     ├── Recent Attempt row ("Hasil") -> Results(sessionId)
 │     ├── Weak Module row ("Pelajari") -> ModuleDetail(moduleId)
 │     └── Empty CTA -> Switch tab to Latihan (AppRoute.Practice)
 └── Profil (AppRoute.Profile)
       ├── Preferences (Language, Theme System/Light/Dark, Speed, Font Boost, Reduce Motion, Haptics, Daily Reminder)
       ├── Konten Offline -> SAF JSON/ZIP Pack Import
       ├── AI Offline ("Kelola Model AI") -> ModelManager
       ├── Keamanan & Integritas R8 (Runtime posture dialog)
       └── Backup / Restore / Delete Data / OSS Licenses

Full-Screen Secondary Scaffold Routes (Outer WindowInsets(0, 0, 0, 0); Inner Scaffold owns statusBars + navigationBars + imePadding)
 ├── ModuleDetail(moduleId) -> Bookmark toggle, Ask Tutor AI -> Chat(moduleId), Audio example playback
 ├── Exam(sessionId) -> Practice/Strict mode, AudioPlayerBar, Tonal Reading passage sheet, Single-row Navigator sheet, Writing Panduan Rubrik toggle, Speaking recorder & Whisper transcript, Submit -> Results(sessionId) (popUpTo Exam inclusive)
 ├── Results(sessionId) -> Objective score breakdown, Ulangi Soal Salah -> Exam(retrySessionId), Review filter (Semua / Salah / Dilewati / Benar), Pelajari Materi -> ModuleDetail(moduleId)
 ├── Chat(contextModuleId?) -> FTS offline retrieval & citation or Gemma 4 LiteRT-LM streaming, Bersihkan, link to ModelManager when unloaded
 └── ModelManager -> Runtime status banner, Gemma 4 E2B/E4B & Whisper Tiny.en catalog, SAF model import, SHA-256 verification, Activation & Delete confirmation
```
