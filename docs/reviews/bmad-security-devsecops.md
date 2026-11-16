# BMAD Review — Security & DevSecOps Lead: R8, Signing & Local RASP

- **Reviewer Role**: BMAD Security / QA Reviewer
- **Authoritative Spec**: `specs/005-devsecops-r8-hardening/spec.md`, OWASP MASVS v2 (MSTG/MASWE)

## Threat Model & Practical Offline Boundaries
1. **What R8 + ProGuard Solve**: Renames classes/methods/fields across `domain.*`, `data.*`, and `ui.*`, strips unused code/resources (`isShrinkResources = true`), and removes debug logging via `-assumenosideeffects`. Slows static JADX analysis substantially without proprietary DexGuard cost.
2. **What Local Integrity Checks Solve**: `AndroidAppIntegrityInspector` detects debuggable flags, attached debuggers, APK re-signing against an expected SHA-256 release fingerprint, and basic root indicators without external servers. Surfacing posture transparently helps detect tampered sideloads.
3. **Honest Offline Limitations**: Client-side checks on a rooted or patched device can ultimately be bypassed by binary patching or Frida hooks; therefore no unbreakable local DRM is claimed. Sensitive third-party pack authenticity should rely on public-key ECDSA (`DetachedSignatureVerifier`) with the private key kept strictly offline off-device, while device-local secrets use `AndroidKeyStore` (`AndroidKeystoreSealer`).
4. **Release Hygiene Gate**: Zero keystores or passwords in Git (`.gitignore`), `allowBackup="false"`, zero `INTERNET` permission, and CI enforcement via `.github/workflows/android-devsecops.yml` running `assembleRelease` to catch missing keep rules early.
