# BMAD Alignment Review — DevSecOps Hardening, CI/CD Fix & Release Gate (`010`)

## Product Analyst
- Memastikan seluruh riwayat push memiliki gerbang CI/CD yang benar-benar berjalan dan lulus (mengatasi 5 kegagalan `./gradlew: Permission denied` sebelumnya) serta mencantumkan atribusi penulis eksplisit **"Dibuat oleh Adrian Syah Abidin"** pada `README.md`.

## System Architect
- Pertahanan berlapis untuk aplikasi Android Jetpack Compose offline-first:
  1. **Compile/Package Layer**: R8 full optimization, `shrinkResources`, stripping `DebugProbesKt.bin`, `vcsInfo.include = false`, `isDebuggable = false`, `isJniDebuggable = false`.
  2. **Manifest/OS Layer**: `INTERNET` absen pada sumber & hasil APK terkompilasi, `allowBackup="false"`, `usesCleartextTraffic="false"`, aturan eksklusi backup XML penuh.
  3. **CI/CD Layer**: Least-privilege `GITHUB_TOKEN`, wrapper validation, regex leak scanner lintas kode/konfigurasi, pemeriksaan ML/AI offline (tanpa bobot model `.litertlm`/`.onnx` atau token Hugging Face bocor di repo), sequential Gradle build, serta verifikasi `aapt2`/`mapping.txt` pada Release APK.

## QA & SecOps Reviewer
- Verifikasi ganda: pembuktian lokal (`testDebugUnitTest`, `lintDebug`, `assembleRelease`, `aapt2`, scanner rahasia) **dan** pemantauan status `success` aktual di GitHub Actions pasca-push (`gh run watch`).
