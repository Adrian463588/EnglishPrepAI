# Specification: English Prep AI UI/UX Redesign (`specs/ui-redesign/spec.md`)

- **Status**: Approved for FigCraft-First Design & Regression Convergence (`2026-11-17`)
- **Authoritative Sources**: `AGENTS.md`, `PRD.md`, `DESIGN.md`, `ARSITEKTUR.md`, `DATA_SCHEMA.md`, `specs/ui-redesign/design-audit.md`
- **Reconciliation Note**: Incorporates working offline capabilities (`1,725` READY items, `30` curriculum modules, `31` OER Material Bank digests, `Media3 ExoPlayer` audio, `LiteRT-LM` Gemma 4, `Whisper ONNX` STT, `SAF`, `Room`, `R8` security posture) and previous `specs/ui-ux-redesign` baseline while elevating **FigCraft by ArtCraft (`design/EnglishPrepAI-Redesign.figcraft` + `design/tokens.json`)** as the visual design source of truth and resolving all critical physical-device audit defects (`M-01..M-08`).

## 1. Functional & UX Requirements

1. **FigCraft-First Source of Truth (`FR-FC01`)**:
   - Maintain a structured multi-page `.figcraft` document at `design/EnglishPrepAI-Redesign.figcraft` containing all 16 organized pages (`00 — Foundations` through `15 — States & Accessibility`), bound to Light/Dark variable collections in `design/tokens.json`.
   - Export visual references (`design/exports/*.png`) and inspect layout bounds, typography hierarchy, spacing (`4/8/12/16/24/32dp`), and contrast before implementing Compose changes.

2. **Keyboard, Insets & System Bar Integrity (`FR-IN01` - Fixes `M-01`, `M-02`)**:
   - No double `statusBars` or double `imePadding` insets between `MainActivity`'s root `Scaffold` and nested screen `Scaffold`s.
   - `ChatScreen` and `ExamScreen` (`Writing`, `FILL_BLANK`) MUST keep TopAppBar pinned cleanly below the status bar, scrollable message/prompt content visible, and composer / bottom bar positioned cleanly directly above the visible software keyboard.
   - Light and Dark modes keep high-contrast system status/navigation bar icons (`isAppearanceLightStatusBars = !useDark`).

3. **Editorial Layout & Information Hierarchy (`FR-ED01` - Fixes `M-03..M-08`)**:
   - **Home**: Aligned `16dp` vertical left grid across `"English Prep"`, merged `>=48dp` `ExamSelector`, focal primary CTA with real Room `X dari Y soal` progress, `"Progres belajar"`, flush `"Pelajaran berikutnya"`, and `"Tutor AI ->"` (`onOpenChat(null)`). Move `showSwitcher` downstream of `flatMapLatest`.
   - **Learn & Module Detail**: Single-row `LazyRow` section chips (`Structure`, `Bacaan OER`), clean divider rows, and editorial `ModuleDetailScreen` formatting pipe-separated worked examples cleanly with line breaks and keeping database IDs out of primary headers.
   - **Practice & Exam (Listening, Reading, Writing, Speaking)**: Remove duplicate SAF import button from Practice (keep in Profile); replace giant 2-line Reading pill with `12dp` tonal passage row + safe-padded sheet; make Navigator filter chips single-row `LazyRow`; use non-wrapping `>=48dp` bottom controls (`Kembali`, `Periksa`, `Lanjut`/`Selesai`); add compact expandable rubric disclosure in Writing; update deprecated `Profil > AI Offline` hints to `Profil > Kelola Model AI`.
   - **Progress & Results**: Map `moduleId -> CourseModuleItem.title` in `ProgressViewModel` and format section labels with `ExamSection.label` (no raw `ITP-MOD-10` or `LISTENING` enums visible on learner dashboards/cards); remove trailing period on `"Benar"`.
   - **AI Tutor Chat**: Shorten TopBar model-status subtitle so it never clips next to `Bersihkan`; update FTS offline note to guide users cleanly to `Profil > Kelola Model AI`; keep thread free of floating chips.
   - **Profile & Model Manager**: 4 clean preference groups on Profile (`Preferensi`, `Konten Offline`, `AI Offline`, `Privasi & Data`) + `AppRoute.ModelManager` preserving full SAF import, GGUF/zip/SHA-256 verification, delete confirmation, and expandable technical specs.

4. **Anti-AI-Slop & Preservation Guarantees (`FR-KP01`)**:
   - Preserve 100% offline execution (`0` `INTERNET` permission), ViewModel/Repository/Room/Media3/QuizGrading/SAF/Gemma4/Whisper contracts, and existing non-official exam disclaimers across TOEFL ITP, TOEFL iBT 2026, and IELTS Academic.
