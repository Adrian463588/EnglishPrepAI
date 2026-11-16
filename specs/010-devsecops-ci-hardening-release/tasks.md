# Tasks: DevSecOps Hardening, CI/CD Fix & Push (`010`)

- [x] **T10-01**: Author Spec 010 (`specs/010-devsecops-ci-hardening-release/*`) & BMAD alignment review (`docs/reviews/bmad-devsecops-ci-release.md`).
- [x] **T10-02**: Set executable `100755` bit on `gradlew` (`git update-index --chmod=+x gradlew`), harden `app/build.gradle.kts` (`isDebuggable = false`, `isJniDebuggable = false`, `vcsInfo.include = false`, `DebugProbesKt.bin` strip) & `AndroidManifest.xml` (`android:usesCleartextTraffic="false"`).
- [x] **T10-03**: Rewrite `.github/workflows/android-devsecops.yml` with verified action versions, `permissions: contents: read`, Android SDK 35 check, secret/credential scan, offline AI/ML & Material Bank guard, sequential Gradle unit/lint/R8 Release checks, compiled APK `aapt2` verification, and artifact uploads.
- [x] **T10-04**: Add author signature `Dibuat oleh Adrian Syah Abidin` and Spec 010 references in `README.md`, plus update `CHANGELOG.md`, `docs/VERIFICATION_CHECKLIST.md`, and `docs/qa/EPIC_STATUS.md`.
- [x] **T10-05**: Run full local secret scan, `testDebugUnitTest`, `lintDebug`, `assembleRelease`, inspect packaged Release APK via `aapt2`, commit, push to `origin/main`, and verify GitHub Actions workflow completes `success`.
