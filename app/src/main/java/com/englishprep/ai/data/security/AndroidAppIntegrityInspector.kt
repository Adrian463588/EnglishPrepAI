package com.englishprep.ai.data.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import com.englishprep.ai.BuildConfig
import com.englishprep.ai.domain.security.SecurityPosture
import com.englishprep.ai.domain.security.SecurityPostureEvaluator
import com.englishprep.ai.domain.security.SecurityPostureInspector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAppIntegrityInspector @Inject constructor(
    @ApplicationContext private val context: Context,
) : SecurityPostureInspector {

    override suspend fun inspect(): SecurityPosture = withContext(Dispatchers.Default) {
        val appFlags = context.applicationInfo.flags
        val isDebuggable = (appFlags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val allowBackup = (appFlags and ApplicationInfo.FLAG_ALLOW_BACKUP) != 0
        val debuggerAttached = Debug.isDebuggerConnected() || Debug.waitingForDebugger()
        val signingDigest = readPrimarySigningSha256()
        val rootIndicators = detectBasicRootIndicators()

        SecurityPostureEvaluator.evaluate(
            isDebuggable = isDebuggable,
            isDebuggerAttached = debuggerAttached,
            signingSha256 = signingDigest,
            expectedReleaseSha256 = BuildConfig.EXPECTED_RELEASE_SIGNING_SHA256,
            rootIndicatorsFound = rootIndicators,
            allowBackupEnabled = allowBackup,
        )
    }

    private fun readPrimarySigningSha256(): String = runCatching {
        val pm = context.packageManager
        val pkgName = context.packageName
        val certBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signingInfo = info.signingInfo ?: return ""
            val signers = if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
            signers.firstOrNull()?.toByteArray()
        } else {
            @Suppress("DEPRECATION")
            val info = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES)
            @Suppress("DEPRECATION")
            info.signatures?.firstOrNull()?.toByteArray()
        } ?: return ""

        MessageDigest.getInstance("SHA-256")
            .digest(certBytes)
            .joinToString("") { "%02X".format(it) }
    }.getOrDefault("")

    private fun detectBasicRootIndicators(): Boolean {
        if (Build.TAGS?.contains("test-keys") == true) return true
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/app/Superuser.apk",
        )
        return paths.any { File(it).exists() }
    }
}
