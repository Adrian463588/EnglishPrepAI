# BMAD Review Report: Master UI/UX Redesign (`docs/reviews/bmad-ui-redesign.md`)

- **Scope**: Supporting multi-perspective BMAD review for `specs/ui-redesign/` (Product Analyst, Architect, UX Designer, QA Engineer). Authoritative backlog stays strictly in `specs/ui-redesign/tasks.md`.

## 1. Product Analyst Review
- **Goal Alignment**: Preserves 100% offline study & tryout scope for TOEFL ITP Level 1, TOEFL iBT (post-Jan 2026), and IELTS Academic, plus Gemma 4 & Whisper ONNX SAF workflows, while closing severe usability blockers uncovered during real-device keyboard/insets testing.
- **Anti-Slop Integrity**: Removing the duplicate SAF button in `PracticeScreen` and replacing raw IDs on `ProgressScreen` directly honors user task focus and plain learner-friendly Indonesian/English copy.

## 2. Software Architect Review
- **Root-Cause Diagnosis (`M-01`, `M-02`)**: Double inset handling (`MainActivity` outer `Scaffold` with `WindowInsets.safeDrawing` + inner `Scaffold` `TopAppBar` & `.imePadding()`) caused both ~40dp top bar duplication and complete IME body squash. Consolidating inset ownership by route destination fixes all 5 secondary routes without modifying any ViewModel, Room DAO, Media3, or Grading contract.
- **Performance Fix (`M-05`)**: Moving `showSwitcher` downstream of `HomeViewModel`'s `flatMapLatest` avoids re-subscribing to 5 Room flows every time the user toggles the exam switcher.

## 3. UX & Visual Design Review
- **FigCraft Source of Truth**: Establishing `design/tokens.json` + the 16-page `EnglishPrepAI-Redesign.figcraft` source anchors Light (`#FAF9F6`) & Dark (`#111415`) editorial roles, 4/8/12/16/24/32dp spacing rhythm, and single Deep Teal accent before Compose convergence.
- **Hierarchy & Micro-Fixes**: Compact bottom action labels (`Kembali` / `Periksa` / `Lanjut`) eliminate the mid-word wrap (`"Berikutny\na"`) measured at 375dp width on Android 16; `12dp` tonal Reading passage banner removes oversized pill shape distortion.

## 4. QA Engineer Review
- **Verification Matrix**: Live verification on real hardware MUST re-test the exact crashing/collapsing flows (`Chat` keyboard open, `Writing` keyboard open, `Results` & `Progress` after session submit, `Reading` bottom sheet, `360dp`/`412dp`/Tablet Robolectric tests, Light & Dark modes, and `aapt2` R8 binary checks).
