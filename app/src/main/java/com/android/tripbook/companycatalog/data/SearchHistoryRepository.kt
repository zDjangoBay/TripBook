package com.android.tripbook.companycatalog.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

class SearchHistoryRepository(private val context: Context) {
    private val MAX_HISTORY_ITEMS = 10

    // Flow of search history items
    val searchHistory: Flow<List<SearchHistoryItem>> = context.dataStore.data.map { preferences ->
        preferences.asMap().entries
            .filter { it.key.name.startsWith("search_") }
            .map { entry ->
                val parts = entry.value.toString().split("|")
                SearchHistoryItem(
                    query = parts[0],
                    timestamp = Date(parts[1].toLong()),
                    resultCount = parts[2].toInt()
                )
            }
            .sortedByDescending { it.timestamp }
            .take(MAX_HISTORY_ITEMS)
    }

    // Add a new search query to history
    suspend fun addSearchQuery(query: String, resultCount: Int) {
        context.dataStore.edit { preferences ->
            val timestamp = System.currentTimeMillis()
            val key = stringPreferencesKey("search_$timestamp")
            preferences[key] = "$query|$timestamp|$resultCount"
        }
    }

    // Clear all search history
    suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences.asMap().keys
                .filter { it.name.startsWith("search_") }
                .forEach { preferences.remove(it) }
        }
    }

    // Remove a specific search query from history
    suspend fun removeSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences.asMap().entries
                .filter { it.key.name.startsWith("search_") && it.value.toString().startsWith("$query|") }
                .forEach { preferences.remove(it.key) }
        }
    }
} 