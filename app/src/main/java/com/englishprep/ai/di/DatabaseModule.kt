package com.englishprep.ai.di

import android.content.Context
import androidx.room.Room
import com.englishprep.ai.data.room.CatalogDao
import com.englishprep.ai.data.room.ChatAndModelDao
import com.englishprep.ai.data.room.EnglishPrepDatabase
import com.englishprep.ai.data.room.SessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EnglishPrepDatabase =
        Room.databaseBuilder(
            context,
            EnglishPrepDatabase::class.java,
            "english_prep.db",
        ).build()

    @Provides
    fun provideCatalogDao(db: EnglishPrepDatabase): CatalogDao = db.catalogDao()

    @Provides
    fun provideSessionDao(db: EnglishPrepDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideChatAndModelDao(db: EnglishPrepDatabase): ChatAndModelDao = db.chatAndModelDao()
}
