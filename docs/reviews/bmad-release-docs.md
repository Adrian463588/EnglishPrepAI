# BMAD Review — Developer Experience & Release Readiness

- **Reviewer Role**: BMAD Product & Release Lead
- **Authoritative Spec**: `specs/006-github-repo-docs-release/spec.md`

## Release & Documentation Review
1. **Binary Distribution Separation**: Keeping `*.apk` out of Git history preserves clone speed while `gh release create v1.0.0` hosts the ~166 MB universal debug-signed sideload APK cleanly alongside explicit instructions and SHA-256 checksums.
2. **Truthful Build Labeling**: The pre-built GitHub Release APK is explicitly documented as the offline-first sideload/QA build (`v1.0.0`, `versionCode 1`), while production Google Play builds use private local `keystore.properties` with R8 + Resource Shrinking (`isMinifyEnabled = true`, `isShrinkResources = true`).
3. **Schema & Diagram Accuracy**: Every Room entity column, index, foreign key cascade, and FTS4 table in `README.md` traces directly to `app/src/main/java/com/englishprep/ai/data/room/Entities.kt` and exported `app/schemas/com.englishprep.ai.data.room.EnglishPrepDatabase/1.json`.
