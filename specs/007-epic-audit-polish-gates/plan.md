# Implementation Plan: EPIC-00..21 Audit, Policy Corrections, UI Craft & QA Ledger

- **Spec**: [spec.md](file:///d:/SEMESTER13/KodeProject/TOELFIELTS/specs/007-epic-audit-polish-gates/spec.md)
- **Authoritative Checklist**: `ENGLISH_PREP_EPIC_CHECKLIST.md` (`EPIC-00` through `EPIC-21`)
- **Design Floor**: `android-compose-anti-slop`, `impeccable/reference/craft-floor.md`, `DESIGN.md`

## Ordered Execution Slices (TDD Red -> Green -> Verify)

1. **Slice 1 - Domain Policies, Normalization & Validation (TDD First)**:
   - Update `ExamScoreEstimator` & `DomainModels` to default deterministic raw scoring + `skippedCount` + `policyVersion` (`v1.1-raw-deterministic`) without fabricated scaled scores (`E05-07`, `E13-03..06`).
   - Add explicit `TFNG`/`YNNG` normalization policy in `QuizGradingEngine` (`E08-02`).
   - Create `PackIntegrityValidator` validating all 15 bundled packs for duplicate IDs, missing keys, unresolved audio, and honest `SAMPLE` vs `READY` readiness (`E07-06`, `E07-08`).
   - Update/add unit tests (`ExamScoreEstimatorTest`, `QuizGradingEngineTest`, `AssetSeederAndFtsTest`) first and observe failure -> fix -> pass.

2. **Slice 2 - Persistence Recovery, Continuity & Integrity Guards**:
   - Extend `UserPreferencesRepository` with `bookmarkedModuleIds` & `lastReadModuleId` (`E03-04`).
   - Create `RoomIntegrityAndRecoveryTest` covering DB reopen answer preservation, `(sessionId, questionId)` unique integrity, elapsed checkpoint recovery, and progress idempotency (`E08-06`, `E12-03`, `E14-04`, `E15-06`).
   - Add `Strict` session check + Clear thread to `ChatViewModel`/`ChatScreen` (`E18-06`, `E18-07`), and `deleteSpeakingRecording` to `ExamViewModel`/`ExamScreen` (`E10-03`).

3. **Slice 3 - Anti-Slop UI Craft, Dry Components & Feature Completeness**:
   - Move `SelectableOptionRow` into `AppComponents.kt` and share across `ExamScreen` & `LearnScreens`.
   - Refine `HomeScreen` per `craft-floor.md` (delete eyebrow kicker, single primary CTA + quiet text link, divider row for Next Lesson, remove duplicate Home AI settings button).
   - Add Bookmark toggle, `Tersimpan` filter chip, and version/license metadata in `LearnScreens`; add Practice transcript toggle + pre-submit unanswered dialog + recording delete in `ExamScreen`; display skipped items & policyVersion in `ResultsScreen`; add OSS license & trademark modal in `ProfileScreen`.
   - Expand `UiAndAccessibilityTest` covering all 5 nav screens, tap depth budgets, phone (360dp)/tablet (840dp), Light/Dark, `fontScale = 2.0f`, empty/error states.

4. **Slice 4 - SBOM/License Audit, QA Ledger, Live Device QA & GitHub Push**:
   - Create `docs/qa/LICENSE_AND_SBOM_AUDIT.md` and `docs/qa/EPIC_STATUS.md` mapping `E00`..`E21` to tasks, tests, and evidence paths honestly.
   - Run `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleRelease` (R8).
   - Verify live on `SM-G988B`, refresh `docs/screenshots/`, visually inspect, update `README.md` / `VERIFICATION_CHECKLIST.md` / `CHANGELOG.md`, commit cleanly, and push to `origin main`.
