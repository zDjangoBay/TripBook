package com.TripBook.postmodule

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manages caching for post-related data to improve performance and offline capabilities.
 * Handles caching of locations, tags, categories, and user preferences.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostCacheManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "post_cache", Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    companion object {
        private const val RECENT_LOCATIONS_KEY = "recent_locations"
        private const val POPULAR_TAGS_KEY = "popular_tags"
        private const val USER_PREFERENCES_KEY = "user_preferences"
        private const val CATEGORY_USAGE_KEY = "category_usage"
        private const val LOCATION_SUGGESTIONS_KEY = "location_suggestions"
        private const val TAG_SUGGESTIONS_KEY = "tag_suggestions"
        private const val CACHE_TIMESTAMP_SUFFIX = "_timestamp"
        private const val DEFAULT_CACHE_DURATION = 24 * 60 * 60 * 1000L // 24 hours
    }
    
    /**
     * Caches recent locations used by the user
     */
    suspend fun cacheRecentLocation(location: PostLocationData) = withContext(Dispatchers.IO) {
        val recentLocations = getRecentLocations().toMutableList()
        
        // Remove if already exists to avoid duplicates
        recentLocations.removeAll { it.latitude == location.latitude && it.longitude == location.longitude }
        
        // Add to beginning
        recentLocations.add(0, location)
        
        // Keep only last 50 locations
        if (recentLocations.size > PostConstants.CollectionLimits.MAX_RECENT_LOCATIONS) {
            recentLocations.removeAt(recentLocations.size - 1)
        }
        
        val cacheData = LocationCache(recentLocations, System.currentTimeMillis())
        val cacheJson = json.encodeToString(cacheData)
        
        prefs.edit()
            .putString(RECENT_LOCATIONS_KEY, cacheJson)
            .putLong("$RECENT_LOCATIONS_KEY$CACHE_TIMESTAMP_SUFFIX", System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Gets cached recent locations
     */
    suspend fun getRecentLocations(): List<PostLocationData> = withContext(Dispatchers.IO) {
        val cacheJson = prefs.getString(RECENT_LOCATIONS_KEY, null)
        val timestamp = prefs.getLong("$RECENT_LOCATIONS_KEY$CACHE_TIMESTAMP_SUFFIX", 0)
        
        if (cacheJson != null && !isCacheExpired(timestamp)) {
            try {
                val cache = json.decodeFromString<LocationCache>(cacheJson)
                cache.locations
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    /**
     * Caches popular tags
     */
    suspend fun cachePopularTags(tags: List<String>) = withContext(Dispatchers.IO) {
        val cacheData = TagCache(tags, System.currentTimeMillis())
        val cacheJson = json.encodeToString(cacheData)
        
        prefs.edit()
            .putString(POPULAR_TAGS_KEY, cacheJson)
            .putLong("$POPULAR_TAGS_KEY$CACHE_TIMESTAMP_SUFFIX", System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Gets cached popular tags
     */
    suspend fun getPopularTags(): List<String> = withContext(Dispatchers.IO) {
        val cacheJson = prefs.getString(POPULAR_TAGS_KEY, null)
        val timestamp = prefs.getLong("$POPULAR_TAGS_KEY$CACHE_TIMESTAMP_SUFFIX", 0)
        
        if (cacheJson != null && !isCacheExpired(timestamp)) {
            try {
                val cache = json.decodeFromString<TagCache>(cacheJson)
                cache.tags
            } catch (e: Exception) {
                PostConstants.PopularTags.ACTIVITY_TAGS
            }
        } else {
            PostConstants.PopularTags.ACTIVITY_TAGS
        }
    }
    
    /**
     * Caches user preferences
     */
    suspend fun cacheUserPreferences(preferences: UserPreferences) = withContext(Dispatchers.IO) {
        val preferencesJson = json.encodeToString(preferences)
        
        prefs.edit()
            .putString(USER_PREFERENCES_KEY, preferencesJson)
            .putLong("$USER_PREFERENCES_KEY$CACHE_TIMESTAMP_SUFFIX", System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Gets cached user preferences
     */
    suspend fun getUserPreferences(): UserPreferences = withContext(Dispatchers.IO) {
        val preferencesJson = prefs.getString(USER_PREFERENCES_KEY, null)
        
        preferencesJson?.let {
            try {
                json.decodeFromString<UserPreferences>(it)
            } catch (e: Exception) {
                UserPreferences()
            }
        } ?: UserPreferences()
    }
    
    /**
     * Updates category usage statistics
     */
    suspend fun updateCategoryUsage(category: String) = withContext(Dispatchers.IO) {
        val usageJson = prefs.getString(CATEGORY_USAGE_KEY, null)
        val usage = usageJson?.let {
            try {
                json.decodeFromString<CategoryUsage>(it)
            } catch (e: Exception) {
                CategoryUsage()
            }
        } ?: CategoryUsage()
        
        val updatedCounts = usage.counts.toMutableMap()
        updatedCounts[category] = updatedCounts.getOrDefault(category, 0) + 1
        
        val updatedUsage = usage.copy(
            counts = updatedCounts,
            lastUpdated = System.currentTimeMillis()
        )
        
        val updatedJson = json.encodeToString(updatedUsage)
        prefs.edit().putString(CATEGORY_USAGE_KEY, updatedJson).apply()
    }
    
    /**
     * Gets most used categories
     */
    suspend fun getMostUsedCategories(limit: Int = 5): List<String> = withContext(Dispatchers.IO) {
        val usageJson = prefs.getString(CATEGORY_USAGE_KEY, null)
        val usage = usageJson?.let {
            try {
                json.decodeFromString<CategoryUsage>(it)
            } catch (e: Exception) {
                CategoryUsage()
            }
        } ?: CategoryUsage()
        
        usage.counts.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }
    
    /**
     * Caches location suggestions
     */
    suspend fun cacheLocationSuggestions(query: String, suggestions: List<PostLocationData>) = withContext(Dispatchers.IO) {
        val cacheKey = "$LOCATION_SUGGESTIONS_KEY:${query.lowercase()}"
        val cacheData = LocationSuggestionCache(suggestions, System.currentTimeMillis())
        val cacheJson = json.encodeToString(cacheData)
        
        prefs.edit()
            .putString(cacheKey, cacheJson)
            .apply()
    }
    
    /**
     * Gets cached location suggestions
     */
    suspend fun getLocationSuggestions(query: String): List<PostLocationData>? = withContext(Dispatchers.IO) {
        val cacheKey = "$LOCATION_SUGGESTIONS_KEY:${query.lowercase()}"
        val cacheJson = prefs.getString(cacheKey, null)
        
        if (cacheJson != null) {
            try {
                val cache = json.decodeFromString<LocationSuggestionCache>(cacheJson)
                if (!isCacheExpired(cache.timestamp, 60 * 60 * 1000L)) { // 1 hour cache
                    cache.suggestions
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    /**
     * Caches tag suggestions
     */
    suspend fun cacheTagSuggestions(query: String, suggestions: List<String>) = withContext(Dispatchers.IO) {
        val cacheKey = "$TAG_SUGGESTIONS_KEY:${query.lowercase()}"
        val cacheData = TagSuggestionCache(suggestions, System.currentTimeMillis())
        val cacheJson = json.encodeToString(cacheData)
        
        prefs.edit()
            .putString(cacheKey, cacheJson)
            .apply()
    }
    
    /**
     * Gets cached tag suggestions
     */
    suspend fun getTagSuggestions(query: String): List<String>? = withContext(Dispatchers.IO) {
        val cacheKey = "$TAG_SUGGESTIONS_KEY:${query.lowercase()}"
        val cacheJson = prefs.getString(cacheKey, null)
        
        if (cacheJson != null) {
            try {
                val cache = json.decodeFromString<TagSuggestionCache>(cacheJson)
                if (!isCacheExpired(cache.timestamp, 60 * 60 * 1000L)) { // 1 hour cache
                    cache.suggestions
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    /**
     * Clears all cached data
     */
    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        prefs.edit().clear().apply()
    }
    
    /**
     * Clears expired cache entries
     */
    suspend fun clearExpiredCache() = withContext(Dispatchers.IO) {
        val editor = prefs.edit()
        val currentTime = System.currentTimeMillis()
        
        prefs.all.forEach { (key, _) ->
            if (key.endsWith(CACHE_TIMESTAMP_SUFFIX)) {
                val timestamp = prefs.getLong(key, 0)
                if (isCacheExpired(timestamp)) {
                    val dataKey = key.removeSuffix(CACHE_TIMESTAMP_SUFFIX)
                    editor.remove(key)
                    editor.remove(dataKey)
                }
            }
        }
        
        editor.apply()
    }
    
    /**
     * Gets cache statistics
     */
    suspend fun getCacheStats(): CacheStats = withContext(Dispatchers.IO) {
        val totalEntries = prefs.all.size
        val expiredEntries = prefs.all.count { (key, _) ->
            if (key.endsWith(CACHE_TIMESTAMP_SUFFIX)) {
                val timestamp = prefs.getLong(key, 0)
                isCacheExpired(timestamp)
            } else false
        }
        
        CacheStats(
            totalEntries = totalEntries,
            expiredEntries = expiredEntries,
            validEntries = totalEntries - expiredEntries
        )
    }
    
    private fun isCacheExpired(timestamp: Long, duration: Long = DEFAULT_CACHE_DURATION): Boolean {
        return System.currentTimeMillis() - timestamp > duration
    }
}

@Serializable
data class LocationCache(
    val locations: List<PostLocationData>,
    val timestamp: Long
)

@Serializable
data class TagCache(
    val tags: List<String>,
    val timestamp: Long
)

@Serializable
data class UserPreferences(
    val defaultCategory: String = PostConstants.Defaults.DEFAULT_CATEGORY,
    val defaultVisibility: String = PostConstants.Defaults.DEFAULT_VISIBILITY,
    val autoSaveEnabled: Boolean = PostConstants.Defaults.DEFAULT_AUTO_SAVE_ENABLED,
    val locationEnabled: Boolean = PostConstants.Defaults.DEFAULT_LOCATION_ENABLED,
    val imageQuality: Int = PostConstants.Defaults.DEFAULT_IMAGE_QUALITY,
    val favoriteCategories: List<String> = emptyList(),
    val frequentTags: List<String> = emptyList()
)

@Serializable
data class CategoryUsage(
    val counts: Map<String, Int> = emptyMap(),
    val lastUpdated: Long = System.currentTimeMillis()
)

@Serializable
data class LocationSuggestionCache(
    val suggestions: List<PostLocationData>,
    val timestamp: Long
)

@Serializable
data class TagSuggestionCache(
    val suggestions: List<String>,
    val timestamp: Long
)

data class CacheStats(
    val totalEntries: Int,
    val expiredEntries: Int,
    val validEntries: Int
)
