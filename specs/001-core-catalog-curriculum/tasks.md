# Tasks: Core Catalog, Curriculum & 15 Tryout Packages

- [x] T001 Create Gradle project `:app` with pinned version catalog (`gradle/libs.versions.toml`) and single-module MVVM layout
- [x] T002 Create `content/CURRICULUM.md` and `app/src/main/assets/curriculum/curriculum.json` covering TOEFL ITP Level 1, TOEFL iBT 2026, and IELTS Academic
- [x] T003 Create `packs/index.json` + 15 original JSON tryout packs (`ITP-L1-01..05`, `IBT26-01..05`, `IELTS-A-01..05`) + real WAV listening/repeat audio assets
- [x] T004 Write failing unit tests for asset pack schema validation, blueprint status (`SAMPLE` vs `READY`), and Room seeding/FTS lookup
- [x] T005 Implement Room entities, FTS4 table, DAOs, schema export (`app/schemas/`), and transactional `AssetSeeder`
- [x] T006 Implement `UserPreferencesRepository` (DataStore) and `ExamCatalogRepositoryImpl`
- [x] T007 Implement `HomeRoute`, `LearnRoute`, and `PracticeRoute` with 5-destination adaptive navigation and <=2–3 tap depth
