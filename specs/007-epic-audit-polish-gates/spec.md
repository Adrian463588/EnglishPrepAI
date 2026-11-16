# Feature Specification: EPIC-00..21 Audit, Policy Corrections, UI Craft & QA Ledger

**Feature Branch**: `007-epic-audit-polish-gates`
**Created**: 2026-11-16
**Status**: Approved for Execution

## Invariants
- Never invent uncalibrated scaled scores (310-677 ITP, 1-6/0-120 iBT 2026, IELTS Bands) from fixed sample practice sets; report exact raw scores, accuracy percentages, skipped counts, `policyVersion`, and non-official disclaimers (`E05-07`, `E13-03..06`).
- Every checkbox in `docs/qa/EPIC_STATUS.md` reflects honest observed verification only; items requiring external multi-GB `.litertlm`/`.onnx` downloads or complete 140/120/85-question licensed sets remain `[ ]` / `PARTIAL` with exact scope noted.
- Apply `craft-floor.md` + `android-compose-anti-slop`: remove unnecessary kickers/eyebrows, eliminate competing CTAs and redundant Home AI config links, reuse shared components cleanly (DRY), enforce >=48dp touch targets and 200% font scale without clipping.
- Enforce Strict Mock integrity in `ChatViewModel` (`E18-06`) and pre-submit unanswered warning in `ExamScreen` (`E08-04`, `E12-04`).

## Functional Requirements
- **FR-022**: Refactor `ExamScoreEstimator` & `DomainModels` to honest raw deterministic scoring + `skippedCount` + `policyVersion`; normalize `TFNG`/`YNNG` & short answers explicitly in `QuizGradingEngine`.
- **FR-023**: Add `PackIntegrityValidator` validating all 15 manifests/packs against duplicates, invalid keys, missing audio refs, and false `READY` claims.
- **FR-024**: Add module bookmarking + last-read tracking + version/license metadata in `LearnScreens`; add Practice transcript toggle (`E09-04`), unanswered confirmation dialog (`E12-04`), and per-recording delete (`E10-03`) in `ExamScreen`; add Clear chat & Strict guard in `ChatScreen`; add OSS license & disclaimer dialog in `ProfileScreen`.
- **FR-025**: Polish `HomeScreen` anti-slop typography/hierarchy (`craft-floor`), centralize `SelectableOptionRow` in `AppComponents`, add `RoomIntegrityAndRecoveryTest` & expanded multi-viewport `UiAndAccessibilityTest`, create `docs/qa/LICENSE_AND_SBOM_AUDIT.md` & `docs/qa/EPIC_STATUS.md`.
