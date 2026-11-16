package com.englishprep.ai.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExamProfileEntity::class,
        CourseModuleEntity::class,
        CurriculumFtsEntity::class,
        ContentPackEntity::class,
        PassageEntity::class,
        MediaAssetEntity::class,
        QuestionEntity::class,
        QuestionOptionEntity::class,
        ExamSessionEntity::class,
        UserAnswerEntity::class,
        EvaluationEntity::class,
        StudyProgressEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class,
        ModelInstallEntity::class,
        SpeechRecordingEntity::class,
        ImportJobEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class EnglishPrepDatabase : RoomDatabase() {
    abstract fun catalogDao(): CatalogDao
    abstract fun sessionDao(): SessionDao
    abstract fun chatAndModelDao(): ChatAndModelDao
}
