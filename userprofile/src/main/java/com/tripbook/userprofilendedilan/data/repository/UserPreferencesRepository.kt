package com.tripbook.userprofilendedilan.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for the DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    // Flow to observe the onboarding completion state
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] ?: false
        }

    // Function to mark onboarding as completed
    suspend fun completeOnboarding() {
        dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] = true
        }
    }

    companion object {
        private val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
    }
}
