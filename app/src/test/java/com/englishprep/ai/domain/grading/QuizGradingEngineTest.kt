package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.TaskType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizGradingEngineTest {

    private val engine = QuizGradingEngine()

    @Test
    fun `single choice scores exact option deterministically`() {
        val q = QuestionItem(
            id = "Q1",
            packId = "ITP-L1-01",
            section = ExamSection.STRUCTURE,
            taskType = TaskType.CHOICE_SINGLE,
            prompt = "Test",
            options = listOf("is", "are"),
            answer = "is",
            explanation = "Singular subject",
            rationaleSource = "ITP-MOD-03",
        )
        val correct = engine.grade(q, "is") as GradingOutcome.Objective
        val wrong = engine.grade(q, "are") as GradingOutcome.Objective
        val skipped = engine.grade(q, "") as GradingOutcome.Objective

        assertTrue(correct.isCorrect)
        assertEquals(1, correct.earnedPoints)
        assertFalse(wrong.isCorrect)
        assertFalse(wrong.isSkipped)
        assertFalse(skipped.isCorrect)
        assertTrue(skipped.isSkipped)
    }

    @Test
    fun `multi select accepts selections in any order and rejects partial matches`() {
        val q = QuestionItem(
            id = "Q2",
            packId = "IELTS-A-01",
            section = ExamSection.LISTENING,
            taskType = TaskType.CHOICE_MULTI,
            prompt = "Pick 2",
            options = listOf("A", "B", "C", "D"),
            answer = "A|C",
            explanation = "Both A and C",
            rationaleSource = "IELTS-MOD-01",
        )
        val reversedOrder = engine.grade(q, "C|A") as GradingOutcome.Objective
        val partial = engine.grade(q, "A") as GradingOutcome.Objective

        assertTrue(reversedOrder.isCorrect)
        assertFalse(partial.isCorrect)
    }

    @Test
    fun `fill blank normalizes whitespace case and enforces IELTS word limit`() {
        val q = QuestionItem(
            id = "Q3",
            packId = "IELTS-A-01",
            section = ExamSection.LISTENING,
            taskType = TaskType.FILL_BLANK,
            prompt = "NO MORE THAN TWO WORDS",
            answer = "west wing",
            acceptedAnswers = listOf("west wing", "west"),
            explanation = "Corrected location",
            rationaleSource = "IELTS-MOD-01",
            maxWords = 2,
        )
        val valid = engine.grade(q, "  West   Wing ") as GradingOutcome.Objective
        val overLimit = engine.grade(q, "the west wing") as GradingOutcome.Objective

        assertTrue(valid.isCorrect)
        assertFalse(overLimit.isCorrect)
        assertTrue(overLimit.feedbackMessage.contains("word limit", ignoreCase = true))
    }

    @Test
    fun `matching and sentence build require exact pairs and order`() {
        val matching = QuestionItem(
            id = "Q4",
            packId = "IELTS-A-01",
            section = ExamSection.READING,
            taskType = TaskType.MATCHING,
            prompt = "Match",
            answer = "A::H1|B::H2",
            explanation = "Heading match",
            rationaleSource = "IELTS-MOD-03",
        )
        val matched = engine.grade(matching, "B::H2|A::H1") as GradingOutcome.Objective
        assertTrue(matched.isCorrect)

        val build = QuestionItem(
            id = "Q5",
            packId = "IBT26-01",
            section = ExamSection.WRITING,
            taskType = TaskType.SENTENCE_BUILD,
            prompt = "Order",
            options = listOf("where", "the", "lab", "moved"),
            answer = "where|the|lab|moved",
            explanation = "Embedded order",
            rationaleSource = "IBT-MOD-05",
        )
        val builtOk = engine.grade(build, "where|the|lab|moved") as GradingOutcome.Objective
        val builtWrong = engine.grade(build, "where|moved|the|lab") as GradingOutcome.Objective
        assertTrue(builtOk.isCorrect)
        assertFalse(builtWrong.isCorrect)
    }

    @Test
    fun `writing response returns subjective pending with word check`() {
        val writing = QuestionItem(
            id = "Q6",
            packId = "IELTS-A-01",
            section = ExamSection.WRITING,
            taskType = TaskType.WRITING_RESPONSE,
            prompt = "Task 1",
            explanation = "Needs overview",
            rationaleSource = "IELTS-MOD-04",
            minWords = 150,
        )
        val shortDraft = (1..40).joinToString(" ") { "word$it" }
        val outcome = engine.grade(writing, shortDraft) as GradingOutcome.SubjectivePending
        assertEquals(40, outcome.wordCount)
        assertFalse(outcome.meetsMinimumWords)
    }

    @Test
    fun `tfng ynng and short answer punctuation follow explicit normalization policy`() {
        val tfng = QuestionItem(
            id = "Q7",
            packId = "IELTS-A-01",
            section = ExamSection.READING,
            taskType = TaskType.CHOICE_SINGLE,
            prompt = "True / False / Not Given: statement",
            options = listOf("True", "False", "Not Given"),
            answer = "Not Given",
            explanation = "No passage evidence.",
            rationaleSource = "IELTS-MOD-03",
        )
        assertTrue((engine.grade(tfng, "ng") as GradingOutcome.Objective).isCorrect)
        assertTrue((engine.grade(tfng, "NOT GIVEN.") as GradingOutcome.Objective).isCorrect)

        val ynng = QuestionItem(
            id = "Q8",
            packId = "IELTS-A-01",
            section = ExamSection.READING,
            taskType = TaskType.FILL_BLANK,
            prompt = "YES, NO or NOT GIVEN",
            answer = "YES",
            explanation = "Writer agrees.",
            rationaleSource = "IELTS-MOD-03",
        )
        assertTrue((engine.grade(ynng, "y") as GradingOutcome.Objective).isCorrect)
        assertTrue((engine.grade(ynng, "yes!") as GradingOutcome.Objective).isCorrect)
    }
}
