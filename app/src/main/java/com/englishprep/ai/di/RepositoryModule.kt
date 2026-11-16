package com.englishprep.ai.di

import com.englishprep.ai.data.inference.LiteRtLmGemma4Engine
import com.englishprep.ai.data.media.Media3AudioEngine
import com.englishprep.ai.data.repository.AiAndImportRepositoryImpl
import com.englishprep.ai.data.repository.ExamCatalogRepositoryImpl
import com.englishprep.ai.data.repository.SessionProgressRepositoryImpl
import com.englishprep.ai.data.security.AndroidAppIntegrityInspector
import com.englishprep.ai.data.stt.WhisperOnnxSpeechEngine
import com.englishprep.ai.domain.engines.AudioPlayer
import com.englishprep.ai.domain.engines.LocalLlmEngine
import com.englishprep.ai.domain.engines.SpeechToTextEngine
import com.englishprep.ai.domain.repositories.AiAndImportRepository
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import com.englishprep.ai.domain.security.SecurityPostureInspector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExamCatalogRepository(impl: ExamCatalogRepositoryImpl): ExamCatalogRepository

    @Binds
    @Singleton
    abstract fun bindSessionProgressRepository(impl: SessionProgressRepositoryImpl): SessionProgressRepository

    @Binds
    @Singleton
    abstract fun bindAiAndImportRepository(impl: AiAndImportRepositoryImpl): AiAndImportRepository

    @Binds
    @Singleton
    abstract fun bindAudioPlayer(impl: Media3AudioEngine): AudioPlayer

    @Binds
    @Singleton
    abstract fun bindLocalLlmEngine(impl: LiteRtLmGemma4Engine): LocalLlmEngine

    @Binds
    @Singleton
    abstract fun bindSpeechToTextEngine(impl: WhisperOnnxSpeechEngine): SpeechToTextEngine

    @Binds
    @Singleton
    abstract fun bindSecurityPostureInspector(impl: AndroidAppIntegrityInspector): SecurityPostureInspector
}
