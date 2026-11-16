# Tasks: DevSecOps, R8/ProGuard Hardening & Offline RASP Integrity

1. **T050 [TDD Red]**: Write `SecurityHardeningTest.kt` testing `SecurityPostureEvaluator` (debug vs release, matched vs tampered SHA-256 digest, debugger attached, root heuristic), `DetachedSignatureVerifier` (ECDSA valid/tampered/bad-key), and `AesGcmCodec` (round-trip seal/unseal + tampered ciphertext rejection).
2. **T051 [Domain Security]**: Implement `SecurityContracts.kt`, `DetachedSignatureVerifier.kt`, and `AesGcmCodec.kt` until `SecurityHardeningTest` passes green.
3. **T052 [Data Security & DI]**: Implement `AndroidAppIntegrityInspector.kt` and `AndroidKeystoreSealer.kt`, bind `SecurityPostureInspector` in `RepositoryModule.kt`.
4. **T053 [Build & Manifest Hardening]**: Harden `.gitignore`, `app/build.gradle.kts` (optional `keystore.properties` + `EXPECTED_RELEASE_SIGNING_SHA256`), `app/proguard-rules.pro` (targeted keeps, `-repackageclasses 'o'`, log stripping), `AndroidManifest.xml` (`allowBackup="false"`), `backup_rules.xml`, and `data_extraction_rules.xml`.
5. **T054 [Profile UI & CI Workflow]**: Surface `securityPosture` in `ProfileViewModel`/`ProfileScreen`, add `.github/workflows/android-devsecops.yml`.
6. **T055 [Verification]**: Run `testDebugUnitTest`, `lintDebug`, `assembleRelease` (verifying `mapping.txt` obfuscation & shrunk APK), `assembleDebug`, and physical device verification on `SM-G988B`. Update `docs/VERIFICATION_CHECKLIST.md` & `CHANGELOG.md`.
