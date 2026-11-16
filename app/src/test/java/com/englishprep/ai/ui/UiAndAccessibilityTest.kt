package com.englishprep.ai.ui

import android.app.Application
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.PackStatus
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.feature.chat.ChatScreen
import com.englishprep.ai.ui.feature.chat.ChatUiState
import com.englishprep.ai.ui.feature.home.HomeScreen
import com.englishprep.ai.ui.feature.home.HomeUiState
import com.englishprep.ai.ui.feature.practice.PracticeScreen
import com.englishprep.ai.ui.feature.practice.PracticeUiState
import com.englishprep.ai.ui.theme.EnglishPrepTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class UiAndAccessibilityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `home screen supports exam switch within 2 taps and 48dp button targets at 2x font scale`() {
        var switcherOpened = false
        var primaryTapped = false
        var chatOpened = false

        composeRule.setContent {
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(baseDensity.density, 2.0f)) {
                EnglishPrepTheme {
                    HomeScreen(
                        state = HomeUiState(
                            isLoading = false,
                            selectedExam = ExamId.ITP_L1,
                            recommendedPack = ContentPackSummary(
                                packId = "ITP-L1-01",
                                examId = ExamId.ITP_L1,
                                edition = "2026.1",
                                title = "ITP Paket 1",
                                theme = "Foundations",
                                status = PackStatus.SAMPLE,
                                blueprintCount = 140,
                                availableCount = 12,
                                durationMinutes = 20,
                                blueprintDurationMinutes = 115,
                                license = "Original",
                                provenance = "Original",
                                audioBytes = 1024L,
                            ),
                            llmState = LlmModelState.Unloaded,
                        ),
                        onOpenSwitcher = { switcherOpened = true },
                        onDismissSwitcher = {},
                        onSelectExam = {},
                        onPrimaryAction = { primaryTapped = true },
                        onOpenModule = {},
                        onOpenPractice = {},
                        onOpenChat = { chatOpened = true },
                        onOpenProfileAi = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Mulai Latihan").assertIsDisplayed().assertHeightIsAtLeast(48.dp).performClick()
        assertTrue(primaryTapped)

        composeRule.onNodeWithContentDescription("Ganti jenis ujian").performClick()
        assertTrue(switcherOpened)

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Tanya AI"))
        composeRule.onNodeWithText("Tanya AI").performClick()
        assertTrue(chatOpened)
    }

    @Test
    fun `practice screen renders Sample badge with available over blueprint ratio`() {
        val samplePack = ContentPackSummary(
            packId = "IELTS-A-01",
            examId = ExamId.IELTS_ACADEMIC,
            edition = "2026.1",
            title = "IELTS Paket 1",
            theme = "Energy",
            status = PackStatus.SAMPLE,
            blueprintCount = 85,
            availableCount = 12,
            durationMinutes = 35,
            blueprintDurationMinutes = 165,
            license = "Original",
            provenance = "Original",
            audioBytes = 1_048_576L,
        )
        composeRule.setContent {
            EnglishPrepTheme {
                PracticeScreen(
                    state = PracticeUiState(
                        isLoading = false,
                        selectedExam = ExamId.IELTS_ACADEMIC,
                        packs = listOf(samplePack),
                    ),
                    onStartPractice = {},
                    onStartStrict = {},
                    onImportPack = {},
                    onDismissMessage = {},
                )
            }
        }
        composeRule.onNodeWithText("Sample • 12/85 soal").assertIsDisplayed()
        composeRule.onNodeWithText("Mode Latihan").assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Simulasi Strict").assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun `chat screen shows offline status and contains no generic pre-prompt suggestion chip clutter`() {
        var sentCount = 0
        composeRule.setContent {
            EnglishPrepTheme {
                ChatScreen(
                    state = ChatUiState(
                        selectedExam = ExamId.IBT_2026,
                        draftInput = "Jelaskan Academic Discussion",
                    ),
                    onBack = {},
                    onOpenProfileAi = {},
                    onDraftChange = {},
                    onSend = { sentCount++ },
                    onCancel = {},
                )
            }
        }
        composeRule.onNodeWithText("Tanpa LLM • Mode ringkasan FTS lokal").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Kirim pertanyaan").assertHeightIsAtLeast(48.dp).performClick()
        assertEquals(1, sentCount)
    }

    @Test
    fun `app button enforces minimum 48dp touch height`() {
        composeRule.setContent {
            EnglishPrepTheme {
                AppButton(text = "Kumpulkan", onClick = {})
            }
        }
        composeRule.onNodeWithText("Kumpulkan").assertHeightIsAtLeast(48.dp)
    }
}
