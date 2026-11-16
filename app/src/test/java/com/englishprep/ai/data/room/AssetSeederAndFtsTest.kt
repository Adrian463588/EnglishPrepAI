package com.englishprep.ai.data.room

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.englishprep.ai.data.repository.ExamCatalogRepositoryImpl
import com.englishprep.ai.domain.grading.PackIntegrityValidator
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.PackStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class AssetSeederAndFtsTest {

    private lateinit var db: EnglishPrepDatabase
    private lateinit var seeder: AssetSeeder
    private lateinit var catalogRepo: ExamCatalogRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        db = Room.inMemoryDatabaseBuilder(context, EnglishPrepDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        seeder = AssetSeeder(context, db.catalogDao(), json)
        catalogRepo = ExamCatalogRepositoryImpl(db.catalogDao(), json)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `seeds all 3 exams 18 modules 15 sample packs and FTS index offline`() = runTest {
        seeder.seedIfNeeded()

        for (exam in ExamId.entries) {
            val modules = catalogRepo.observeModules(exam).first()
            val packs = catalogRepo.observePacks(exam).first()
            assertEquals("Expected 6 modules for $exam", 6, modules.size)
            assertEquals("Expected 5 packs for $exam", 5, packs.size)
            packs.forEach { pack ->
                assertEquals(PackStatus.SAMPLE, pack.status)
                assertEquals(12, pack.availableCount)
                val bundle = catalogRepo.loadPackBundle(pack.packId)!!
                assertEquals(12, bundle.questions.size)
                val issues = PackIntegrityValidator.validateBundle(bundle)
                assertTrue("Pack ${pack.packId} failed validation: $issues", issues.isEmpty())
            }
        }

        val badPack = catalogRepo.loadPackBundle("ITP-L1-01")!!
        val brokenBundle = badPack.copy(
            pack = badPack.pack.copy(status = PackStatus.READY),
            questions = badPack.questions + badPack.questions.first(),
        )
        val detected = PackIntegrityValidator.validateBundle(brokenBundle)
        assertTrue("Validator must catch duplicate IDs and false READY status", detected.size >= 2)

        val ragHits = catalogRepo.searchLocalCurriculum(ExamId.ITP_L1, "succulents water stomata", limit = 3)
        assertTrue("FTS search should find offline curriculum/passage hits", ragHits.isNotEmpty())

        val indonesianQueryHits = catalogRepo.searchLocalCurriculum(
            ExamId.IELTS_ACADEMIC,
            "Jelaskan konsep utama modul listening distractor",
            limit = 3,
        )
        assertTrue("FTS search should skip stopwords and find IELTS listening hits", indonesianQueryHits.isNotEmpty())
    }
}
