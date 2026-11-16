# Plan: Spec 010 — DevSecOps Hardening, GitHub Actions CI/CD Fix & Push

## Phase 1: Baseline Audit & Root-Cause Evidence
- Audit GitHub Actions failure logs (`gh run view 43876231014 --log`) -> confirmed `100644` `gradlew` caused `./gradlew: Permission denied`.
- Audit tracked files (`284` non-binary tracked files of `709`) for secrets/tokens -> `0` hits.
- Audit merged Release `AndroidManifest.xml`, `app/build.gradle.kts`, `proguard-rules.pro`, `backup_rules.xml`, `data_extraction_rules.xml`.

## Phase 2: Android DevSecOps & R8 Hardening
1. Set `git update-index --chmod=+x gradlew` so `gradlew` commits with `100755`.
2. Harden `app/build.gradle.kts`:
   - Keep `isMinifyEnabled = true`, `isShrinkResources = true`, `proguard-android-optimize.txt` + `proguard-rules.pro`.
   - Set `isDebuggable = false`, `isJniDebuggable = false`, `vcsInfo { include = false }` inside `buildTypes.release`.
   - Strip `DebugProbesKt.bin` in `packaging.resources.excludes` without breaking Kotlin Serialization or Hilt.
3. Harden `app/src/main/AndroidManifest.xml`: add explicit `android:usesCleartextTraffic="false"` on `<application>` (protects Android 8.0-8.1 API 26-27 alongside API 28+ default).

## Phase 3: Fix `.github/workflows/android-devsecops.yml` (CI/CD + Offline ML Gate)
1. Add least-privilege `permissions: { contents: read }` and `concurrency` cancellation.
2. Upgrade action pins (`actions/checkout`, `actions/setup-java`, `gradle/actions/setup-gradle`, `android-actions/setup-android`, `actions/upload-artifact`) to verified Node 24-compatible releases.
3. Implement comprehensive **Secret, Manifest & Offline ML Hygiene** step:
   - Enforce `gradlew` executable (`100755` + `chmod +x gradlew`).
   - Block keystores/credentials (`*.jks`, `*.keystore`, `*.p12`, `keystore.properties`, `local.properties`).
   - Run regex secret scan across tracked files (AWS, GitHub, HuggingFace `hf_`, Google `AIza`, OpenAI `sk-`, PEM keys, Bearer headers).
   - **Offline AI/ML & Material Bank Guard**: forbid accidental committed multi-GB `.litertlm`/`.onnx`/`.gguf` weights or copyrighted PDFs under `material_bank/official_reference_pdfs/`, and validate `packs/index.json` + `app/src/main/assets/material_bank/material_bank.json`.
   - Assert R8/ProGuard flags (`isMinifyEnabled = true`, `isShrinkResources = true`, `-repackageclasses`, `-assumenosideeffects class android.util.Log`) and `AndroidManifest.xml` (`!INTERNET`, `allowBackup="false"`, `usesCleartextTraffic="false"`).
4. Ensure Android SDK 35 (`platforms;android-35`, `build-tools;35.0.0`), run `./gradlew testDebugUnitTest --console=plain`, `./gradlew lintDebug --console=plain`, `./gradlew assembleRelease --console=plain` sequentially.
5. Verify compiled Release APK binary (`aapt2 dump permissions` shows zero `INTERNET`, `mapping.txt` non-empty and obfuscated to package `o.`) and upload CI artifacts.

## Phase 4: Documentation & Signature (`README.md`, `CHANGELOG.md`, `VERIFICATION_CHECKLIST.md`, `EPIC_STATUS.md`)
- Add `"Dibuat oleh Adrian Syah Abidin"` signature section to `README.md` (strictly keeping `0` em/en/double-hyphen dashes).
- Update `CHANGELOG.md`, `VERIFICATION_CHECKLIST.md`, `EPIC_STATUS.md`, and directory tree in `README.md`.

## Phase 5: Local Verification, Commit, Push & Remote CI Green Check
1. Run local secret scan, `testDebugUnitTest`, `lintDebug`, `assembleRelease`, and `aapt2` Release APK permission inspection.
2. Verify `git status` & `git diff --cached` hygiene.
3. Commit cleanly, push to `origin/main`, and watch GitHub Actions run finish `success`.
