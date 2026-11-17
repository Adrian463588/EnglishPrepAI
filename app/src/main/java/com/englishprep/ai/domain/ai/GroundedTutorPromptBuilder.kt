package com.englishprep.ai.domain.ai

import com.englishprep.ai.domain.grading.QuizGradingEngine
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.RagSnippet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroundedTutorPromptBuilder @Inject constructor() {

    private val injectionPatterns = listOf(
        Regex("ignore\\s+(all\\s+)?previous\\s+instructions?", RegexOption.IGNORE_CASE),
        Regex("disregard\\s+(all\\s+)?above", RegexOption.IGNORE_CASE),
        Regex("you\\s+are\\s+now\\s+in\\s+developer\\s+mode", RegexOption.IGNORE_CASE),
        Regex("\\bsystem\\s*:\\s*", RegexOption.IGNORE_CASE),
        Regex("<\\|.*?\\|>"),
    )

    fun sanitizeSnippet(raw: String): String {
        var cleaned = raw
        injectionPatterns.forEach { pattern ->
            cleaned = cleaned.replace(pattern, "[filtered]")
        }
        return cleaned.trim().take(480)
    }

    fun buildPrompt(
        examLabel: String,
        userQuery: String,
        snippets: List<RagSnippet>,
    ): String {
        val safeContext = if (snippets.isEmpty()) {
            "Tidak ada cuplikan materi lokal yang cocok; nyatakan keterbatasan dan sarankan membuka modul terkait."
        } else {
            snippets.joinToString("\n\n") { s ->
                "[${s.sourceId}] ${s.title}: ${sanitizeSnippet(s.excerpt)}"
            }
        }
        return buildString {
            appendLine("Target Ujian: $examLabel")
            appendLine("Aturan: Jawab ringkas, akurat, kutip ID sumber lokal dalam tanda kurung siku [ID], dan tandai bila materi tidak tersedia.")
            appendLine()
            appendLine("--- KONTEKS MATERI LOKAL (DATA REFERENSI SAJA) ---")
            appendLine(safeContext)
            appendLine("--- AKHIR KONTEKS ---")
            appendLine()
            append("Pertanyaan Pelajar: ${sanitizeSnippet(userQuery)}")
        }
    }

    fun buildFallbackOfflineReply(
        examId: ExamId,
        userQuery: String,
        snippets: List<RagSnippet>,
    ): String {
        if (snippets.isEmpty()) {
            return "Model Gemma 4 offline belum dipasang, dan belum ada kecocokan langsung di catatan ${examId.title} untuk '${userQuery.take(60)}'. " +
                "Anda dapat membuka tab Materi atau mengimpor model Gemma 4 (.litertlm) melalui Profil > Kelola Model AI."
        }
        return buildString {
            appendLine("Ringkasan Materi Lokal (${examId.title} · tanpa LLM aktif):")
            snippets.forEach { s ->
                appendLine("• [${s.sourceId}] ${s.title}: ${sanitizeSnippet(s.excerpt)}")
            }
            append("\nCatatan: Pasang model Gemma 4 (.litertlm) di Profil > Kelola Model AI untuk penjelasan interaktif mendalam.")
        }
    }
}

@Singleton
class RubricFeedbackAnalyzer @Inject constructor() {

    fun buildWritingPrompt(examId: ExamId, question: QuestionItem, userText: String): String {
        val criteria = question.rubricCriteria.ifEmpty {
            listOf("Task response", "Coherence & cohesion", "Lexical precision", "Grammatical accuracy")
        }
        return buildString {
            appendLine("Evaluasi tulisan latihan ${examId.title} berikut sebagai pelatih non-resmi:")
            appendLine("Prompt: ${question.prompt.take(350)}")
            appendLine("Rubrik: ${criteria.joinToString(", ")}")
            appendLine("Jawaban peserta (${QuizGradingEngine.countWords(userText)} kata):")
            appendLine(userText.take(2000))
            append("Berikan 2 kekuatan, 2 saran perbaikan spesifik, dan estimasi rentang non-resmi.")
        }
    }

    fun buildDeterministicWritingFeedback(
        examId: ExamId,
        question: QuestionItem,
        userText: String,
    ): String {
        val words = QuizGradingEngine.countWords(userText)
        val minW = question.minWords ?: 50
        val paragraphs = userText.trim().split(Regex("\\n\\s*\\n")).filter { it.isNotBlank() }.size
        val sentences = userText.trim().split(Regex("[.!?]+")).filter { it.trim().isNotEmpty() }.size
        val avgWordsPerSentence = if (sentences > 0) words / sentences else words
        val criteriaLine = question.rubricCriteria.joinToString(" • ").ifBlank { "Kohesi & tata bahasa" }

        return buildString {
            appendLine("Evaluasi Struktural Lokal (Non-Resmi · ${examId.title})")
            appendLine("• Jumlah kata: $words kata (target minimal: $minW kata)")
            appendLine("• Struktur: $paragraphs paragraf, ~$sentences kalimat (rata-rata $avgWordsPerSentence kata/kalimat)")
            if (words < minW) {
                appendLine("• Perhatian: Panjang tulisan masih di bawah target minimal ($minW kata); kembangkan alasan dan contoh konkret.")
            } else {
                appendLine("• Panjang kata sudah memenuhi target minimal latihan.")
            }
            if (examId == ExamId.IELTS_ACADEMIC && paragraphs < 3) {
                appendLine("• Saran IELTS: Pisahkan minimal menjadi Intro/Overview, Body 1, dan Body 2/Kesimpulan agar Coherence & Cohesion terbaca jelas.")
            }
            append("• Rubrik acuan: $criteriaLine. Untuk umpan balik semantik lanjutan, aktifkan Gemma 4 di Profil > AI Offline.")
        }
    }
}
