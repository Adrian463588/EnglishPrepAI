package com.englishprep.ai.domain.security

data class SecurityPosture(
    val isDebuggable: Boolean,
    val isDebuggerAttached: Boolean,
    val signingSha256: String,
    val expectedReleaseSha256: String,
    val signatureMatchesExpected: Boolean?,
    val rootIndicatorsFound: Boolean,
    val allowBackupEnabled: Boolean,
    val r8ExpectedOnRelease: Boolean,
    val advisoryWarnings: List<String>,
)

interface SecurityPostureInspector {
    suspend fun inspect(): SecurityPosture
}

/**
 * Pure Kotlin evaluator for offline RASP/DevSecOps posture.
 * Client-side checks cannot prevent determined binary patching on rooted devices,
 * so findings act as local integrity transparency rather than false absolute guarantees.
 */
object SecurityPostureEvaluator {

    fun normalizeDigest(hex: String): String =
        hex.replace(":", "").replace(" ", "").trim().uppercase()

    fun evaluate(
        isDebuggable: Boolean,
        isDebuggerAttached: Boolean,
        signingSha256: String,
        expectedReleaseSha256: String,
        rootIndicatorsFound: Boolean,
        allowBackupEnabled: Boolean,
    ): SecurityPosture {
        val normActual = normalizeDigest(signingSha256)
        val normExpected = normalizeDigest(expectedReleaseSha256)
        val signatureMatches = if (normExpected.isNotEmpty() && normActual.isNotEmpty()) {
            normActual == normExpected
        } else {
            null
        }

        val warnings = buildList {
            if (isDebuggable) add("Build berjalan dalam mode debug (R8 penuh aktif pada varian release).")
            if (isDebuggerAttached) add("Debugger terdeteksi aktif pada proses aplikasi.")
            if (signatureMatches == false) add("Fingerprint sertifikat APK tidak cocok dengan release key tepercaya.")
            if (rootIndicatorsFound) add("Indikator lingkungan root/test-keys terdeteksi di perangkat.")
            if (allowBackupEnabled) add("Cadangan OS (allowBackup) aktif; disarankan nonaktif untuk data lokal.")
        }

        return SecurityPosture(
            isDebuggable = isDebuggable,
            isDebuggerAttached = isDebuggerAttached,
            signingSha256 = normActual,
            expectedReleaseSha256 = normExpected,
            signatureMatchesExpected = signatureMatches,
            rootIndicatorsFound = rootIndicatorsFound,
            allowBackupEnabled = allowBackupEnabled,
            r8ExpectedOnRelease = !isDebuggable,
            advisoryWarnings = warnings,
        )
    }
}
