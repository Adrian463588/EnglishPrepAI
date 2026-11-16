package com.englishprep.ai.domain.security

import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

data class SignatureCheckResult(
    val isValid: Boolean,
    val reason: String = "",
)

/**
 * Verifies detached ECDSA P-256 (SHA256withECDSA) signatures for offline study packs
 * or signed local manifests. Private signing keys remain strictly off-device.
 */
object DetachedSignatureVerifier {

    fun verifyEcdsaSha256(
        payload: ByteArray,
        signatureBase64: String,
        spkiPublicKeyBase64: String,
    ): SignatureCheckResult {
        if (signatureBase64.isBlank() || spkiPublicKeyBase64.isBlank()) {
            return SignatureCheckResult(false, "Tanda tangan atau public key kosong.")
        }
        return runCatching {
            val decoder = Base64.getDecoder()
            val keyBytes = decoder.decode(spkiPublicKeyBase64.trim())
            val sigBytes = decoder.decode(signatureBase64.trim())
            val publicKey = KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(keyBytes))
            val verifier = Signature.getInstance("SHA256withECDSA").apply {
                initVerify(publicKey)
                update(payload)
            }
            if (verifier.verify(sigBytes)) {
                SignatureCheckResult(true)
            } else {
                SignatureCheckResult(false, "Tanda tangan digital tidak valid untuk muatan saat ini.")
            }
        }.getOrElse { err ->
            SignatureCheckResult(false, "Verifikasi tanda tangan gagal: ${err.message ?: "format tidak valid"}")
        }
    }
}
