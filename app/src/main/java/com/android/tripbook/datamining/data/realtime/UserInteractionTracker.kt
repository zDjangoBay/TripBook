package com.android.tripbook.datamining.data.realtime

import com.android.tripbook.datamining.data.database.dao.UserPreferenceDao
import com.android.tripbook.datamining.data.database.entities.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks and processes user interactions in real-time
 */
class UserInteractionTracker(
    private val userPreferenceDao: UserPreferenceDao,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // Event types
    enum class InteractionType {
        VIEW,
        CLICK,
        BOOKMARK,
        RATE,
        SEARCH,
        FILTER,
        SHARE
    }
    
    // Interaction event data class
    data class UserInteraction(
        val userId: String,
        val timestamp: Long = System.currentTimeMillis(),
        val type: InteractionType,
        val targetId: String? = null,
        val targetType: String? = null,
        val value: Float? = null,
        val metadata: Map<String, String> = emptyMap()
    )
    
    // Recent interactions cache
    private val recentInteractions = ConcurrentHashMap<String, MutableList<UserInteraction>>()
    
    // Interaction event flow for real-time processing
    private val _interactionEvents = MutableSharedFlow<UserInteraction>()
    val interactionEvents: SharedFlow<UserInteraction> = _interactionEvents.asSharedFlow()
    
    // Interaction count thresholds for preference inference
    private val viewThreshold = 3
    private val clickThreshold = 2
    private val bookmarkThreshold = 1
    
    init {
        // Set up event processing
        coroutineScope.launch {
            interactionEvents.collect { interaction ->
                processInteraction(interaction)
            }
        }
    }
    
    /**
     * Tracks a user interaction event
     */
    suspend fun trackInteraction(interaction: UserInteraction) {
        // Add to recent interactions cache
        recentInteractions.getOrPut(interaction.userId) { mutableListOf() }
            .add(interaction)
        
        // Trim cache if it gets too large
        val userInteractions = recentInteractions[interaction.userId]
        if (userInteractions != null && userInteractions.size > 100) {
            val sortedInteractions = userInteractions.sortedByDescending { it.timestamp }
            recentInteractions[interaction.userId] = sortedInteractions.take(50).toMutableList()
        }
        
        // Emit event for real-time processing
        _interactionEvents.emit(interaction)
    }
    
    /**
     * Processes an interaction event
     */
    private suspend fun processInteraction(interaction: UserInteraction) {
        when (interaction.type) {
            InteractionType.VIEW -> processViewInteraction(interaction)
            InteractionType.CLICK -> processClickInteraction(interaction)
            InteractionType.BOOKMARK -> processBookmarkInteraction(interaction)
            InteractionType.RATE -> processRatingInteraction(interaction)
            InteractionType.SEARCH -> processSearchInteraction(interaction)
            InteractionType.FILTER -> processFilterInteraction(interaction)
            InteractionType.SHARE -> processShareInteraction(interaction)
        }
    }
    
    /**
     * Processes a view interaction
     */
    private suspend fun processViewInteraction(interaction: UserInteraction) {
        // Check if user has viewed this target multiple times
        val viewCount = countRecentInteractions(
            interaction.userId,
            InteractionType.VIEW,
            interaction.targetId,
            interaction.targetType
        )
        
        if (viewCount >= viewThreshold) {
            // Infer a weak preference
            inferPreference(
                userId = interaction.userId,
                preferenceType = interaction.targetType ?: "destination",
                preferenceValue = interaction.targetId ?: return,
                preferenceStrength = 0.3f,
                source = "implicit_view"
            )
        }
    }
    
    /**
     * Processes a click interaction
     */
    private suspend fun processClickInteraction(interaction: UserInteraction) {
        // Check if user has clicked this target multiple times
        val clickCount = countRecentInteractions(
            interaction.userId,
            InteractionType.CLICK,
            interaction.targetId,
            interaction.targetType
        )
        
        if (clickCount >= clickThreshold) {
            // Infer a medium preference
            inferPreference(
                userId = interaction.userId,
                preferenceType = interaction.targetType ?: "destination",
                preferenceValue = interaction.targetId ?: return,
                preferenceStrength = 0.5f,
                source = "implicit_click"
            )
        }
    }
    
    /**
     * Processes a bookmark interaction
     */
    private suspend fun processBookmarkInteraction(interaction: UserInteraction) {
        // Bookmarking indicates a strong preference
        inferPreference(
            userId = interaction.userId,
            preferenceType = interaction.targetType ?: "destination",
            preferenceValue = interaction.targetId ?: return,
            preferenceStrength = 0.8f,
            source = "implicit_bookmark"
        )
    }
    
    /**
     * Processes a rating interaction
     */
    private suspend fun processRatingInteraction(interaction: UserInteraction) {
        val rating = interaction.value ?: return
        
        // Convert rating (assumed to be 1-5) to preference strength (0-1)
        val preferenceStrength = (rating / 5.0f).coerceIn(0.0f, 1.0f)
        
        inferPreference(
            userId = interaction.userId,
            preferenceType = interaction.targetType ?: "destination",
            preferenceValue = interaction.targetId ?: return,
            preferenceStrength = preferenceStrength,
            source = "explicit_rating"
        )
    }
    
    /**
     * Processes a search interaction
     */
    private suspend fun processSearchInteraction(interaction: UserInteraction) {
        // Extract search terms from metadata
        val searchQuery = interaction.metadata["query"] ?: return
        
        // Infer preferences from search terms
        val searchTerms = searchQuery.split(" ", ",", ";")
            .map { it.trim().lowercase() }
            .filter { it.length > 3 } // Filter out short terms
        
        searchTerms.forEach { term ->
            inferPreference(
                userId = interaction.userId,
                preferenceType = "search_term",
                preferenceValue = term,
                preferenceStrength = 0.4f,
                source = "implicit_search"
            )
        }
    }
    
    /**
     * Processes a filter interaction
     */
    private suspend fun processFilterInteraction(interaction: UserInteraction) {
        // Extract filter type and value from metadata
        val filterType = interaction.metadata["filter_type"] ?: return
        val filterValue = interaction.metadata["filter_value"] ?: return
        
        inferPreference(
            userId = interaction.userId,
            preferenceType = filterType,
            preferenceValue = filterValue,
            preferenceStrength = 0.6f,
            source = "implicit_filter"
        )
    }
    
    /**
     * Processes a share interaction
     */
    private suspend fun processShareInteraction(interaction: UserInteraction) {
        // Sharing indicates a strong preference
        inferPreference(
            userId = interaction.userId,
            preferenceType = interaction.targetType ?: "destination",
            preferenceValue = interaction.targetId ?: return,
            preferenceStrength = 0.9f,
            source = "implicit_share"
        )
    }
    
    /**
     * Counts recent interactions of a specific type with a specific target
     */
    private fun countRecentInteractions(
        userId: String,
        type: InteractionType,
        targetId: String?,
        targetType: String?
    ): Int {
        val userInteractions = recentInteractions[userId] ?: return 0
        
        // Count interactions in the last 7 days
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        
        return userInteractions.count { interaction ->
            interaction.type == type &&
            interaction.targetId == targetId &&
            interaction.targetType == targetType &&
            interaction.timestamp >= oneWeekAgo
        }
    }
    
    /**
     * Infers a user preference from interactions and stores it
     */
    private suspend fun inferPreference(
        userId: String,
        preferenceType: String,
        preferenceValue: String,
        preferenceStrength: Float,
        source: String
    ) {
        // Check if preference already exists
        val existingPreferences = userPreferenceDao.getUserPreferencesByTypeAndValue(
            userId, preferenceType, preferenceValue
        )
        
        if (existingPreferences.isNotEmpty()) {
            // Update existing preference
            val existingPref = existingPreferences.first()
            
            // Only update if new strength is higher or source is more reliable
            if (preferenceStrength > existingPref.preferenceStrength ||
                (source.startsWith("explicit") && existingPref.source.startsWith("implicit"))) {
                
                userPreferenceDao.update(
                    existingPref.copy(
                        preferenceStrength = preferenceStrength.coerceAtLeast(existingPref.preferenceStrength),
                        lastUpdated = Date(),
                        source = if (source.startsWith("explicit")) source else existingPref.source,
                        confidence = calculateConfidence(source, preferenceStrength)
                    )
                )
            }
        } else {
            // Create new preference
            userPreferenceDao.insert(
                UserPreference(
                    userId = userId,
                    preferenceType = preferenceType,
                    preferenceValue = preferenceValue,
                    preferenceStrength = preferenceStrength,
                    lastUpdated = Date(),
                    source = source,
                    confidence = calculateConfidence(source, preferenceStrength)
                )
            )
        }
    }
    
    /**
     * Calculates confidence in an inferred preference
     */
    private fun calculateConfidence(source: String, strength: Float): Float {
        // Explicit preferences have higher confidence
        val baseConfidence = if (source.startsWith("explicit")) 0.9f else 0.7f
        
        // Stronger preferences have higher confidence
        return (baseConfidence * (0.5f + strength * 0.5f)).coerceIn(0.0f, 1.0f)
    }
}
