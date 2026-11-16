# Acceptance Criteria: EPIC-00..21 Audit, Policy Corrections, UI Craft & QA Ledger

- [x] `ExamScoreEstimator` returns raw score & accuracy (`Skor Mentah: X/Y (Z%)`) + `skippedCount` + `policyVersion = "v1.1-raw-deterministic"` + non-official note without invented ITP/iBT/IELTS scaled conversions (`ExamScoreEstimatorTest` 3/3 passed).
- [x] `QuizGradingEngine` normalizes `TFNG`/`YNNG` (`t/f/ng/y/n`) & short-answer trailing punctuation explicitly (`QuizGradingEngineTest` 6/6 passed).
- [x] `PackIntegrityValidator` validates all 15 bundled packs and enforces honest `SAMPLE` vs `READY` readiness (`AssetSeederAndFtsTest` passed).
- [x] `RoomIntegrityAndRecoveryTest` (2/2 passed) proves answer & monotonic elapsed checkpoint persistence across SQLite file close/reopen without duplicate rows.
- [x] UI enhancements (`HomeScreen` craft-floor cleanup without kicker eyebrow + 1 primary CTA, Learn bookmarks/last-read/license metadata, Exam pre-submit unanswered confirmation + Practice transcript toggle + delete recording, Chat clear + Strict guard, Profile OSS license dialog, Results skipped count & raw policy, shared `SelectableOptionRow`) implemented & verified in `UiAndAccessibilityTest` (8/8 passed) and live R8 release on `SM-G988B`.
- [x] `docs/qa/EPIC_STATUS.md` & `docs/qa/LICENSE_AND_SBOM_AUDIT.md` record honest `VERIFIED` vs `PARTIAL/OPEN` status with evidence paths (`34/34` `testDebugUnitTest`, `lintDebug` 0 errors, `assembleRelease` R8 `mapping.txt`, physical `SM-G988B` screenshots).
- [x] Updated codebase, QA ledger, refreshed screenshots, and `v1.0.0` GitHub Release APK pushed cleanly to `Adrian463588/EnglishPrepAI` on `main`.
