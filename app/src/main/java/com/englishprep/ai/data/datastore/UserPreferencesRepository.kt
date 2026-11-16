package com.englishprep.ai.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.englishprep.ai.domain.models.ExamId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "english_prep_settings",
)

enum class AppLanguage(val code: String, val label: String) {
    ID("id", "Bahasa Indonesia"),
    EN("en", "English"),
}

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark"),
}

data class UserSettings(
    val selectedExam: ExamId = ExamId.ITP_L1,
    val language: AppLanguage = AppLanguage.ID,
    val audioSpeedPractice: Float = 1.0f,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val activeLlmModelId: String? = null,
    val activeSttModelId: String? = null,
    val reduceMotion: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val fontScaleBoost: Boolean = false,
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val SELECTED_EXAM = stringPreferencesKey("selected_exam")
        val LANGUAGE = stringPreferencesKey("language")
        val AUDIO_SPEED = floatPreferencesKey("audio_speed_practice")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACTIVE_LLM = stringPreferencesKey("active_llm_model_id")
        val ACTIVE_STT = stringPreferencesKey("active_stt_model_id")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val HAPTICS = booleanPreferencesKey("haptics_enabled")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val FONT_SCALE_BOOST = booleanPreferencesKey("font_scale_boost")
    }

    val settingsFlow: Flow<UserSettings> = context.userSettingsDataStore.data.map { prefs ->
        UserSettings(
            selectedExam = prefs[Keys.SELECTED_EXAM]?.let { runCatching { ExamId.valueOf(it) }.getOrNull() }
                ?: ExamId.ITP_L1,
            language = prefs[Keys.LANGUAGE]?.let { code -> AppLanguage.entries.find { it.code == code } }
                ?: AppLanguage.ID,
            audioSpeedPractice = (prefs[Keys.AUDIO_SPEED] ?: 1.0f).coerceIn(0.75f, 1.5f),
            themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            activeLlmModelId = prefs[Keys.ACTIVE_LLM],
            activeSttModelId = prefs[Keys.ACTIVE_STT],
            reduceMotion = prefs[Keys.REDUCE_MOTION] ?: false,
            hapticsEnabled = prefs[Keys.HAPTICS] ?: true,
            reminderEnabled = prefs[Keys.REMINDER_ENABLED] ?: false,
            reminderHour = prefs[Keys.REMINDER_HOUR] ?: 20,
            reminderMinute = prefs[Keys.REMINDER_MINUTE] ?: 0,
            fontScaleBoost = prefs[Keys.FONT_SCALE_BOOST] ?: false,
        )
    }

    suspend fun setSelectedExam(examId: ExamId) {
        context.userSettingsDataStore.edit { it[Keys.SELECTED_EXAM] = examId.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.userSettingsDataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setAudioSpeedPractice(speed: Float) {
        context.userSettingsDataStore.edit { it[Keys.AUDIO_SPEED] = speed.coerceIn(0.75f, 1.5f) }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.userSettingsDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setActiveLlmModel(modelId: String?) {
        context.userSettingsDataStore.edit { prefs ->
            if (modelId == null) prefs.remove(Keys.ACTIVE_LLM) else prefs[Keys.ACTIVE_LLM] = modelId
        }
    }

    suspend fun setActiveSttModel(modelId: String?) {
        context.userSettingsDataStore.edit { prefs ->
            if (modelId == null) prefs.remove(Keys.ACTIVE_STT) else prefs[Keys.ACTIVE_STT] = modelId
        }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[Keys.REDUCE_MOTION] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[Keys.HAPTICS] = enabled }
    }

    suspend fun setReminder(enabled: Boolean, hour: Int = 20, minute: Int = 0) {
        context.userSettingsDataStore.edit {
            it[Keys.REMINDER_ENABLED] = enabled
            it[Keys.REMINDER_HOUR] = hour
            it[Keys.REMINDER_MINUTE] = minute
        }
    }

    suspend fun setFontScaleBoost(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[Keys.FONT_SCALE_BOOST] = enabled }
    }
}
