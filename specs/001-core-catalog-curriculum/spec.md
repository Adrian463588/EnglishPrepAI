# Feature Specification: Core Catalog, Curriculum & 15 Tryout Packages

**Feature Branch**: `001-core-catalog-curriculum`
**Created**: 2026-11-16
**Status**: Approved for Implementation

## Invariants
- `ExamProfile` MUST distinguish `ITP_L1`, `IBT_2026`, and `IELTS_ACADEMIC` explicitly; ITP Level 1 core exam MUST NOT force Speaking/Writing into core mock flow.
- All 15 tryout packs (`ITP-L1-01..05`, `IBT26-01..05`, `IELTS-A-01..05`) MUST exist offline at first launch with original items, real local audio assets, explicit `license`/`provenance`, and honest `status = SAMPLE` when `availableItems < blueprintItems`.
- Room database `english_prep.db` MUST export schemas, enforce indices/unique constraints per `DATA_SCHEMA.md`, and NEVER use destructive migration in production.

## User Scenarios & Testing

### User Story 1 - Exam Selection & Home Readiness (Priority: P1)
Learners switch seamlessly between TOEFL ITP Level 1, TOEFL iBT 2026, and IELTS Academic from Home in at most 2 taps, with persistent selection across restarts.

**Independent Test**: Switch exam on Home, kill/recreate ViewModel or Activity, verify persisted exam selection in DataStore and matching curriculum/packs.

**Acceptance Scenarios**:
1. **Given** app launches cold offline, **When** Home loads, **Then** selected exam badge, primary Resume/Start CTA, next recommended lesson, progress summary, and single discrete AI Tutor button are visible within <=2 taps to resume.
2. **Given** user taps exam selector on Home, **When** user picks `IBT_2026`, **Then** Home updates recommendations and DataStore persists `IBT_2026` in 2 total taps (open switcher + pick exam).

### User Story 2 - Complete Offline Curriculum & Interactive Drills (Priority: P1)
Learners browse exam -> section -> module -> lesson (`theory`, `workedExample`, `interactive 3-5 item drill`, `mistakeExplanation`, `difficulty`, `duration`, `sourceTrace`, optional audio script).

**Independent Test**: Open Learn tab, drill down to any module across all 3 exams, complete its inline mini-drill offline, verify mastery update in `StudyProgress`.

**Acceptance Scenarios**:
1. **Given** `LearnScreen` for any of the 3 exams, **When** user selects a module (<=3 taps from Home), **Then** full lesson theory, worked example, common traps, source trace, and 3–5 interactive drill questions render offline without network.
2. **Given** user finishes a lesson mini-drill, **When** answers are checked, **Then** deterministic rationales display immediately and `StudyProgress` persists attempts/correct count in Room.

### User Story 3 - Five Tryout Packages per Exam (15 Packs Total) (Priority: P1)
Practice tab displays 5 distinct themed tryout packages per exam (15 total) with `Sample` / `Ready` badges, `available/blueprint` counts, duration, and filter by section or mode (`PRACTICE` vs `STRICT`).

**Independent Test**: Query catalog for each exam; assert 5 packs per exam, non-empty questions/audio per pack, and `SAMPLE` status badge whenever `availableItems < blueprintItems`.

**Acceptance Scenarios**:
1. **Given** Practice tab for `ITP_L1`, `IBT_2026`, or `IELTS_ACADEMIC`, **When** pack list renders, **Then** 5 unique themed packages appear, each visibly showing `Sample · X/Y soal` (never impersonating full official ETS/IELTS tests).
2. **Given** user imports a valid external SAF pack manifest matching schema v1, **When** validation passes, **Then** pack atomically installs into Room + `files/packs/` and becomes playable offline.

## Functional Requirements
- **FR-001**: Seed Room DB on first install from bundled `assets/curriculum/curriculum.json` and `assets/packs/index.json` + 15 pack JSON files (`ITP-L1-01..05`, `IBT26-01..05`, `IELTS-A-01..05`).
- **FR-002**: Index curriculum theory, worked examples, and reading passages in Room FTS4 (`CurriculumFts`) for instant offline search and AI RAG citations.
- **FR-003**: Store user preferences (`selectedExam`, `language`, `audioSpeedPractice`, `reminderSettings`, `darkTheme`, `modelId`, `reduceMotion`, `hapticsEnabled`) in DataStore Preferences.
- **FR-004**: Support 10 shared task primitives (`CHOICE_SINGLE`, `CHOICE_MULTI`, `FILL_BLANK`, `MATCHING`, `REORDER`, `ERROR_IDENTIFICATION`, `SENTENCE_BUILD`, `WRITING_RESPONSE`, `SPEAKING_RESPONSE`, `LISTEN_REPEAT`).
