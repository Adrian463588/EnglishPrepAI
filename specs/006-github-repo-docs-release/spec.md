# Feature Specification: GitHub Repository, Technical README & Release Distribution

**Feature Branch**: `006-github-repo-docs-release`
**Created**: 2026-11-16
**Status**: Approved for Implementation

## Invariants
- Repository MUST be created cleanly via GitHub CLI (`gh`) under `Adrian463588/EnglishPrepAI` with meaningful `--description` and relevant `--add-topic` metadata.
- Top of `README.md` MUST display application version (`v1.0.0`, `versionCode = 1`) and direct download links (`releases/latest` and direct `.apk` asset URL) for immediate developer/user access.
- `README.md` MUST document current, verified behavior only: project overview, directory structure, build/usage instructions, complete 17-table Room + DataStore schema specification matching `Entities.kt`, Mermaid architecture & ER diagrams (valid GitHub Mermaid syntax without HTML tags), DevSecOps/R8 security summary, and real-time physical-device screenshots (`docs/screenshots/*.png`) covering every core menu and feature.
- No binary `.apk`, `.keystore`, `.jks`, `.p12`, or `keystore.properties` may be committed into Git; downloadable APK artifacts are published exclusively through GitHub Releases (`gh release create v1.0.0`).
- Zero em dashes (`—`), en dashes (`–`), or ` -- ` in `README.md` per `technical-writing` guidelines.

## User Scenarios & Testing
1. **Repository & Release Discovery**: A developer opening the GitHub repository sees description, topics, app version `v1.0.0`, and clickable top download link pointing to an existing verified GitHub Release asset.
2. **Visual & Schema Onboarding**: A contributor reading `README.md` sees authentic UI screenshots for every menu/feature, clean directory layout, Mermaid ER diagram, full 17-entity schema, and reproducible Gradle build/run steps.

## Functional Requirements
- **FR-019**: Capture verified real-time screenshots from physical Android hardware (`SM-G988B`) for every primary menu and feature in `docs/screenshots/`.
- **FR-020**: Author complete `README.md` complying with verified codebase facts and `technical-writing` conventions.
- **FR-021**: Initialize initial clean Git commit, create GitHub repository via `gh repo create`, set description & topics via `gh`, push codebase, and publish GitHub Release `v1.0.0` with installable APK artifact + SHA-256 verification.
