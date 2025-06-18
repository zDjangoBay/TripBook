package com.android.tripbook.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val textSize: TextSize = TextSize.MEDIUM,
    val isAccessibilityModeEnabled: Boolean = false,
    val preferredLanguage: String = "fr",
    val autoTranslateEnabled: Boolean = true,
    val speechRate: Float = 1.0f
)

enum class TextSize(val scaleFactor: Float, val displayName: String) {
    SMALL(0.85f, "Petit"),
    MEDIUM(1.0f, "Moyen"),
    LARGE(1.15f, "Grand"),
    EXTRA_LARGE(1.3f, "Très grand")
}

class UserPreferencesRepository(private val context: Context) {
    
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val TEXT_SIZE_KEY = stringPreferencesKey("text_size")
        private val ACCESSIBILITY_MODE_KEY = booleanPreferencesKey("accessibility_mode")
        private val PREFERRED_LANGUAGE_KEY = stringPreferencesKey("preferred_language")
        private val AUTO_TRANSLATE_KEY = booleanPreferencesKey("auto_translate")
        private val SPEECH_RATE_KEY = floatPreferencesKey("speech_rate")
    }
    
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                isDarkMode = preferences[DARK_MODE_KEY] ?: false,
                textSize = TextSize.valueOf(
                    preferences[TEXT_SIZE_KEY] ?: TextSize.MEDIUM.name
                ),
                isAccessibilityModeEnabled = preferences[ACCESSIBILITY_MODE_KEY] ?: false,
                preferredLanguage = preferences[PREFERRED_LANGUAGE_KEY] ?: "fr",
                autoTranslateEnabled = preferences[AUTO_TRANSLATE_KEY] ?: true,
                speechRate = preferences[SPEECH_RATE_KEY] ?: 1.0f
            )
        }
    
    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }
    
    suspend fun updateTextSize(textSize: TextSize) {
        context.dataStore.edit { preferences ->
            preferences[TEXT_SIZE_KEY] = textSize.name
        }
    }
    
    suspend fun updateAccessibilityMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ACCESSIBILITY_MODE_KEY] = isEnabled
        }
    }
    
    suspend fun updatePreferredLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PREFERRED_LANGUAGE_KEY] = language
        }
    }
    
    suspend fun updateAutoTranslate(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_TRANSLATE_KEY] = enabled
        }
    }
    
    suspend fun updateSpeechRate(rate: Float) {
        context.dataStore.edit { preferences ->
            preferences[SPEECH_RATE_KEY] = rate
        }
    }
}

