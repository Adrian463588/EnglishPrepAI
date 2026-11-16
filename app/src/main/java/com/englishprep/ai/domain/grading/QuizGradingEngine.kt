package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.TaskType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizGradingEngine @Inject constructor() {

    fun grade(
        question: QuestionItem,
        rawAnswer: String,
        hasAudioRecording: Boolean = false,
    ): GradingOutcome {
        return when (question.taskType) {
            TaskType.WRITING_RESPONSE -> gradeWriting(question, rawAnswer)
            TaskType.SPEAKING_RESPONSE -> gradeSpeaking(rawAnswer, hasAudioRecording)
            TaskType.LISTEN_REPEAT -> gradeListenRepeat(question, rawAnswer, hasAudioRecording)
            TaskType.CHOICE_SINGLE,
            TaskType.ERROR_IDENTIFICATION -> gradeSingleChoice(question, rawAnswer)
            TaskType.CHOICE_MULTI -> gradeMultiChoice(question, rawAnswer)
            TaskType.FILL_BLANK -> gradeFillBlank(question, rawAnswer)
            TaskType.MATCHING -> gradeMatching(question, rawAnswer)
            TaskType.REORDER,
            TaskType.SENTENCE_BUILD -> gradeSequence(question, rawAnswer)
        }
    }

    private fun gradeSingleChoice(question: QuestionItem, rawAnswer: String): GradingOutcome.Objective {
        val norm = normalizeText(rawAnswer)
        if (norm.isEmpty()) {
            return skippedOutcome(question)
        }
        val expected = normalizeText(question.answer)
        val ok = norm == expected
        return GradingOutcome.Objective(
            isCorrect = ok,
            isSkipped = false,
            earnedPoints = if (ok) question.points else 0,
            maxPoints = question.points,
            normalizedUserAnswer = norm,
            feedbackMessage = if (ok) "Benar." else "Kurang tepat. Kunci: ${question.answer}",
        )
    }

    private fun gradeMultiChoice(question: QuestionItem, rawAnswer: String): GradingOutcome.Objective {
        val selected = splitPipeSet(rawAnswer)
        if (selected.isEmpty()) {
            return skippedOutcome(question)
        }
        val expected = splitPipeSet(question.answer)
        val ok = selected == expected
        return GradingOutcome.Objective(
            isCorrect = ok,
            isSkipped = false,
            earnedPoints = if (ok) question.points else 0,
            maxPoints = question.points,
            normalizedUserAnswer = selected.sorted().joinToString("|"),
            feedbackMessage = if (ok) "Benar." else "Kurang tepat. Pilihan benar: ${question.answer.replace("|", ", ")}",
        )
    }

    private fun gradeFillBlank(question: QuestionItem, rawAnswer: String): GradingOutcome.Objective {
        val trimmed = rawAnswer.trim()
        if (trimmed.isEmpty()) {
            return skippedOutcome(question)
        }
        val wordCount = countWords(trimmed)
        val limit = question.maxWords
        if (limit != null && wordCount > limit) {
            return GradingOutcome.Objective(
                isCorrect = false,
                isSkipped = false,
                earnedPoints = 0,
                maxPoints = question.points,
                normalizedUserAnswer = normalizeText(trimmed),
                feedbackMessage = "Melebihi batas kata ($wordCount/$limit word limit). Kunci: ${question.answer}",
            )
        }
        val norm = normalizeText(trimmed)
        val accepted = (question.acceptedAnswers + question.answer)
            .map(::normalizeText)
            .filter { it.isNotEmpty() }
            .toSet()
        val ok = norm in accepted
        return GradingOutcome.Objective(
            isCorrect = ok,
            isSkipped = false,
            earnedPoints = if (ok) question.points else 0,
            maxPoints = question.points,
            normalizedUserAnswer = norm,
            feedbackMessage = if (ok) "Benar." else "Kurang tepat. Jawaban diterima: ${question.answer}",
        )
    }

    private fun gradeMatching(question: QuestionItem, rawAnswer: String): GradingOutcome.Objective {
        val userPairs = splitPipeSet(rawAnswer)
        if (userPairs.isEmpty()) {
            return skippedOutcome(question)
        }
        val expectedPairs = splitPipeSet(question.answer)
        val ok = userPairs == expectedPairs
        return GradingOutcome.Objective(
            isCorrect = ok,
            isSkipped = false,
            earnedPoints = if (ok) question.points else 0,
            maxPoints = question.points,
            normalizedUserAnswer = userPairs.sorted().joinToString("|"),
            feedbackMessage = if (ok) "Semua pasangan tepat." else "Pasangan belum tepat seluruhnya.",
        )
    }

    private fun gradeSequence(question: QuestionItem, rawAnswer: String): GradingOutcome.Objective {
        val userSeq = rawAnswer.split("|").map(::normalizeText).filter { it.isNotEmpty() }
        if (userSeq.isEmpty()) {
            return skippedOutcome(question)
        }
        val expectedSeq = question.answer.split("|").map(::normalizeText).filter { it.isNotEmpty() }
        val ok = userSeq == expectedSeq
        return GradingOutcome.Objective(
            isCorrect = ok,
            isSkipped = false,
            earnedPoints = if (ok) question.points else 0,
            maxPoints = question.points,
            normalizedUserAnswer = userSeq.joinToString(" "),
            feedbackMessage = if (ok) "Urutan tepat." else "Urutan tepat: ${question.answer.replace("|", " ")}",
        )
    }

    private fun gradeWriting(question: QuestionItem, rawAnswer: String): GradingOutcome.SubjectivePending {
        val words = countWords(rawAnswer)
        val minWords = question.minWords ?: 0
        val meetsMin = words >= minWords
        val msg = when {
            words == 0 -> "Belum ada draf tulisan."
            !meetsMin -> "Jumlah kata $words/$minWords (di bawah batas minimal). Umpan balik AI bersifat estimasi non-resmi."
            else -> "Draf tersimpan ($words kata). Siap untuk evaluasi rubrik lokal (estimasi non-resmi)."
        }
        return GradingOutcome.SubjectivePending(
            wordCount = words,
            meetsMinimumWords = meetsMin,
            hasAudioRecording = false,
            feedbackMessage = msg,
        )
    }

    private fun gradeSpeaking(transcriptOrNotes: String, hasAudioRecording: Boolean): GradingOutcome.SubjectivePending {
        val words = countWords(transcriptOrNotes)
        val msg = when {
            !hasAudioRecording && words == 0 -> "Belum ada rekaman suara atau catatan transkrip."
            hasAudioRecording -> "Rekaman suara lokal tersimpan. Analisis kelancaran & rubrik bersifat estimasi non-resmi."
            else -> "Catatan latihan tersimpan ($words kata). Estimasi non-resmi."
        }
        return GradingOutcome.SubjectivePending(
            wordCount = words,
            meetsMinimumWords = words >= 10 || hasAudioRecording,
            hasAudioRecording = hasAudioRecording,
            feedbackMessage = msg,
        )
    }

    private fun gradeListenRepeat(
        question: QuestionItem,
        transcript: String,
        hasAudioRecording: Boolean,
    ): GradingOutcome.SubjectivePending {
        val words = countWords(transcript)
        val target = question.referenceText ?: question.answer
        val overlapMsg = if (transcript.isNotBlank() && target.isNotBlank()) {
            val metrics = SpeakingMetricsAnalyzer().analyzeTranscript(transcript, 8.0, target)
            val pct = ((metrics.overlapRatioWithReference ?: 0.0) * 100).toInt()
            "Kemiripan transkrip lokal: $pct% terhadap teks acuan (estimasi non-resmi)."
        } else if (hasAudioRecording) {
            "Rekaman Listen & Repeat tersimpan; aktifkan Whisper ONNX untuk transkrip otomatis atau edit manual."
        } else {
            "Belum ada rekaman suara."
        }
        return GradingOutcome.SubjectivePending(
            wordCount = words,
            meetsMinimumWords = hasAudioRecording || words > 0,
            hasAudioRecording = hasAudioRecording,
            feedbackMessage = overlapMsg,
        )
    }

    private fun skippedOutcome(question: QuestionItem) = GradingOutcome.Objective(
        isCorrect = false,
        isSkipped = true,
        earnedPoints = 0,
        maxPoints = question.points,
        normalizedUserAnswer = "",
        feedbackMessage = "Dilewati. Kunci: ${question.answer.replace("|", ", ")}",
    )

    companion object {
        private val TRAILING_PUNCTUATION = Regex("[.!?,:;]+$")

        fun normalizeText(input: String): String {
            val cleaned = input.trim()
                .lowercase()
                .replace(Regex("\\s+"), " ")
                .replace(TRAILING_PUNCTUATION, "")
                .trim()
            return when (cleaned) {
                "t" -> "true"
                "f" -> "false"
                "ng" -> "not given"
                "y" -> "yes"
                "n" -> "no"
                else -> cleaned
            }
        }

        fun splitPipeSet(input: String): Set<String> =
            input.split("|").map(::normalizeText).filter { it.isNotEmpty() }.toSet()

        fun countWords(input: String): Int {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) return 0
            return trimmed.split(Regex("\\s+")).size
        }
    }
}
