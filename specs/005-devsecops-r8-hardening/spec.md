# Feature Specification: DevSecOps, R8/ProGuard Hardening & Offline RASP Integrity

**Feature Branch**: `005-devsecops-r8-hardening`
**Created**: 2026-11-16
**Status**: Approved for Implementation

## Invariants
- Release builds MUST compile with `isMinifyEnabled = true`, `isShrinkResources = true`, `proguard-android-optimize.txt`, hardened `proguard-rules.pro` (`-repackageclasses`, log stripping), and verified `mapping.txt` output.
- No keystore, `.jks`, `.p12`, password, private signing key, or `keystore.properties` may ever be committed to Git.
- Offline-first constraint holds: `INTERNET` permission remains absent; `allowBackup="false"` and strict `backup_rules.xml` / `data_extraction_rules.xml` prevent unintended ADB/cloud extraction of local DB/records.
- Pure cryptographic/integrity evaluators (`SecurityPostureEvaluator`, `DetachedSignatureVerifier`, `AesGcmSealer`) MUST reside in pure Kotlin `domain/` for fast deterministic JVM TDD, while Android platform bindings (`PackageManager`, `Debug`, `AndroidKeyStore`) live in `data/security/`.
- Offline anti-tampering never claims unbreakable security; debuggable builds remain usable for local QA while clearly surfacing DevSecOps posture in `ProfileScreen`.

## User Scenarios & Testing

### User Story 1 - Hardened Release Build & Signing Hygiene (Priority: P1)
Developers build shrunk, obfuscated release APKs cleanly with R8 active, ProGuard rules stripping logs/internal names without breaking Kotlinx Serialization, Hilt, Room, Media3, LiteRT-LM, or ONNX Runtime, and optional local release signing via ignored `keystore.properties`.

**Acceptance Scenarios**:
1. **Given** `.\gradlew.bat assembleRelease`, **When** R8 runs, **Then** build succeeds, `mapping.txt` proves obfuscation of domain/data packages, `android.util.Log` debug calls are stripped, and `.apk` sizes shrink cleanly.
2. **Given** repository root `.gitignore`, **When** scanning tracked files, **Then** zero `.keystore`, `.jks`, or `keystore.properties` files exist in Git.

### User Story 2 - Local Integrity & Signed-Manifest Verification (Priority: P1)
The app locally inspects its APK signing SHA-256 fingerprint, debuggable/debugger state, and backup configuration via Clean MVVM, supports ECDSA (`SHA256withECDSA`) verification for offline signed packs/manifests, and uses Android Keystore AES-GCM for sensitive local sealed payloads without hardcoded keys.

**Acceptance Scenarios**:
1. **Given** a signed payload and ECDSA P-256 public key, **When** `DetachedSignatureVerifier` verifies intact vs tampered bytes, **Then** intact passes and 1-byte modification or mismatched key fails deterministically.
2. **Given** `ProfileScreen`, **When** opened on device, **Then** user sees accurate build/signing SHA-256 posture and backup hardening status cleanly inside the existing Material 3 editorial card layout.

## Functional Requirements
- **FR-015**: Harden `app/build.gradle.kts`, `app/proguard-rules.pro`, `.gitignore`, `AndroidManifest.xml`, `backup_rules.xml`, and `data_extraction_rules.xml`.
- **FR-016**: Implement `SecurityPosture`, `SecurityPostureEvaluator`, and `SecurityPostureInspector` (`AndroidAppIntegrityInspector`) via Hilt DI and expose read-only posture in `ProfileViewModel` & `ProfileScreen`.
- **FR-017**: Implement pure-Kotlin `DetachedSignatureVerifier` (`SHA256withECDSA`) and Keystore-backed `AesGcmSealer` (`AndroidKeystoreSealer`) with comprehensive unit tests.
- **FR-018**: Add automated DevSecOps workflow (`.github/workflows/android-devsecops.yml`) checking unit tests, Android Lint, manifest/secret hygiene, and R8 `assembleRelease` verification.
