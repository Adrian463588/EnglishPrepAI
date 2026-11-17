# Implementation Plan: FigCraft-Driven UI/UX Redesign (`specs/ui-redesign/plan.md`)

## Architecture & Workstream Sequence

1. **Stage 1 - Design Tokens & FigCraft Project (`design/`)**:
   - Author `design/tokens.json` with Light (`#FAF9F6`) & Dark (`#111415`) editorial Material 3 palettes, Deep Teal primary (`#0F6B66` / `#66D1CA`), spacing (`4/8/12/16/24/32`), radii (`4/8/12/16/24`), and typography scale.
   - Create `design/EnglishPrepAI-Redesign.figcraft` via FigCraft MCP (`create_node`, `batch`, `page.add`, `page.rename`, `variable.createCollection`, `variable.create`, `component.create`, `instance.create`, `save_file`, `file.export`, `screenshot`) containing all 16 organized pages (`00 — Foundations` .. `15 — States & Accessibility`).
   - Export key FigCraft pages (`00`, `01`, `03..15`) to `design/exports/` and visually inspect them via `read_file` against Phase 5–8 Anti-AI-Slop & Accessibility rules.

2. **Stage 2 - Targeted Jetpack Compose Fixes (`app/src/main/java/com/englishprep/ai/`)**:
   - **`MainActivity.kt`**: Fix root `Scaffold` `contentWindowInsets` + conditional padding so secondary screens (`Exam`, `ModuleDetail`, `Results`, `Chat`, `ModelManager`) stop receiving double `statusBars` & double `imePadding()` insets (`M-01`, `M-02`).
   - **`AppComponents.kt` & `HomeScreen.kt` / `HomeViewModel.kt`**: Merge `ExamSelector` semantics cleanly (`contentDescription = null` on icon, `Role.Button`), align `"Pelajaran berikutnya"` left margin via `contentPadding` parameter on `ModuleRow`, extract `showSwitcher` downstream of `flatMapLatest` (`M-05`).
   - **`LearnScreens.kt`**: Format `"Contoh Kerja"` speaker/answer turns onto readable lines when separated by ` | `.
   - **`PracticeScreen.kt`**: Remove redundant top `"Impor Paket Soal (SAF)"` button (consolidated under Profile -> Konten Offline) and refine package spacing (`M-06`).
   - **`ExamScreen.kt`**: Fix bottom action bar labels (`Kembali`, `Periksa`, `Lanjut`/`Selesai`) + `navigationBarsPadding()` + `imePadding()` without double consumption (`M-01`, `M-03`); convert Reading `"Baca Teks"` pill into sleek `12dp` tonal list trigger (`M-07`); convert Navigator filter chips into single-row `LazyRow` (`M-07`); add compact expandable Writing rubric disclosure (`M-08`); update Speaking hint string (`M-08`).
   - **`ProgressViewModel.kt` / `ProgressScreen.kt` / `ResultsScreen.kt`**: Resolve real `moduleTitle` for weak modules, human `ExamSection.label` everywhere, and clean `"Benar"` status badge (`M-04`).
   - **`ChatScreen.kt` / `ChatViewModel.kt`**: Shorten TopBar status subtitle so `"Bersihkan"` never clips it; update FTS fallback guidance from `Profil > AI Offline` to `Profil > Kelola Model AI` (`M-08`).

3. **Stage 3 - Visual Regression Loop & Automated QA (`Phase 10–11`)**:
   - Update `UiAndAccessibilityTest.kt` to test `ExamScreen` Writing + IME bounds, non-wrapping `w360dp` bottom controls (`Kembali`/`Lanjut`), `ProgressScreen` showing real module titles instead of `ITP-MOD-*`, and `HomeScreen` switcher reactivity.
   - Run `.\gradlew.bat testDebugUnitTest`, `.\gradlew.bat lintDebug`, `.\gradlew.bat assembleDebug assembleRelease`, sign Release APK locally for QA, install via `--no-incremental` on physical ADB device (`RRCN3008VYE`), capture screenshots across normal + **Keyboard Open (Writing & Chat)** + Dark/Light + Audio + Results + Progress + ModelManager, inspect side-by-side against FigCraft exports!
   - Produce all 8 required documents under `docs/ui-ux/` + update `specs/ui-redesign/{tasks,acceptance}.md`.
