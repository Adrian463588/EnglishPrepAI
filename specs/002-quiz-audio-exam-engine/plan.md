# Implementation Plan: Quiz, Audio & Timed Exam Engine

**Branch**: `002-quiz-audio-exam-engine` | **Date**: 2026-11-16 | **Spec**: `specs/002-quiz-audio-exam-engine/spec.md`

## Technical Context
- **Pure Domain Grading**: `domain/grading/QuizGradingEngine.kt` + `ExamScoreEstimator.kt` (zero Android imports, fully unit tested)
- **Audio Engine**: `data/media/Media3AudioEngine.kt` implementing `domain/media/AudioPlayer.kt` using Media3 `1.5.1`
- **Session Persistence**: `ExamSessionEntity`, `UserAnswerEntity`, `EvaluationEntity`, `SavedStateHandle` + monotonic clock `SystemClock.elapsedRealtime()` delta persistence
- **UI**: `ui/feature/exam/ExamScreen.kt` (all 10 task renderers + `AudioPlayerBar` + flag/navigator sheet + Writing word counter + Speaking recorder pane), `ui/feature/results/ResultsScreen.kt`
