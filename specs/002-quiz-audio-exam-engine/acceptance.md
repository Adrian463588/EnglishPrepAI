# Acceptance Criteria: Quiz, Audio & Timed Exam Engine

- [x] `AC-02-A`: All 10 task primitives render cleanly and objective types score deterministically in pure Kotlin unit tests.
- [x] `AC-02-B`: Strict mode suppresses hints/explanations until submit, hides speed controls, and enforces audio replay count.
- [x] `AC-02-C`: Rotation and process-death restore active session index, selected answers, flags, and elapsed timer without data loss.
- [x] `AC-02-D`: Results screen links every missed question to its explanation and matching `CourseModule` and launches a targeted Retry Mistakes session.
