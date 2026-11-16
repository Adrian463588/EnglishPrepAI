# Feature Specification: Quiz, Audio & Timed Exam Engine

**Feature Branch**: `002-quiz-audio-exam-engine`
**Created**: 2026-11-16
**Status**: Approved for Implementation

## Invariants
- Objective scoring MUST be 100% deterministic in pure Kotlin (`QuizGradingEngine`) with zero Android framework or LLM dependency.
- Completed `ExamSession` scores reference immutable pack revision + `scorePolicyVersion`; user answer edits never overwrite authoritative answer keys.
- In `STRICT` mode: explanations stay hidden until submission, audio speed controls are disabled, and replay limits per exam rule are enforced; `PRACTICE` mode allows seek, speed (0.75x–1.5x), replay, and immediate check.
- `Media3 ExoPlayer` holds a single player instance scoped cleanly to lifecycle, managing `AudioManager` audio focus and `ACTION_AUDIO_BECOMING_NOISY`.
- Answers and monotonic elapsed duration checkpoints auto-save on every answer mutation so rotation/process death loses zero user progress.

## User Scenarios & Testing

### User Story 1 - Multi-Format Deterministic Quiz Engine (Priority: P1)
Supports MCQ (`CHOICE_SINGLE`), multi-select (`CHOICE_MULTI`), fill-in-the-blank (`FILL_BLANK`) with IELTS-aware spelling/case/plural normalization, matching (`MATCHING`), reordering (`REORDER`), TOEFL ITP error identification (`ERROR_IDENTIFICATION`), TOEFL iBT 2026 sentence building (`SENTENCE_BUILD`), plus Writing and Speaking response capture.

**Acceptance Scenarios**:
1. **Given** a `FILL_BLANK` IELTS item with accepted variants `["14", "fourteen"]` and max words `2`, **When** user submits `"  Fourteen "` or exceeds word limit, **Then** grading accepts `"fourteen"` deterministically and rejects over-limit answers with clear explanation.
2. **Given** an active `ExamSession`, **When** user changes an answer, skips an item, flags a question, rotates screen, or process restarts, **Then** Room restores exact question index, answers, flags, and monotonic elapsed timer.

### User Story 2 - Offline Media3 ExoPlayer Listening Engine (Priority: P1)
Plays bundled `asset:///` or imported SAF/private file audio reliably offline with explicit user start (never surprising autoplay in Practice), audio focus handling, and strict vs practice controls.

**Acceptance Scenarios**:
1. **Given** `PRACTICE` listening question, **When** user taps Play, adjusts speed to `1.25x`, or seeks, **Then** ExoPlayer updates state/position smoothly and pauses cleanly on transient audio focus loss or headphone unplug.
2. **Given** `STRICT` mock test listening question with `maxReplays = 1`, **When** audio finishes once, **Then** replay button disables with clear label `"Replay limit reached (Strict mode)"` and speed control stays hidden.

### User Story 3 - Timed Mock Exam & Detailed Results (Priority: P1)
Runs Practice or Strict mode with real monotonic countdown, section context, non-official score disclaimers (ITP 310–677 estimate, iBT 2026 1–6 raw summary, IELTS 0–9 raw band estimate), item-by-item explanation, linked curriculum module, and "Retry Mistakes" action.

**Acceptance Scenarios**:
1. **Given** user submits an `ExamSession`, **When** Results screen renders, **Then** objective raw accuracy, section breakdown, non-official estimated badge, per-item key + explanation + curriculum lesson link, and one-tap "Retry Mistakes" appear.

## Functional Requirements
- **FR-005**: Implement `QuizGradingEngine` with deterministic normalization for all 8 objective/semi-objective types and rubric-pending status for Writing/Speaking.
- **FR-006**: Implement `ExamSessionEngine` tracking `elapsedMonotonicCheckpointMs`, `lastSavedAt`, `deadlineAt`, `isFlagged`, and `STRICT`/`PRACTICE` rule enforcement.
- **FR-007**: Implement `OfflineAudioEngine` wrapping `androidx.media3.exoplayer.ExoPlayer` (`1.5.1`) with audio focus, `BecomingNoisyReceiver`, lifecycle release, and missing/corrupt asset error recovery.
