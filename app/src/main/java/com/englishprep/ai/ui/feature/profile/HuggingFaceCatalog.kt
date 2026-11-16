package com.englishprep.ai.ui.feature.profile

data class RecommendedModelEntry(
    val id: String,
    val displayName: String,
    val family: String,
    val variant: String,
    val format: String,
    val runtime: String,
    val huggingFaceUrl: String,
    val expectedFiles: String,
    val minDiskGb: String,
    val minRamGb: String,
    val notes: String,
)

object HuggingFaceCatalog {
    val recommendedModels = listOf(
        RecommendedModelEntry(
            id = "HF-GEMMA4-E2B",
            displayName = "Gemma 4 E2B IT (LiteRT-LM)",
            family = "GEMMA4",
            variant = "E2B-it",
            format = ".litertlm",
            runtime = "LiteRT-LM 0.13.1 (CPU/GPU)",
            huggingFaceUrl = "https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm",
            expectedFiles = "gemma-4-E2B-it.litertlm",
            minDiskGb = "~2.6 GB",
            minRamGb = "6 GB+ RAM disarankan",
            notes = "Disarankan untuk Tutor AI lokal & evaluasi Writing/Speaking offline.",
        ),
        RecommendedModelEntry(
            id = "HF-GEMMA4-E4B",
            displayName = "Gemma 4 E4B IT (LiteRT-LM)",
            family = "GEMMA4",
            variant = "E4B-it",
            format = ".litertlm",
            runtime = "LiteRT-LM 0.13.1 (CPU/GPU)",
            huggingFaceUrl = "https://huggingface.co/litert-community/gemma-4-E4B-it-litert-lm",
            expectedFiles = "gemma-4-E4B-it.litertlm",
            minDiskGb = "~4.2 GB",
            minRamGb = "8–12 GB+ RAM disarankan",
            notes = "Kualitas penalaran esai lebih tinggi untuk perangkat RAM besar.",
        ),
        RecommendedModelEntry(
            id = "HF-WHISPER-TINY-EN",
            displayName = "Whisper Tiny.en (ONNX)",
            family = "WHISPER_ONNX",
            variant = "tiny.en",
            format = ".onnx folder",
            runtime = "ONNX Runtime 1.22.0",
            huggingFaceUrl = "https://huggingface.co/onnx-community/whisper-tiny.en",
            expectedFiles = "encoder_model.onnx + decoder_model.onnx + vocab.json",
            minDiskGb = "~85–150 MB",
            minRamGb = "4 GB+ RAM",
            notes = "Pilih folder hasil unduhan via SAF (ACTION_OPEN_DOCUMENT_TREE).",
        ),
    )
}
