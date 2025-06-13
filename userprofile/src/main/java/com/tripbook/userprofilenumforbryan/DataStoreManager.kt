package com.tripbook.userprofilenumforbryan

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.analytics.FirebaseAnalytics

// Extension property to get DataStore instance
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val VISIBILITY_KEY = stringPreferencesKey("visibility_key")
    }

    // Get current visibility as a Flow
    val visibilityFlow: Flow<Visibility> = context.dataStore.data
        .map { prefs ->
            val savedValue = prefs[VISIBILITY_KEY] ?: Visibility.FRIENDS.name
            try {
                Visibility.valueOf(savedValue)
            } catch (e: Exception) {
                Visibility.FRIENDS
            }
        }

    // Save new visibility setting
    suspend fun saveVisibility(newVisibility: Visibility) {
        context.dataStore.edit { prefs ->
            prefs[VISIBILITY_KEY] = newVisibility.name
        }
        // Log to Firebase Analytics for remote persistence/logging
        logVisibilityToFirebase(newVisibility)
    }

    private fun logVisibilityToFirebase(visibility: Visibility) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = android.os.Bundle().apply {
            putString("privacy_visibility", visibility.name)
        }
        firebaseAnalytics.logEvent("privacy_visibility_changed", bundle)
    }
}
