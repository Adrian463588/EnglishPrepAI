# Implementation Plan: DevSecOps, R8/ProGuard Hardening & Offline RASP Integrity

- **Spec**: [spec.md](file:///d:/SEMESTER13/KodeProject/TOELFIELTS/specs/005-devsecops-r8-hardening/spec.md)
- **Methodology**: Spec Kit + BMAD + RED-GREEN-KEEP TDD + Ponytail minimal-diff

## Architecture Choices
1. **Gradle / R8 (`app/build.gradle.kts`, `app/proguard-rules.pro`)**:
   - Tighten keep rules so only `@kotlinx.serialization.Serializable` DTOs, LiteRT-LM/ONNX JNI boundaries, and Room/Hilt contracts stay preserved; allow full R8 obfuscation (`-repackageclasses 'o'`, `-allowaccessmodification`, `-renamesourcefileattribute "App"`) and `-assumenosideeffects` `Log` stripping on `domain.grading`, `domain.ai`, `domain.security`, and `data.imports`.
   - Load optional `keystore.properties` safely for release signing and inject `BuildConfig.EXPECTED_RELEASE_SIGNING_SHA256` without ever storing secrets in repo.
2. **Manifest & Backup (`AndroidManifest.xml`, `backup_rules.xml`, `data_extraction_rules.xml`)**:
   - Set `android:allowBackup="false"` and exclude `database`, `sharedpref`, and `files` across cloud/device rules since user-triggered SAF JSON export covers backup intentionally.
3. **Domain + Data Security Modules**:
   - `domain/security/SecurityContracts.kt`: `SecurityPosture`, `SecurityPostureEvaluator`, `SecurityPostureInspector`.
   - `domain/security/DetachedSignatureVerifier.kt`: `SHA256withECDSA` detached signature verification.
   - `domain/security/AesGcmCodec.kt`: pure AES-256/GCM seam testable on JVM without Robolectric Keystore quirks.
   - `data/security/AndroidAppIntegrityInspector.kt`: PackageManager SHA-256 cert fingerprint, `FLAG_DEBUGGABLE`, `Debug.isDebuggerConnected()`, root indicator heuristics, manifest `allowBackup` flag.
   - `data/security/AndroidKeystoreSealer.kt`: `AndroidKeyStore` AES-256/GCM key generation/retrieval + delegation to `AesGcmCodec`.
4. **MVVM UI + DevSecOps CI**:
   - Wire `SecurityPostureInspector` into `RepositoryModule` and `ProfileViewModel` -> `ProfileScreen` ("Keamanan & Integritas").
   - Add `.github/workflows/android-devsecops.yml` running tests, lint, secret/manifest hygiene checks, and `assembleRelease`.
