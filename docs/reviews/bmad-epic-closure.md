# BMAD Review — Final Epic Closure Gate (`EPIC-00`..`EPIC-21`)

- **Reviewers**: BMAD PM, Architect, UX, QA
- **Authoritative Source**: `ENGLISH_PREP_EPIC_CHECKLIST.md`, `specs/007-epic-audit-polish-gates/spec.md`

## Findings Applied Before Closure
1. **PM / Exam Fidelity (`E05-07`, `E13-03..06`)**: Removed uncalibrated linear extrapolation formulas (`310-677` / `1-6` / `Band`) in `ExamScoreEstimator` so sample practice results report raw counts, percentages, skipped totals, and explicit non-official status honestly until licensed calibration tables are supplied.
2. **UX / Taste (`craft-floor.md`, `E20-03`, `E20-04`)**: Stripped the eyebrow kicker from Home quick-start, elevated a single primary CTA on Home, removed the duplicate AI import link under Tutor AI on Home (centralized in Profile & Chat), replaced redundant Home lesson card framing with a hairline divider row, and unified `SelectableOptionRow` in `AppComponents`.
3. **QA Honesty (`E00-08`, `E17-05`, `E04-08`, `E06-07`)**: Marked real multi-GB `.litertlm`/`.onnx` device throughput measurements and 140/120/85 full-length blueprint completion as genuinely unverified (`[ ]` / `OPEN`) while closing all P0 software/architecture/UI/R8/security requirements with verified evidence in `docs/qa/EPIC_STATUS.md`.
