package com.android.tripbook.data.managers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager for handling user's favorite trips
 * Uses DataStore for persistent storage of favorites
 */
class FavoritesManager(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val FAVORITES_KEY = stringSetPreferencesKey("user_favorites")
        
        // Singleton instance for non-DI usage
        @Volatile
        private var INSTANCE: FavoritesManager? = null
        
        fun getInstance(dataStore: DataStore<Preferences>? = null): FavoritesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesManager(
                    dataStore ?: throw IllegalArgumentException("DataStore must be provided for first initialization")
                ).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Flow of all favorite trip IDs
     */
    val favoriteTrips: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[FAVORITES_KEY] ?: emptySet()
    }
    
    /**
     * Check if a trip is favorited
     */
    fun isFavorite(tripId: String): Flow<Boolean> = favoriteTrips.map { favorites ->
        tripId in favorites
    }
    
    /**
     * Toggle favorite status of a trip
     */
    suspend fun toggleFavorite(tripId: String): Boolean {
        var isFavorited = false
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            
            if (tripId in currentFavorites) {
                currentFavorites.remove(tripId)
                isFavorited = false
            } else {
                currentFavorites.add(tripId)
                isFavorited = true
            }
            
            preferences[FAVORITES_KEY] = currentFavorites
        }
        return isFavorited
    }
    
    /**
     * Add a trip to favorites
     */
    suspend fun addToFavorites(tripId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.add(tripId)
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }
    
    /**
     * Remove a trip from favorites
     */
    suspend fun removeFromFavorites(tripId: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.remove(tripId)
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }
    
    /**
     * Clear all favorites
     */
    suspend fun clearAllFavorites() {
        dataStore.edit { preferences ->
            preferences.remove(FAVORITES_KEY)
        }
    }
    
    /**
     * Get count of favorite trips
     */
    val favoritesCount: Flow<Int> = favoriteTrips.map { it.size }
}
