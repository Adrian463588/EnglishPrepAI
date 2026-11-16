package com.englishprep.ai.domain.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.Base64
import javax.crypto.KeyGenerator

class SecurityHardeningTest {

    @Test
    fun `posture evaluator verifies normalized sha256 match in hardened release`() {
        val posture = SecurityPostureEvaluator.evaluate(
            isDebuggable = false,
            isDebuggerAttached = false,
            signingSha256 = "ab:cd:ef:01:23:45",
            expectedReleaseSha256 = "ABCDEF012345",
            rootIndicatorsFound = false,
            allowBackupEnabled = false,
        )

        assertFalse(posture.isDebuggable)
        assertTrue(posture.r8ExpectedOnRelease)
        assertEquals(true, posture.signatureMatchesExpected)
        assertTrue("Clean hardened release should have zero warnings", posture.advisoryWarnings.isEmpty())
    }

    @Test
    fun `posture evaluator flags signature mismatch debugger backup and root indicators`() {
        val posture = SecurityPostureEvaluator.evaluate(
            isDebuggable = true,
            isDebuggerAttached = true,
            signingSha256 = "11:22:33",
            expectedReleaseSha256 = "AA:BB:CC",
            rootIndicatorsFound = true,
            allowBackupEnabled = true,
        )

        assertEquals(false, posture.signatureMatchesExpected)
        assertFalse(posture.r8ExpectedOnRelease)
        assertTrue(posture.advisoryWarnings.size >= 4)
    }

    @Test
    fun `ecdsa detached signature verifier passes valid signature and rejects tampered payload`() {
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        val kp = kpg.generateKeyPair()

        val payload = "{\"packId\":\"PRO-01\",\"version\":1}".toByteArray(Charsets.UTF_8)
        val sigBytes = Signature.getInstance("SHA256withECDSA").apply {
            initSign(kp.private)
            update(payload)
        }.sign()

        val sigB64 = Base64.getEncoder().encodeToString(sigBytes)
        val pubB64 = Base64.getEncoder().encodeToString(kp.public.encoded)

        val valid = DetachedSignatureVerifier.verifyEcdsaSha256(payload, sigB64, pubB64)
        assertTrue(valid.isValid)

        val tampered = "{\"packId\":\"PRO-99\",\"version\":1}".toByteArray(Charsets.UTF_8)
        val invalid = DetachedSignatureVerifier.verifyEcdsaSha256(tampered, sigB64, pubB64)
        assertFalse(invalid.isValid)
    }

    @Test
    fun `aes gcm codec seals unseals and rejects tampered ciphertext`() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val key = keyGen.generateKey()

        val secret = "local-offline-session-seal".toByteArray(Charsets.UTF_8)
        val sealed = AesGcmCodec.encrypt(secret, key)
        assertTrue(sealed.size > secret.size + 12)

        val unsealed = AesGcmCodec.decrypt(sealed, key)
        assertNotNull(unsealed)
        assertEquals("local-offline-session-seal", unsealed!!.toString(Charsets.UTF_8))

        val corrupted = sealed.copyOf().also { it[it.lastIndex] = (it.last().toInt() xor 0xFF).toByte() }
        assertNull("Tampered ciphertext must fail authentication tag check", AesGcmCodec.decrypt(corrupted, key))
    }
}
