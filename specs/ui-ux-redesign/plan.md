# UI/UX Redesign Architecture & Implementation Plan (`specs/ui-ux-redesign/plan.md`)

## Core Strategy
- **Zero Business-Logic Disruption**: Preserve all Room schemas, FTS4 RAG indexing, deterministic `QuizGradingEngine`, `ExamScoreEstimator`, `Media3AudioEngine`, `WavAudioRecorder`, `WhisperOnnxSpeechEngine`, `LiteRtLmGemma4Engine`, `SafSecurityValidator`, and R8 security posture.
- **Restrained Editorial Material 3 System** (`docs/design-system.md`):
  - Palette: Warm Paper light (`#FAF9F6`) / Slate Dark (`#111415`) + single Deep Teal accent (`#0F6B66` / `#66D1CA`).
  - Surface Discipline: Replace box-inside-box borders with **typographic hierarchy + 8dp/16dp whitespace + quiet `HorizontalDivider`s**, reserving tinted containers only for primary focal CTAs or semantic feedback.
  - Spacing Scale: Strictly `AppSpacing` (`4 / 8 / 12 / 16 / 24 / 32 dp`), `16–20 dp` default screen margins.
  - Motion: Subtle feedback (`120–175 ms`) via `MotionTokens`, honored `reduceMotion` toggle across every animation.

## Implementation Sequence (Vertical Slices)
1. **Slice 1 — Design System, Edge-to-Edge Fix & Shared Components (`Theme.kt`, `Tokens.kt`, `MainActivity.kt`, `AppDestinations.kt`, `AppNavigation.kt`, `AppComponents.kt`)**:
   - Fix P0 light-mode status/nav bar contrast in `MainActivity.kt` (`SideEffect` syncing `WindowCompat.getInsetsController` with `!useDark`).
   - Add `AppRoute.ModelManager` under navigation graph (`onOpenModelManager`).
   - Refactor `AppComponents.kt`:
     - Clean compact `ExamSelector` (inline tappable pill/row instead of oversized outlined box).
     - Clean `ModuleRow` (concise section eyebrow-free metadata line below title + divider).
     - Compact `AudioPlayerBar` (single-row play + friendly title + scrubber/timestamps + compact dropdown menu for speed in Practice mode — no giant stacked card borders).
     - Borderless `QuestionPromptBlock` / clean `QuestionCard` without outer double border over `SelectableOptionRow`.
     - Shared `SettingActionRow` / `PreferenceSectionHeader` list primitives.
2. **Slice 2 — Home (`HomeViewModel.kt`, `HomeScreen.kt`)**:
   - Compute real `activeSessionAnsweredCount` and `activeSessionTotalCount` in `HomeViewModel` from `SessionProgressRepository.observeAnswers` + pack `availableCount`.
   - Rebuild `HomeScreen` to match clean editorial flow: Title + compact `[Exam ▾]`, focal Continue/Start surface (`title`, real `"X dari Y soal"` progress, single primary CTA, quiet secondary `"Lihat semua paket"`), divider row `Pelajaran berikutnya`, clean `Progres belajar` metric (when attempts > 0), and clean `Tutor AI ->` row (`contextSourceId = null`).
3. **Slice 3 — Learn & Module Detail (`LearnViewModel.kt`, `LearnScreens.kt`)**:
   - Single-row `LazyRow` filter bar with short labels (`Semua`, `Listening`, `Structure`, `Reading` / `Writing`, `Speaking`, `Tersimpan`, `Bacaan OER`).
   - Default view shows concise curriculum divider rows (`Section`, concise `Title`, `Difficulty · Min` metadata, `mastery%` if earned). Switching to `Bacaan OER` reveals Material Bank digests cleanly with expand animation & Tutor link.
   - Refactor `ModuleDetailScreen` to clean readable editorial prose sections + dividers instead of 4 identical cards, human-readable title in `TopAppBar`, and clean drill layout without raw enums/IDs.
4. **Slice 4 — Practice & Exam (`PracticeScreen.kt`, `ExamScreen.kt`)**:
   - Clean `PracticeScreen` header + compact SAF import action + scannable pack list (`Sample`/`Ready` badge kept honest).
   - Redesign `ExamScreen`: clean header (`Soal X dari Y` left, tabular timer right), thin progress strip, compact `AudioPlayerBar` (human title without internal `ITP01-L01`), borderless question block with whitespace above response widgets, Practice-only `[Periksa]` button separated clearly from `[Sebelumnya]` and `[Berikutnya]`/`[Kumpulkan]`.
5. **Slice 5 — Profile, Dedicated `ModelManagerScreen`, Chat, Progress & Results**:
   - Restructure `ProfileScreen` into 4 logical divider-grouped sections: `Preferensi`, `Konten Offline`, `AI Offline` (`Kelola Model AI >` -> `ModelManagerScreen`, `Keamanan & Integritas R8 >` modal/expandable), `Privasi & Data`.
   - Create `ModelManagerScreen` in `ui/feature/profile/ModelManagerScreen.kt` (Installed models + Available HF models with expandable technical details + single anchored `[Impor model dari perangkat]` action sheet for Gemma `.litertlm` / Whisper `.onnx` folder).
   - Clean `ChatScreen` (clean TopBar without competing `"Impor AI"` cramp, formatted citation labels without raw JSON brackets, inline link to `ModelManager` when model unloaded).
   - Clean `ProgressScreen` (strict 4-section hierarchy, sample sizes shown, no boilerplate) and `ResultsScreen` (divider-separated review rows removing nested cards).
6. **Slice 6 — Testing, Multi-Viewport Screenshots, Mobile MCP / ADB QA & Final Documentation**:
   - Update `UiAndAccessibilityTest.kt` for redesigned labels, Profile -> ModelManager split, Home real progress, Learn single-row filter, Exam clean controls, Light/Dark contrast, `w360dp`, `w412dp`, `w840dp` tablet, and `2.0x` font scaling.
   - Run full Gradle verification (`testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleRelease`), install R8 Release on `SM-G988B`, capture before/after screenshots across Dark & Light modes, verify zero crashes, author `docs/design-system.md`, `docs/ui-audit.md`, `docs/ux-before-after.md`, and complete traceability docs.
