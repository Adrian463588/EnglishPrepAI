# Implementation Plan: Progress Analytics, UI/UX Polish & Accessibility

**Branch**: `004-progress-analytics-uiux-a11y` | **Date**: 2026-11-16 | **Spec**: `specs/004-progress-analytics-uiux-a11y/spec.md`

## Technical Context
- **Theme & Tokens**: `ui/theme/{Color.kt,Type.kt,Shape.kt,Spacing.kt,Theme.kt}` + `MotionTokens` respecting `reduceMotion`
- **Shared Components**: `ui/components/` (`AppButton`, `ExamSelector`, `ModuleRow`, `PackageRow`, `QuestionCard`, `AudioPlayerBar`, `ProgressSummary`, `InlineFeedback`, `OfflineModelState`)
- **Screens**: `ui/feature/progress/ProgressScreen.kt`, `ui/feature/profile/ProfileScreen.kt`
- **Verification**: JUnit4 + Robolectric + Compose UI Tests (`createComposeRule`) verifying tap depth, 48dp targets, 2.0x font scale, and zero AI-slop chat overlay
