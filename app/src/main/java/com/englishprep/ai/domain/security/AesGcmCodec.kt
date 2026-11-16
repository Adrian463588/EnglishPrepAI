package com.englishprep.ai.domain.security

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Stateless AES-256-GCM framing helper (`[12-byte IV || ciphertext+16-byte tag]`).
 * No keys are hardcoded; keys are supplied by AndroidKeyStore or caller-managed key objects.
 */
object AesGcmCodec {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_BYTES = 12
    private const val TAG_BITS = 128

    fun encrypt(plaintext: ByteArray, key: SecretKey): ByteArray {
        val iv = ByteArray(IV_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        }
        val cipherText = cipher.doFinal(plaintext)
        return iv + cipherText
    }

    fun decrypt(sealed: ByteArray, key: SecretKey): ByteArray? {
        if (sealed.size <= IV_BYTES + 16) return null
        return runCatching {
            val iv = sealed.copyOfRange(0, IV_BYTES)
            val cipherText = sealed.copyOfRange(IV_BYTES, sealed.size)
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
            }
            cipher.doFinal(cipherText)
        }.getOrNull()
    }
}
