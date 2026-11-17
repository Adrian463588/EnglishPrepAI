# Final UI/UX Redesign Acceptance Checklist (`docs/ui-ux/acceptance-checklist.md`)

**Date**: 2026-11-17  
**Spec Source**: `specs/ui-redesign/spec.md`, `specs/ui-redesign/acceptance.md`, `docs/reviews/bmad-ui-redesign.md`

## 1. Design & Specification Gate
- [x] **SPEC-01**: `specs/ui-redesign/` (`design-audit.md`, `spec.md`, `plan.md`, `tasks.md`, `acceptance.md`) and `docs/reviews/bmad-ui-redesign.md` documented cleanly.
- [x] **FIG-01**: Official **FigCraft** project `design/EnglishPrepAI-Redesign.figcraft` (`43,258` bytes) + `design/tokens.json` (`Light`/`Dark` collections + Spacing/Radius variables) + 16 visually verified exports under `design/exports/00-foundations.png`..`15-states-accessibility.png`.

## 2. Live Mobile MCP Defects Resolved & Verified (`RRCN3008VYE`)
- [x] **M-01 (P0)**: Double IME collapse eliminated on `ChatScreen` (`chat-ime-reply.png`) and `ExamScreen` Writing (`exam-writing-ime.png`) with keyboard visible.
- [x] **M-02 (P1)**: Double status-bar top gap eliminated on all 5 secondary screens (`ModuleDetail`, `Exam`, `Results`, `ModelManager`, `Chat`).
- [x] **M-03 (P1)**: Exam action bar (`Kembali` / `Periksa` / `Lanjut` / `Selesai`) enforces single-line text + >=48dp touch height.
- [x] **M-04 (P1)**: `ProgressScreen` resolves curriculum titles (`moduleId -> moduleTitle`), `ResultsScreen` / `ProgressScreen` use mixed-case `ExamSection.label`, `"Benar"` cleaned.
- [x] **M-05 (P1)**: Home vertical alignment (`16dp` / `x=42`) flush across all rows, `ExamSelector` accessibility semantics merged (`Role.Button`), `HomeViewModel.showSwitcher` moved downstream of `flatMapLatest`.
- [x] **M-06 (P1)**: Duplicate top SAF import CTA removed from `PracticeScreen`; SAF pack import retained in `Profile > Konten Offline`.
- [x] **M-07 (P1)**: Reading tonal `Baca teks:` banner, single-row `LazyRow` Navigator chips (`Structure`), and `navigationBarsPadding()` on bottom sheets verified live.
- [x] **M-08 (P2)**: Model hints updated to `Profil > Kelola Model AI`, unclipped Chat header (`Mode FTS aktif • Kelola model`), optional Writing `Panduan Rubrik` toggle added.

## 3. Automated Build, Test, Security & Binary Verification
- [x] **TEST-01**: `.\gradlew.bat testDebugUnitTest --console=plain` passed `47/47` tests (`0` failures, `0` errors, `0` skipped), including all `21` Compose UI & Accessibility tests in `UiAndAccessibilityTest`.
- [x] **LINT-01**: `.\gradlew.bat lintDebug --console=plain` passed (`0` errors/fatal, `81` warnings).
- [x] **R8-01**: `assembleRelease` built R8-minified APK (`versionCode = 4`, `versionName = 1.2.0`); `aapt2 dump permissions` & `aapt2 dump badging` verified `0` `INTERNET` permission and `0` `application-debuggable`.
- [x] **SEC-01**: Secret/credential scan across `310` tracked text files returned `0` hits; `adb -s RRCN3008VYE logcat -d` recorded `0` crashes / fatal exceptions.
- [x] **DOC-01**: All 8 required documents written under `docs/ui-ux/` + Phase 10 live screenshots stored under `docs/screenshots/redesign/`.

## 4. Open External Prerequisites (Preserved Unchecked Truthfully)
- [ ] **E07-04 / E10-05 / E17-04 / E17-05 / E21-04**: Real on-device `.litertlm` (Gemma 4 E2B/E4B) inference benchmarks, real `.onnx` Whisper transcription QA, TalkBack physical traversal sign-off by a human tester, and 7-day soak stability log require external multi-GB model weights / manual device sign-off outside automated repo checks.
