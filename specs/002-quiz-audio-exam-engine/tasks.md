# Tasks: Quiz, Audio & Timed Exam Engine

- [x] T010 Write failing unit tests for `QuizGradingEngine` (MCQ, MultiSelect, FillBlank + word limit + typo/plural policy, Matching, Reorder, ErrorIdentification, SentenceBuild, skipped items)
- [x] T011 Write failing unit tests for `ExamScoreEstimator` (ITP raw->310-677 non-official band, iBT 2026 non-linear disclaimer, IELTS raw->0-9 non-official estimate) and `ExamTimerState` (monotonic elapsed delta, background/clock drift safety)
- [x] T012 Implement `QuizGradingEngine` & `ExamScoreEstimator`
- [x] T013 Implement `AudioPlayer` interface & `Media3AudioEngine` (single ExoPlayer instance, audio focus, becoming noisy, strict replay limits, corrupt/absent asset recovery)
- [x] T014 Implement `ExamViewModel` & `ResultsViewModel` with auto-save, `SavedStateHandle`, mistake-retry session builder
- [x] T015 Implement `ExamScreen` (shared task renderers, accessible controls, quiet autosave badge) & `ResultsScreen`
