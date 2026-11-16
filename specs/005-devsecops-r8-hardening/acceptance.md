# Acceptance Criteria: DevSecOps, R8/ProGuard Hardening & Offline RASP Integrity

- [x] `app/build.gradle.kts` enables `isMinifyEnabled = true`, `isShrinkResources = true`, `proguard-android-optimize.txt`, and supports optional local `keystore.properties` without committing secrets.
- [x] `app/proguard-rules.pro` strips `android.util.Log` verbose/debug/info/warn calls, enables `-repackageclasses 'o'` & source filename redaction, and preserves only required serialization/JNI/Room/Hilt contracts.
- [x] `.\gradlew.bat assembleRelease` compiles cleanly with R8, produces non-empty `mapping.txt` confirming class/method obfuscation (`QuizGradingEngine -> o.Bd0`, `SafSecurityValidator -> o.ei0`, `AndroidAppIntegrityInspector -> o.a3`), shrinks APK size (`174.8 MB` debug -> `154.4 MB` unsigned release), and passes `lintDebug` with 0 errors.
- [x] `AndroidManifest.xml` sets `android:allowBackup="false"` (`ALLOW_BACKUP` absent in `dumpsys package`) while preserving `INTERNET`-less offline posture.
- [x] `SecurityPostureEvaluator`, `DetachedSignatureVerifier` (`SHA256withECDSA`), and `AesGcmCodec` achieve 100% pass (4/4 in `SecurityHardeningTest`, 27/27 total suite) in JVM tests.
- [x] `ProfileScreen` renders runtime R8/signing SHA-256 posture on physical device (`SM-G988B`) cleanly in both Debug QA and live R8 Release execution (`307 ms` cold start, Audio + Quiz verified crash-free).
- [x] `.github/workflows/android-devsecops.yml` defines automated test, lint, secret/manifest scan, and release R8 gate.
