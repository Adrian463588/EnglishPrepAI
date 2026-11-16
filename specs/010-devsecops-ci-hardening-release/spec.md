# Spec 010: DevSecOps Hardening, CI/CD YAML & Offline ML Guard Fix, README Signature & Push

## Problem Statement
1. **GitHub Actions CI/CD Failure**: Every workflow run (`43831388143`, `43832449602`, `43839536536`, `43868412297`, `43876231014`) failed in 9-16 seconds at `./gradlew: Permission denied` (`exit code 126`) because `gradlew` was tracked with mode `100644` instead of `100755`. Additionally, `.github/workflows/android-devsecops.yml` used Node 20-deprecated `v4` actions, lacked `permissions: contents: read`, lacked `concurrency` cancellation, lacked deep credential-leak regex scanning, lacked offline AI/ML model-weight & Material Bank JSON hygiene checks, lacked Android SDK 35 provisioning, and lacked compiled Release APK verification.
2. **Offline Jetpack Compose DevSecOps Defense-in-Depth**: Ensure `app/build.gradle.kts` (`isMinifyEnabled = true`, `isShrinkResources = true`, `isDebuggable = false`, `isJniDebuggable = false`, `vcsInfo.include = false`, `DebugProbesKt.bin` exclusion), `AndroidManifest.xml` (`android:usesCleartextTraffic="false"` alongside `allowBackup="false"` and zero `INTERNET`), and `SecurityHardeningTest.kt` stay strictly enforced with zero secret leaks.
3. **Author Attribution**: Append explicit signature `"Dibuat oleh Adrian Syah Abidin"` cleanly in `README.md`, then verify, commit, push to GitHub, and watch the CI workflow pass green.

## Scope & Constraints
- No speculative dependencies, no hardcoded secrets/credentials, zero `INTERNET` permission.
- Preserve existing working features, offline functionality, 40/40 unit/UI tests, and screenshot-capable usability (no intrusive flags breaking accessibility/QA screenshots).
- Verify all gates locally (`testDebugUnitTest`, `lintDebug`, `assembleRelease`, R8 mapping, packaged APK `aapt2` check, secret scan) AND remotely (`gh run watch`).
