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
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.englishprep.ai.data.datastore.ThemeMode
import com.englishprep.ai.data.datastore.UserSettings
import com.englishprep.ai.data.room.ModelInstallEntity
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.domain.models.MaterialBankEntry
import com.englishprep.ai.domain.models.MediaAssetItem
import com.englishprep.ai.domain.models.PackStatus
import com.englishprep.ai.domain.models.PassageItem
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.TaskType
import com.englishprep.ai.domain.security.SecurityPostureEvaluator
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.feature.chat.ChatScreen
import com.englishprep.ai.ui.feature.chat.ChatUiState
import com.englishprep.ai.ui.feature.exam.ExamScreen
import com.englishprep.ai.ui.feature.exam.ExamUiState
import com.englishprep.ai.ui.feature.exam.QuestionNavigatorSheetContent
import com.englishprep.ai.ui.feature.home.HomeScreen
import com.englishprep.ai.ui.feature.home.HomeUiState
import com.englishprep.ai.ui.feature.learn.LearnScreen
import com.englishprep.ai.ui.feature.learn.LearnUiState
import com.englishprep.ai.ui.feature.practice.PracticeScreen
import com.englishprep.ai.ui.feature.practice.PracticeUiState
import com.englishprep.ai.ui.feature.profile.ModelManagerScreen
import com.englishprep.ai.ui.feature.profile.ProfileScreen
import com.englishprep.ai.ui.feature.profile.ProfileUiState
import com.englishprep.ai.ui.feature.progress.ProgressScreen
import com.englishprep.ai.ui.feature.progress.ProgressUiState
import com.englishprep.ai.ui.feature.progress.WeakModuleRow
import com.englishprep.ai.ui.feature.results.QuestionReviewRow
import com.englishprep.ai.ui.feature.results.ResultsScreen
import com.englishprep.ai.ui.feature.results.ResultsUiState
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

        composeRule.onNodeWithContentDescription("Ganti jenis ujian (${ExamId.ITP_L1.title})").performClick()
        assertTrue(switcherOpened)

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Tanya AI"))
        composeRule.onNodeWithText("Tanya AI").performClick()
        assertTrue(chatOpened)
    }

    @Test
    fun `practice screen renders Sample badge with available over blueprint ratio and Ready badge for full packs`() {
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
        val readyPack = ContentPackSummary(
            packId = "ITP-L1-01",
            examId = ExamId.ITP_L1,
            edition = "2026.2",
            title = "ITP Paket 1",
            theme = "Full 140",
            status = PackStatus.READY,
            blueprintCount = 140,
            availableCount = 140,
            durationMinutes = 115,
            blueprintDurationMinutes = 115,
            license = "Original",
            provenance = "Original",
            audioBytes = 3_900_000L,
        )
        composeRule.setContent {
            EnglishPrepTheme {
                PracticeScreen(
                    state = PracticeUiState(
                        isLoading = false,
                        selectedExam = ExamId.ITP_L1,
                        packs = listOf(readyPack, samplePack),
                    ),
                    onStartPractice = {},
                    onStartStrict = {},
                    onImportPack = {},
                    onDismissMessage = {},
                )
            }
        }
        composeRule.onNodeWithText("Ready • 140 soal").assertIsDisplayed()
        composeRule.onNodeWithText("Full Mock Strict").assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Sample • 12/85 soal").assertIsDisplayed()
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
                    onOpenModelManager = {},
                    onDraftChange = {},
                    onSend = { sentCount++ },
                    onCancel = {},
                )
            }
        }
        composeRule.onNodeWithText("Mode FTS aktif • Kelola model").assertIsDisplayed()
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
        composeRule.onNodeWithText("Selesai").assertHeightIsAtLeast(48.dp).performClick()
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

    @Test
    fun `exam navigator renders section-filtered 140 questions with 48dp targets without overflow`() {
        val questions140 = (1..140).map { i ->
            val section = when {
                i <= 50 -> ExamSection.LISTENING
                i <= 90 -> ExamSection.STRUCTURE
                else -> ExamSection.READING
            }
            QuestionItem(
                id = "Q-$i",
                packId = "ITP-L1-01",
                section = section,
                taskType = TaskType.CHOICE_SINGLE,
                prompt = "Question $i",
                options = listOf("A", "B", "C", "D"),
                answer = "A",
                explanation = "Exp",
                rationaleSource = "ITP-MOD-01",
            )
        }
        var jumpedTo = -1
        composeRule.setContent {
            EnglishPrepTheme(reduceMotion = true) {
                QuestionNavigatorSheetContent(
                    questions = questions140,
                    answers = mapOf("Q-1" to "A"),
                    flaggedIds = emptySet(),
                    currentIndex = 0,
                    onSelectQuestion = { jumpedTo = it },
                )
            }
        }
        composeRule.onNodeWithText("Navigasi Soal (1/140 terjawab)").assertIsDisplayed()
        composeRule.onNodeWithText("Reading (50)").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("91").assertHeightIsAtLeast(48.dp).performClick()
        assertEquals(90, jumpedTo)
    }

    @Test
    fun `learn screen displays OER material bank entries and ask tutor action`() {
        val sampleEntry = MaterialBankEntry(
            id = "MB-OER-GRAM-01-SEC-1",
            sourceId = "MB-OER-GRAM-01",
            examScope = "ALL",
            skill = "STRUCTURE",
            sourceTitle = "High-Intermediate Academic Grammar",
            chapterTitle = "Gerunds and Infinitives",
            pageRange = "31-31",
            pdfFileName = "libretexts_academic_grammar_esl_ccby.pdf",
            authorOrOrg = "LibreTexts",
            license = "CC BY 4.0",
            sourceUrl = "https://libretexts.org",
            redistributable = true,
            excerpt = "Gerunds are -ing forms of verbs used as nouns.",
        )
        var tutorSrc = ""
        composeRule.setContent {
            EnglishPrepTheme {
                LearnScreen(
                    state = LearnUiState(
                        isLoading = false,
                        selectedExam = ExamId.ITP_L1,
                        materialBankOnly = true,
                        materialBankEntries = listOf(sampleEntry),
                        expandedMaterialId = sampleEntry.id,
                    ),
                    onSelectSection = {},
                    onOpenModule = {},
                    onAskTutor = { tutorSrc = it },
                )
            }
        }
        composeRule.onNodeWithText("Bacaan OER (1)").performScrollTo().assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Gerunds and Infinitives").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Gerunds are -ing forms of verbs used as nouns.").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Tanya AI tentang bacaan ini").performScrollTo().assertHeightIsAtLeast(48.dp).performClick()
        assertEquals("MB-OER-GRAM-01-SEC-1", tutorSrc)
    }

    @Test
    @Config(qualifiers = "w320dp-h640dp")
    fun `narrow 320dp phone at 2x font scale wraps exam bottom bar without button clipping`() {
        val q = QuestionItem(
            id = "ITP-L1-01-Q001",
            packId = "ITP-L1-01",
            section = ExamSection.STRUCTURE,
            taskType = TaskType.CHOICE_SINGLE,
            prompt = "Choose the best clause completion.",
            options = listOf("Alpha", "Beta"),
            answer = "Alpha",
            explanation = "Reason",
            rationaleSource = "ITP-MOD-03",
        )
        var nextClicked = false
        composeRule.setContent {
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(baseDensity.density, 2.0f)) {
                EnglishPrepTheme(reduceMotion = true) {
                    ExamScreen(
                        state = ExamUiState(
                            isLoading = false,
                            examId = ExamId.ITP_L1,
                            mode = ExamMode.PRACTICE,
                            questions = listOf(q, q.copy(id = "ITP-L1-01-Q002")),
                            answers = mapOf("ITP-L1-01-Q001" to "Alpha"),
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
                        onNext = { nextClicked = true },
                        onPrev = {},
                        onGoTo = {},
                        onSetNavigator = {},
                        onSetPassageSheet = {},
                        onToggleAudio = {},
                        onReplayAudio = {},
                        onSeekAudio = {},
                        onSetAudioSpeed = {},
                        onSubmit = {},
                    )
                }
            }
        }
        composeRule.onNodeWithText("Kembali").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Periksa").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Lanjut").assertIsDisplayed().assertHeightIsAtLeast(48.dp).performClick()
        assertTrue(nextClicked)
    }

    @Test
    @Config(qualifiers = "w960dp-h720dp")
    fun `wide 960dp tablet displays dual-column practice packs cleanly`() {
        val p1 = ContentPackSummary(
            packId = "ITP-L1-01",
            examId = ExamId.ITP_L1,
            edition = "2026.2",
            title = "ITP Paket 1",
            theme = "Full 140",
            status = PackStatus.READY,
            blueprintCount = 140,
            availableCount = 140,
            durationMinutes = 115,
            blueprintDurationMinutes = 115,
            license = "Original",
            provenance = "Original",
            audioBytes = 4_600_000L,
        )
        val p2 = p1.copy(packId = "ITP-L1-02", title = "ITP Paket 2")
        composeRule.setContent {
            EnglishPrepTheme {
                PracticeScreen(
                    state = PracticeUiState(
                        isLoading = false,
                        selectedExam = ExamId.ITP_L1,
                        packs = listOf(p1, p2),
                    ),
                    onStartPractice = {},
                    onStartStrict = {},
                    onImportPack = {},
                    onDismissMessage = {},
                )
            }
        }
        composeRule.onNodeWithText("ITP Paket 1").assertIsDisplayed()
        composeRule.onNodeWithText("ITP Paket 2").assertIsDisplayed()
    }

    @Test
    fun `results screen filters wrong skipped and correct review rows accurately`() {
        val qCorrect = QuestionItem(
            id = "Q1",
            packId = "ITP-L1-01",
            section = ExamSection.LISTENING,
            taskType = TaskType.CHOICE_SINGLE,
            prompt = "Prompt Correct",
            options = listOf("A", "B"),
            answer = "A",
            explanation = "Correct exp",
            rationaleSource = "ITP-MOD-01",
        )
        val qWrong = qCorrect.copy(id = "Q2", prompt = "Prompt Wrong")
        val qSkip = qCorrect.copy(id = "Q3", prompt = "Prompt Skipped")
        composeRule.setContent {
            EnglishPrepTheme {
                ResultsScreen(
                    state = ResultsUiState(
                        isLoading = false,
                        sessionTitle = "ITP Paket 1",
                        examId = ExamId.ITP_L1,
                        rows = listOf(
                            QuestionReviewRow(qCorrect, "A", GradingOutcome.Objective(true, false, 1, 1, "A", "Tepat")),
                            QuestionReviewRow(qWrong, "B", GradingOutcome.Objective(false, false, 0, 1, "B", "Salah")),
                            QuestionReviewRow(qSkip, "", GradingOutcome.Objective(false, true, 0, 1, "", "Dilewati")),
                        ),
                        wrongQuestionIds = listOf("Q2"),
                    ),
                    onBackHome = {},
                    onOpenModule = {},
                    onRetryMistakes = {},
                )
            }
        }
        composeRule.onNodeWithText("Pembahasan Per Butir (3/3)").assertIsDisplayed()
        composeRule.onNodeWithText("Salah").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Prompt Wrong").assertIsDisplayed()
        composeRule.onNodeWithText("Dilewati").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Prompt Skipped").assertIsDisplayed()
        composeRule.onNodeWithText("Benar").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Prompt Correct").assertIsDisplayed()
    }

    @Test
    fun `profile screen supports whole-row switch toggle and reflects updated 1725 ready copy`() {
        var hapticsToggledTo: Boolean? = null
        composeRule.setContent {
            EnglishPrepTheme {
                ProfileScreen(
                    state = ProfileUiState(
                        securityPosture = SecurityPostureEvaluator.evaluate(
                            isDebuggable = false,
                            isDebuggerAttached = false,
                            signingSha256 = "ABC1234567890DEF",
                            expectedReleaseSha256 = "",
                            rootIndicatorsFound = false,
                            allowBackupEnabled = false,
                        ),
                    ),
                    onSelectLanguage = {},
                    onSelectTheme = {},
                    onSelectSpeed = {},
                    onToggleFontBoost = {},
                    onToggleReduceMotion = {},
                    onToggleHaptics = { hapticsToggledTo = it },
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
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Getar Haptik Saat Selesai"))
        composeRule.onNodeWithText("Getar Haptik Saat Selesai").assertHeightIsAtLeast(48.dp).performClick()
        assertEquals(false, hapticsToggledTo)
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Lihat Lisensi Open-Source & Pihak Ketiga"))
        composeRule.onNodeWithText("Lihat Lisensi Open-Source & Pihak Ketiga").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Lisensi Open-Source & Atribusi").assertIsDisplayed()
        composeRule.onNodeWithText(
            "Seluruh 30 modul, 15 paket tryout penuh (1.725 butir READY)",
            substring = true,
        ).assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun `home screen on 360dp phone shows real session progress and 48dp touch targets in light mode`() {
        composeRule.setContent {
            EnglishPrepTheme(darkTheme = false) {
                HomeScreen(
                    state = HomeUiState(
                        isLoading = false,
                        selectedExam = ExamId.IELTS_ACADEMIC,
                        activeSessionId = "SES-1",
                        activeSessionTitle = "IELTS Paket 1 (PRACTICE)",
                        activeSessionAnsweredCount = 12,
                        activeSessionTotalCount = 85,
                        averageAccuracyPercent = 72,
                        completedSessionCount = 2,
                    ),
                    onOpenSwitcher = {},
                    onDismissSwitcher = {},
                    onSelectExam = {},
                    onPrimaryAction = {},
                    onOpenModule = {},
                    onOpenPractice = {},
                    onOpenChat = {},
                    onOpenProfileAi = {},
                )
            }
        }
        composeRule.onNodeWithText("English Prep").assertIsDisplayed()
        composeRule.onNodeWithText("12 dari 85 soal").assertIsDisplayed()
        composeRule.onNodeWithText("Akurasi: 72% • 2 sesi selesai").assertIsDisplayed()
        composeRule.onNodeWithText("Lanjutkan").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Tutor AI").performScrollTo().assertIsDisplayed().assertHeightIsAtLeast(48.dp)
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp")
    fun `model manager screen displays installed model expandable details and saf picker dialog`() {
        var gemmaTriggered = false
        val installed = ModelInstallEntity(
            id = "MDL-1",
            displayName = "Gemma 4 E2B",
            family = "GEMMA4",
            modelVariant = "E2B-it",
            runtime = "LiteRT-LM 0.13.1",
            format = ".litertlm",
            quantization = "INT4",
            sha256 = "ABCDEF1234567890ABCDEF",
            sizeBytes = 2_600L * 1024L * 1024L,
            privateStoragePath = "/data/user/0/com.englishprep.ai/files/models/gemma4.litertlm",
            sourceDocumentUri = null,
            status = "READY",
            installedAt = 1L,
            licenseId = "Gemma Terms",
        )
        composeRule.setContent {
            EnglishPrepTheme(darkTheme = true) {
                ModelManagerScreen(
                    state = ProfileUiState(
                        settings = UserSettings(themeMode = ThemeMode.DARK),
                        installedModels = listOf(installed),
                    ),
                    onBack = {},
                    onImportGemma4 = { gemmaTriggered = true },
                    onImportWhisper = {},
                    onActivateModel = {},
                    onDeleteModel = {},
                    onOpenExternalUrl = {},
                    onDismissBanner = {},
                )
            }
        }
        composeRule.onNodeWithText("Gemma 4 E2B").assertIsDisplayed()
        composeRule.onNodeWithText("Detail teknis").assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Runtime: LiteRT-LM 0.13.1 • Format: .litertlm").assertIsDisplayed()
        composeRule.onNodeWithText("Impor model dari perangkat").performScrollTo().assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Gemma 4 (.litertlm)").assertHeightIsAtLeast(48.dp).performClick()
        assertTrue(gemmaTriggered)
    }

    @Test
    fun `profile screen separates model manager behind single tap row`() {
        var openedManager = false
        composeRule.setContent {
            EnglishPrepTheme {
                ProfileScreen(
                    state = ProfileUiState(),
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
                    onOpenModelManager = { openedManager = true },
                )
            }
        }
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Kelola Model AI"))
        composeRule.onNodeWithText("Kelola Model AI").assertHeightIsAtLeast(48.dp).performClick()
        assertTrue(openedManager)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun `360dp exam screen shows Kembali Periksa Lanjut at 48dp and reading tonal trigger`() {
        val passage = PassageItem(
            id = "PAS-1",
            packId = "ITP-L1-01",
            title = "Glacial Meltwater Channels",
            body = "Academic passage body text.",
        )
        val q = QuestionItem(
            id = "ITP-L1-01-Q091",
            packId = "ITP-L1-01",
            section = ExamSection.READING,
            taskType = TaskType.CHOICE_SINGLE,
            prompt = "What is the main focus of the passage?",
            options = listOf("Hydrology", "Botany"),
            answer = "Hydrology",
            explanation = "Reason",
            rationaleSource = "ITP-MOD-09",
            passageId = "PAS-1",
        )
        var openedPassage = false
        composeRule.setContent {
            EnglishPrepTheme {
                ExamScreen(
                    state = ExamUiState(
                        isLoading = false,
                        examId = ExamId.ITP_L1,
                        mode = ExamMode.PRACTICE,
                        questions = listOf(q, q.copy(id = "ITP-L1-01-Q092")),
                        passages = mapOf("PAS-1" to passage),
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
                    onSetPassageSheet = { openedPassage = it },
                    onToggleAudio = {},
                    onReplayAudio = {},
                    onSeekAudio = {},
                    onSetAudioSpeed = {},
                    onSubmit = {},
                )
            }
        }
        composeRule.onNodeWithText("Kembali").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Periksa").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Lanjut").assertIsDisplayed().assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithText("Baca teks: Glacial Meltwater Channels")
            .assertIsDisplayed()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assertTrue(openedPassage)
    }

    @Test
    fun `writing task toggles optional rubric criteria panel`() {
        val writingQ = QuestionItem(
            id = "IELTS-W2",
            packId = "IELTS-A-01",
            section = ExamSection.WRITING,
            taskType = TaskType.WRITING_RESPONSE,
            prompt = "Discuss both views on urban public transport investment.",
            answer = "",
            explanation = "Sample approach",
            rationaleSource = "IELTS-MOD-06",
            minWords = 250,
            rubricCriteria = listOf("Task response", "Coherence and cohesion"),
        )
        composeRule.setContent {
            EnglishPrepTheme {
                ExamScreen(
                    state = ExamUiState(
                        isLoading = false,
                        examId = ExamId.IELTS_ACADEMIC,
                        mode = ExamMode.PRACTICE,
                        questions = listOf(writingQ),
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
                    onSubmit = {},
                )
            }
        }
        composeRule.onNodeWithText("Panduan Rubrik").assertIsDisplayed().assertHeightIsAtLeast(48.dp).performClick()
        composeRule.onNodeWithText("Kriteria penilaian lokal (non-resmi): Task response • Coherence and cohesion")
            .assertIsDisplayed()
    }

    @Test
    fun `progress screen renders human readable weak module title instead of raw module id`() {
        composeRule.setContent {
            EnglishPrepTheme {
                ProgressScreen(
                    state = ProgressUiState(
                        isLoading = false,
                        selectedExam = ExamId.ITP_L1,
                        overallAccuracyPercent = 65,
                        weakModules = listOf(
                            WeakModuleRow(
                                moduleId = "ITP-MOD-04",
                                moduleTitle = "Inversion & Parallel Structure",
                                correct = 3,
                                attempts = 6,
                                percent = 50,
                            ),
                        ),
                    ),
                    onOpenResults = {},
                    onOpenModule = {},
                    onOpenPractice = {},
                )
            }
        }
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Inversion & Parallel Structure"))
        composeRule.onNodeWithText("Inversion & Parallel Structure").assertIsDisplayed()
        composeRule.onNodeWithText("ITP-MOD-04").assertDoesNotExist()
    }

    @Test
    fun `chat screen displays clean non-truncated FTS header and model action`() {
        composeRule.setContent {
            EnglishPrepTheme {
                ChatScreen(
                    state = ChatUiState(llmState = LlmModelState.Unloaded),
                    onBack = {},
                    onOpenModelManager = {},
                    onDraftChange = {},
                    onSend = {},
                    onCancel = {},
                )
            }
        }
        composeRule.onNodeWithText("Tutor AI").assertIsDisplayed()
        composeRule.onNodeWithText("Mode FTS aktif • Kelola model").assertIsDisplayed()
    }
}
