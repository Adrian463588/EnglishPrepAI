# Acceptance Criteria: Spec 010 (`010-devsecops-ci-hardening-release`)

1. **Zero Secret / Credential Leaks**: Secret scan across all tracked non-binary files reports `0` hits; no `.jks`, `.keystore`, `.p12`, `keystore.properties`, `local.properties`, `.onnx`, `.litertlm`, `.gguf`, or copyrighted ETS/IELTS PDFs are tracked by Git.
2. **Offline Jetpack Compose DevSecOps**:
   - `app/build.gradle.kts` enables `isMinifyEnabled = true`, `isShrinkResources = true`, `isDebuggable = false`, `isJniDebuggable = false`, `vcsInfo.include = false`, and strips `DebugProbesKt.bin`.
   - `AndroidManifest.xml` has zero `INTERNET` permission, `android:allowBackup="false"`, `android:usesCleartextTraffic="false"`, and full backup exclusions.
   - `proguard-rules.pro` enforces `-repackageclasses 'o'`, `-renamesourcefileattribute "App"`, and strips `android.util.Log` (`v/d/i/w`).
   - Compiled `app-release-unsigned.apk` verified via `aapt2 dump permissions` confirms zero `INTERNET` permission, and `mapping.txt` confirms obfuscation.
3. **CI/CD Workflow Green**: `.github/workflows/android-devsecops.yml` runs on GitHub Actions after push without permission/Node20/SDK errors and finishes with conclusion `success`.
4. **README Attribution**: `README.md` visibly contains `"Dibuat oleh Adrian Syah Abidin"` and maintains clean dash hygiene.
5. **Local Gates**: `testDebugUnitTest` passes 100%, `lintDebug` has `0` errors, `assembleRelease` builds cleanly, `git status` is clean after push.
