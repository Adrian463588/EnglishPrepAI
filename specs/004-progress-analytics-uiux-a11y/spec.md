# Feature Specification: Progress Analytics, UI/UX Polish & Accessibility

**Feature Branch**: `004-progress-analytics-uiux-a11y`
**Created**: 2026-11-16
**Status**: Approved for Implementation

## Invariants
- Tap budget from Home: `<=2` taps to resume/open exam switcher/open AI Tutor; `<=3` taps to start a topic drill or mock package (excluding system picker & actual question answering).
- Every interactive touch target MUST be `>=48dp x 48dp` with `>=8dp` spacing between adjacent targets.
- Typography MUST scale cleanly up to `200%` (`fontScale = 2.0f`) with zero clipped buttons, truncated headings, or overlapping badges.
- Reduced-motion preference (`reduceMotion`) disables nonessential animations; micro-interactions never block or delay user input.
- Analytics NEVER invent official ETS/IELTS scores or fake streaks; all empty states give clear next actions.

## User Scenarios & Testing

### User Story 1 - Progress Analytics & Mistake Review (Priority: P1)
Learners track exam-specific section accuracy, recent completed sessions, mastery trends over time, reviewed mistakes, and can export/import a local JSON backup or wipe all data from Profile.

**Acceptance Scenarios**:
1. **Given** completed sessions in Room, **When** user opens Progress tab, **Then** section accuracy bars, simple trend plot, recent sessions, and "Review Missed Questions" drill render accurately for the active exam.
2. **Given** zero attempts for an exam, **When** Progress tab renders, **Then** calm empty state explains what appears after first drill and provides a 1-tap "Start Practice" CTA.

### User Story 2 - Accessibility, Adaptive Layout & Micro-Interactions (Priority: P1)
Supports Light/Dark Material 3 theme (calm deep teal accent on neutral surfaces), edge-to-edge system bar + IME insets, TalkBack headings & descriptions, Compact BottomBar / Medium+ NavigationRail, and multi-viewport responsiveness.

**Acceptance Scenarios**:
1. **Given** Compose UI test on Compact (360dp), Tablet (800dp), and `fontScale = 2.0f`, **When** traversing Home, Learn, Practice, Exam, Chat, Progress, Profile, **Then** zero clipped controls, >=48dp interactive bounds, and correct navigation tap counts pass.
2. **Given** Profile > Delete All Data, **When** user confirms dialog, **Then** sessions, recordings, chat threads, and progress clear cleanly with optional Undo snackbar where applicable for reversible actions.

## Functional Requirements
- **FR-012**: Implement `ProgressRepository` & `ProgressViewModel` computing exam-specific section mastery, accuracy trends, and mistake queues.
- **FR-013**: Implement `BackupExportUseCase` (JSON export/restore via SAF `ACTION_CREATE_DOCUMENT` / `ACTION_OPEN_DOCUMENT` with schema version check).
- **FR-014**: Enforce Material 3 design tokens (`AppTheme`, `AppSpacing`, `MotionTokens`), adaptive navigation scaffold, TalkBack semantics, and Compose UI verification suite.
