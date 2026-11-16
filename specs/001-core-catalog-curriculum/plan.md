# Implementation Plan: Core Catalog, Curriculum & 15 Tryout Packages

**Branch**: `001-core-catalog-curriculum` | **Date**: 2026-11-16 | **Spec**: `specs/001-core-catalog-curriculum/spec.md`

## Technical Context
- **Language/Version**: Kotlin 2.0.21 / AGP 8.7.3 / Gradle 8.11.1 / JVM 17 target
- **Primary Dependencies**: Jetpack Compose Material 3 (`1.3.1`), Navigation Compose (`2.8.8`), Hilt (`2.55`), Room (`2.6.1`) + FTS4, DataStore Preferences (`1.1.3`), Kotlinx Serialization JSON (`1.7.3`)
- **Storage**: `databases/english_prep.db` + `app/src/main/assets/{curriculum,packs,audio}` + `files/packs/`
- **Testing**: JUnit4 + Kotlin Coroutines Test + Robolectric (`4.14.1`) + Room in-memory/migration tests

## Project Structure
Single `:app` Gradle module:
- `domain/models/` (ExamProfileId, TaskType, ExamSection, CourseModule, ContentPack, Question, Passage, StudyProgress)
- `data/room/` (`EnglishPrepDatabase`, entities, DAOs, `CurriculumFts`, `AssetSeeder`, `TypeConverters`)
- `data/datastore/` (`UserPreferencesRepository`)
- `data/repository/` (`ExamCatalogRepositoryImpl`)
- `ui/feature/home/`, `ui/feature/learn/`, `ui/feature/practice/`
