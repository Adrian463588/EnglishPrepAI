package com.englishprep.ai.data.imports

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SafSecurityValidatorTest {

    private val validator = SafSecurityValidator()

    @Test
    fun `rejects empty or tiny invalid model files`() {
        val zeroResult = validator.validateModelHeader(
            fileName = "gemma-4-E2B-it.litertlm",
            sizeBytes = 0L,
            headerBytes = ByteArray(0),
            availableDiskBytes = 10_000_000_000L,
        )
        assertFalse(zeroResult.isValid)
        assertTrue(zeroResult.reason.contains("kosong", ignoreCase = true) || zeroResult.reason.contains("0-byte", ignoreCase = true))
    }

    @Test
    fun `rejects mismatched runtime format such as gguf renamed as litertlm`() {
        val ggufMagic = "GGUF".toByteArray() + ByteArray(60)
        val result = validator.validateModelHeader(
            fileName = "gemma-4-E2B-it.litertlm",
            sizeBytes = 2_000_000_000L,
            headerBytes = ggufMagic,
            availableDiskBytes = 10_000_000_000L,
        )
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("GGUF", ignoreCase = true) || result.reason.contains("format", ignoreCase = true))
    }

    @Test
    fun `rejects low free storage before copying model`() {
        val okHeader = ByteArray(64) { 0x4C }
        val result = validator.validateModelHeader(
            fileName = "gemma-4-E2B-it.litertlm",
            sizeBytes = 2_500_000_000L,
            headerBytes = okHeader,
            availableDiskBytes = 500_000_000L,
        )
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("penyimpanan", ignoreCase = true) || result.reason.contains("storage", ignoreCase = true))
    }

    @Test
    fun `blocks zip slip path traversal entries`() {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            zos.putNextEntry(ZipEntry("../../etc/passwd"))
            zos.write("bad".toByteArray())
            zos.closeEntry()
        }
        val result = validator.inspectZipSafety(
            ByteArrayInputStream(baos.toByteArray()),
            maxUncompressedBytes = 10_000_000L,
            maxEntries = 100,
        )
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("traversal", ignoreCase = true))
    }

    @Test
    fun `blocks decompression bomb exceeding byte cap`() {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            zos.putNextEntry(ZipEntry("pack.json"))
            zos.write(ByteArray(4096) { 65 })
            zos.closeEntry()
        }
        val result = validator.inspectZipSafety(
            ByteArrayInputStream(baos.toByteArray()),
            maxUncompressedBytes = 1024L,
            maxEntries = 100,
        )
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("ukuran", ignoreCase = true) || result.reason.contains("bomb", ignoreCase = true) || result.reason.contains("exceed", ignoreCase = true))
    }
}
