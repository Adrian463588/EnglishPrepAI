package com.englishprep.ai.data.imports

import java.io.InputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

data class ValidationResult(
    val isValid: Boolean,
    val reason: String = "",
)

@Singleton
class SafSecurityValidator @Inject constructor() {

    fun validateModelHeader(
        fileName: String,
        sizeBytes: Long,
        headerBytes: ByteArray,
        availableDiskBytes: Long,
    ): ValidationResult {
        if (sizeBytes <= 0L || headerBytes.isEmpty()) {
            return ValidationResult(false, "Berkas model kosong (0-byte).")
        }
        val lowerName = fileName.lowercase()
        val headerMagic = headerBytes.take(4).toByteArray().toString(Charsets.US_ASCII)

        if (headerMagic == "GGUF" && lowerName.endsWith(".litertlm")) {
            return ValidationResult(
                false,
                "Format tidak cocok: berkas memiliki header GGUF padahal runtime Gemma 4 memakai .litertlm.",
            )
        }
        if (lowerName.endsWith(".litertlm") && sizeBytes < 1_000_000L) {
            return ValidationResult(false, "Berkas .litertlm terlalu kecil/korup (<1 MB).")
        }
        if (!lowerName.endsWith(".litertlm") && !lowerName.endsWith(".onnx") && !lowerName.endsWith(".json") && !lowerName.endsWith(".txt")) {
            return ValidationResult(false, "Ekstensi berkas model tidak didukung ($fileName).")
        }
        val requiredFree = sizeBytes + 256L * 1024L * 1024L
        if (availableDiskBytes < requiredFree) {
            return ValidationResult(
                false,
                "Ruang penyimpanan tersisa tidak mencukupi untuk menyalin model dengan aman.",
            )
        }
        return ValidationResult(true)
    }

    fun inspectZipSafety(
        zipStream: InputStream,
        maxUncompressedBytes: Long = 100L * 1024L * 1024L,
        maxEntries: Int = 500,
    ): ValidationResult {
        var totalBytes = 0L
        var count = 0
        val buffer = ByteArray(8192)
        ZipInputStream(zipStream).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                count++
                if (count > maxEntries) {
                    return ValidationResult(false, "Arsip melebihi batas jumlah entri ($maxEntries).")
                }
                val name = entry.name
                if (name.startsWith("/") || name.startsWith("\\") || name.contains("..")) {
                    return ValidationResult(false, "Terdeteksi jalur path traversal berbahaya: $name")
                }
                var read = zis.read(buffer)
                while (read > 0) {
                    totalBytes += read
                    if (totalBytes > maxUncompressedBytes) {
                        return ValidationResult(
                            false,
                            "Ukuran ekstraksi arsip melebihi batas aman (potensi decompression bomb).",
                        )
                    }
                    read = zis.read(buffer)
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
        return ValidationResult(true)
    }

    fun sha256Hex(stream: InputStream): String {
        val md = MessageDigest.getInstance("SHA-256")
        val buf = ByteArray(16_384)
        var n = stream.read(buf)
        while (n > 0) {
            md.update(buf, 0, n)
            n = stream.read(buf)
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
