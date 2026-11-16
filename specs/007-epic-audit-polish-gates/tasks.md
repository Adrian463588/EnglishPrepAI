# Tasks: EPIC-00..21 Audit, Policy Corrections, UI Craft & QA Ledger

- **T070 [TDD Red - Domain Policies]**: Update `ExamScoreEstimatorTest`, `QuizGradingEngineTest`, and `AssetSeederAndFtsTest` for honest raw-only score output, TFNG/YNNG normalization, and 15-pack `PackIntegrityValidator`.
- **T071 [Domain Impl]**: Update `DomainModels.kt`, `ExamScoreEstimator.kt`, `QuizGradingEngine.kt`, `ResultsViewModel.kt`, and implement `PackIntegrityValidator.kt`.
- **T072 [Persistence & Safeguards]**: Add `bookmarkedModuleIds`/`lastReadModuleId` in `UserPreferencesRepository.kt`; write `RoomIntegrityAndRecoveryTest.kt`; add `clearConversation` & Strict mock guard in `ChatViewModel.kt`; add `deleteSpeakingRecording` & transcript caption exposure in `ExamViewModel.kt`.
- **T073 [UI Craft & Dry Components]**: Move `SelectableOptionRow` to `AppComponents.kt`; refine `HomeScreen.kt` (no kicker eyebrow, 1 primary CTA, clean row layout, no duplicate Profile link); upgrade `LearnScreens.kt`, `ExamScreen.kt`, `ResultsScreen.kt`, `ChatScreen.kt`, `ProfileScreen.kt` with OSS licenses modal.
- **T074 [Compose UI & A11y Matrix]**: Expand `UiAndAccessibilityTest.kt` to cover 5 nav screens, compact/tablet, light/dark, empty/error states, tap budgets, and `fontScale = 2.0f`.
- **T075 [QA Ledger & Verification]**: Author `docs/qa/LICENSE_AND_SBOM_AUDIT.md` & `docs/qa/EPIC_STATUS.md`, run full Gradle test + lint + R8 release build + physical `SM-G988B` QA screenshots, sync docs, commit, and push to GitHub.
