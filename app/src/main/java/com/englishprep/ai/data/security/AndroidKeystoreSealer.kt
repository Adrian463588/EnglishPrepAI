package com.englishprep.ai.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.englishprep.ai.domain.security.AesGcmCodec
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hardware-backed (where supported) Android Keystore wrapper using AES-256-GCM
 * for sealing local sensitive state without embedding keys inside source or assets.
 */
@Singleton
class AndroidKeystoreSealer @Inject constructor() {

    fun seal(alias: String, plaintext: ByteArray): ByteArray? = runCatching {
        AesGcmCodec.encrypt(plaintext, getOrCreateSecretKey(alias))
    }.getOrNull()

    fun unseal(alias: String, sealed: ByteArray): ByteArray? = runCatching {
        AesGcmCodec.decrypt(sealed, getOrCreateSecretKey(alias))
    }.getOrNull()

    private fun getOrCreateSecretKey(alias: String): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        val existing = (ks.getEntry(alias, null) as? KeyStore.SecretKeyEntry)?.secretKey
        if (existing != null) return existing

        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER).apply {
            init(spec)
        }.generateKey()
    }

    private companion object {
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    }
}
