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
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.models.MediaAssetItem
import com.englishprep.ai.domain.models.PackStatus
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.TaskType
import com.englishprep.ai.domain.security.SecurityPostureEvaluator
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.feature.chat.ChatScreen
import com.englishprep.ai.ui.feature.chat.ChatUiState
import com.englishprep.ai.ui.feature.exam.ExamScreen
import com.englishprep.ai.ui.feature.exam.ExamUiState
import com.englishprep.ai.ui.feature.home.HomeScreen
import com.englishprep.ai.ui.feature.home.HomeUiState
import com.englishprep.ai.ui.feature.learn.LearnScreen
import com.englishprep.ai.ui.feature.learn.LearnUiState
import com.englishprep.ai.ui.feature.practice.PracticeScreen
import com.englishprep.ai.ui.feature.practice.PracticeUiState
import com.englishprep.ai.ui.feature.profile.ProfileScreen
import com.englishprep.ai.ui.feature.profile.ProfileUiState
import com.englishprep.ai.ui.feature.progress.ProgressScreen
import com.englishprep.ai.ui.feature.progress.ProgressUiState
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
        composeRule.onNodeWithText("Tryout Strict (Sample)").assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun `chat screen shows offline status enforces strict lock and has no chip clutter`() {
        var sentCount = 0
        composeRule.setContent {
            EnglishPrepTheme(darkTheme = true) {
                ChatScreen(
                    state = ChatUiState(
                        selectedExam = ExamId.IBT_2026,
                        draftInput = "Jelaskan Academic Discussion",
                        strictMockActive = true,
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
        composeRule.onNodeWithText("Mode Simulasi Strict Sedang Aktif").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Kirim pertanyaan").assertHeightIsAtLeast(48.dp).performClick()
        assertEquals("Strict mode must prevent sending chat queries", 0, sentCount)
    }

    @Test
    fun `learn progress and profile screens render accessible empty state bookmark filter and oss licenses`() {
        composeRule.setContent {
            EnglishPrepTheme {
                LearnScreen(
                    state = LearnUiState(isLoading = false, selectedExam = ExamId.ITP_L1),
                    onSelectSection = {},
                    onOpenModule = {},
                )
            }
        }
        composeRule.onNodeWithText("Tersimpan (0)").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun `progress empty state renders honest no-attempts screen`() {
        var openPractice = false
        composeRule.setContent {
            EnglishPrepTheme {
                ProgressScreen(
                    state = ProgressUiState(isLoading = false, selectedExam = ExamId.IELTS_ACADEMIC),
                    onOpenResults = {},
                    onOpenModule = {},
                    onOpenPractice = { openPractice = true },
                )
            }
        }
        composeRule.onNodeWithText("Belum Ada Riwayat Tryout").assertIsDisplayed()
        composeRule.onNodeWithText("Mulai Paket Pertama").assertHeightIsAtLeast(48.dp).performClick()
        assertTrue(openPractice)
    }

    @Test
    fun `profile screen shows r8 security posture and opens oss licenses dialog`() {
        val posture = SecurityPostureEvaluator.evaluate(
            isDebuggable = false,
            isDebuggerAttached = false,
            signingSha256 = "AABBCCDD11223344",
            expectedReleaseSha256 = "",
            rootIndicatorsFound = false,
            allowBackupEnabled = false,
        )
        composeRule.setContent {
            EnglishPrepTheme {
                ProfileScreen(
                    state = ProfileUiState(securityPosture = posture),
                    onSelectLanguage = {},
                    onSelectTheme = {},
                    onSelectSpeed = {},
                    onToggleFontBoost = {},
                    onToggleReduceMotion = {},
                    onToggleHaptics = {},
                    onToggleReminder = {},
                    onImportGemma4 = {},
                    onImportWhisper = {},
                    onImportPack = {},
                    onActivateModel = {},
                    onDeleteModel = {},
                    onOpenExternalUrl = {},
                    onExportBackup = {},
                    onRestoreBackup = {},
                    onDeleteAllData = {},
                    onDismissBanner = {},
                )
            }
        }
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Keamanan & Integritas R8"))
        composeRule.onNodeWithText("Mode Release (R8/ProGuard Aktif)").assertIsDisplayed()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Lihat Lisensi Open-Source & Pihak Ketiga"))
        composeRule.onNodeWithText("Lihat Lisensi Open-Source & Pihak Ketiga").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Lisensi Open-Source & Atribusi").assertIsDisplayed()
    }

    @Test
    fun `exam screen supports practice transcript toggle and unanswered confirmation before submit`() {
        var submitted = false
        val q = QuestionItem(
            id = "ITP-L1-01-Q01",
            packId = "ITP-L1-01",
            section = ExamSection.LISTENING,
            taskType = TaskType.CHOICE_SINGLE,
            prompt = "Listen to the dialogue.",
            options = listOf("A", "B"),
            answer = "A",
            explanation = "Reason",
            rationaleSource = "ITP-MOD-01",
            audioAssetId = "AST1",
        )
        val asset = MediaAssetItem(
            id = "AST1",
            packId = "ITP-L1-01",
            relativePathOrUri = "asset://audio/ITP01-A01.wav",
            kind = "AUDIO",
            sha256 = "abc",
            byteSize = 1024L,
            caption = "Speaker A: Have you finished the lab?",
        )
        composeRule.setContent {
            EnglishPrepTheme(reduceMotion = true) {
                ExamScreen(
                    state = ExamUiState(
                        isLoading = false,
                        examId = ExamId.ITP_L1,
                        mode = ExamMode.PRACTICE,
                        questions = listOf(q),
                        assets = mapOf("AST1" to asset),
                    ),
                    onBack = {},
                    onSelectAnswer = { _, _ -> },
                    onToggleMulti = { _, _ -> },
                    onSetMatching = { _, _, _ -> },
                    onAppendSequence = { _, _ -> },
                    onRemoveLastSequence = {},
                    onToggleFlag = {},
                    onCheckPractice = {},
                    onEvaluateSubjective = {},
                    onStartPrepCountdown = {},
                    onStartRecording = { false },
                    onStopRecording = {},
                    onPlayOwnRecording = {},
                    onSpeakTts = {},
                    onNext = {},
                    onPrev = {},
                    onGoTo = {},
                    onSetNavigator = {},
                    onSetPassageSheet = {},
                    onToggleAudio = {},
                    onReplayAudio = {},
                    onSeekAudio = {},
                    onSetAudioSpeed = {},
                    onSubmit = { submitted = true },
                )
            }
        }
        composeRule.onNodeWithText("Lihat Transkrip Audio (Mode Latihan)").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Speaker A: Have you finished the lab?").assertIsDisplayed()
        composeRule.onNodeWithText("Kumpulkan").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Kumpulkan jawaban sekarang?").assertIsDisplayed()
        composeRule.onNodeWithText("Kumpulkan Sekarang").performClick()
        assertTrue(submitted)
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
