package com.android.tripbook.datamining.data.realtime

import com.android.tripbook.datamining.data.algorithms.RecommendationEngine
import com.android.tripbook.datamining.data.database.dao.DestinationDao
import com.android.tripbook.datamining.data.database.dao.UserPreferenceDao
import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.UserPreference
import com.android.tripbook.datamining.data.model.TravelRecommendation
import com.android.tripbook.datamining.data.model.TrendingDestination
import com.android.tripbook.datamining.data.model.TrendingTopic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

/**
 * Dynamically updates recommendations based on real-time user behavior
 */
class DynamicRecommender(
    private val userInteractionTracker: UserInteractionTracker,
    private val trendAnalyzer: TrendAnalyzer,
    private val userPreferenceDao: UserPreferenceDao,
    private val destinationDao: DestinationDao,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // Cache of user preferences
    private val userPreferencesCache = ConcurrentHashMap<String, List<UserPreference>>()
    
    // Cache of all destinations
    private var destinationsCache = listOf<Destination>()
    
    // Dynamic recommendations for each user
    private val userRecommendations = ConcurrentHashMap<String, MutableStateFlow<List<TravelRecommendation>>>()
    
    // Last update timestamps
    private val lastUpdateTimes = ConcurrentHashMap<String, Long>()
    
    // Update interval (5 minutes)
    private val updateInterval = 5 * 60 * 1000
    
    init {
        // Initialize destinations cache
        coroutineScope.launch {
            destinationsCache = destinationDao.getAllDestinations()
        }
        
        // Listen for trending destinations updates
        coroutineScope.launch {
            trendAnalyzer.trendingDestinations.collectLatest { trendingDestinations ->
                if (trendingDestinations.isNotEmpty()) {
                    updateRecommendationsWithTrends(trendingDestinations)
                }
            }
        }
        
        // Listen for trending topics updates
        coroutineScope.launch {
            trendAnalyzer.trendingTopics.collectLatest { trendingTopics ->
                if (trendingTopics.isNotEmpty()) {
                    updateRecommendationsWithTopics(trendingTopics)
                }
            }
        }
        
        // Listen for user interactions
        coroutineScope.launch {
            userInteractionTracker.interactionEvents.collect { interaction ->
                // Check if it's time to update recommendations for this user
                val userId = interaction.userId
                val currentTime = System.currentTimeMillis()
                val lastUpdate = lastUpdateTimes[userId] ?: 0L
                
                if (currentTime - lastUpdate > updateInterval) {
                    updateRecommendationsForUser(userId)
                    lastUpdateTimes[userId] = currentTime
                }
            }
        }
    }
    
    /**
     * Gets recommendations for a specific user
     */
    fun getRecommendationsForUser(userId: String): StateFlow<List<TravelRecommendation>> {
        // Create flow if it doesn't exist
        val recommendationsFlow = userRecommendations.getOrPut(userId) {
            MutableStateFlow(emptyList())
        }
        
        // Trigger update if needed
        coroutineScope.launch {
            val currentTime = System.currentTimeMillis()
            val lastUpdate = lastUpdateTimes[userId] ?: 0L
            
            if (currentTime - lastUpdate > updateInterval) {
                updateRecommendationsForUser(userId)
                lastUpdateTimes[userId] = currentTime
            }
        }
        
        return recommendationsFlow.asStateFlow()
    }
    
    /**
     * Updates recommendations for a specific user
     */
    private suspend fun updateRecommendationsForUser(userId: String) {
        try {
            // Refresh user preferences cache
            refreshUserPreferencesCache(userId)
            
            // Get all user preferences
            val allUserPreferences = userPreferenceDao.getAllUserPreferences()
                .groupBy { it.userId }
            
            // Generate recommendations using hybrid approach
            val hybridRecs = RecommendationEngine.hybridRecommendations(
                userId = userId,
                userPreferences = allUserPreferences,
                destinations = destinationsCache,
                topK = 10
            )
            
            // Get trending recommendations
            val trendingRecs = generateTrendingRecommendations(userId, 5)
            
            // Combine recommendations, removing duplicates
            val allDestIds = (hybridRecs.map { it.destinationId } + trendingRecs.map { it.destinationId })
                .filterNotNull()
                .toSet()
            
            val combinedRecs = mutableListOf<TravelRecommendation>()
            
            // Add hybrid recommendations first (higher priority)
            combinedRecs.addAll(hybridRecs)
            
            // Add trending recommendations that aren't duplicates
            trendingRecs.forEach { trendingRec ->
                if (trendingRec.destinationId !in hybridRecs.mapNotNull { it.destinationId }) {
                    combinedRecs.add(trendingRec)
                }
            }
            
            // Update recommendations flow
            userRecommendations[userId]?.value = combinedRecs
                .sortedByDescending { it.relevanceScore }
                .take(15) // Limit to top 15
            
        } catch (e: Exception) {
            // Log error
            e.printStackTrace()
        }
    }
    
    /**
     * Updates recommendations based on trending destinations
     */
    private fun updateRecommendationsWithTrends(trendingDestinations: List<TrendingDestination>) {
        // Update all users' recommendations with trending data
        userRecommendations.keys.forEach { userId ->
            coroutineScope.launch {
                val currentRecs = userRecommendations[userId]?.value ?: emptyList()
                
                // Generate trending recommendations
                val trendingRecs = generateTrendingRecommendations(userId, 5)
                
                // Merge with existing recommendations, prioritizing personalized ones
                val existingDestIds = currentRecs.mapNotNull { it.destinationId }.toSet()
                val newRecs = currentRecs.toMutableList()
                
                trendingRecs.forEach { trendingRec ->
                    if (trendingRec.destinationId !in existingDestIds) {
                        newRecs.add(trendingRec)
                    }
                }
                
                // Update recommendations flow
                userRecommendations[userId]?.value = newRecs
                    .sortedByDescending { it.relevanceScore }
                    .take(15) // Limit to top 15
            }
        }
    }
    
    /**
     * Updates recommendations based on trending topics
     */
    private fun updateRecommendationsWithTopics(trendingTopics: List<TrendingTopic>) {
        // For each user, find destinations that match trending topics
        userRecommendations.keys.forEach { userId ->
            coroutineScope.launch {
                try {
                    val userPrefs = userPreferencesCache[userId] ?: return@launch
                    
                    // Find destinations that match trending topics
                    val topicMatchRecs = findDestinationsMatchingTopics(trendingTopics, userPrefs)
                    
                    if (topicMatchRecs.isNotEmpty()) {
                        // Merge with existing recommendations
                        val currentRecs = userRecommendations[userId]?.value ?: emptyList()
                        val existingDestIds = currentRecs.mapNotNull { it.destinationId }.toSet()
                        val newRecs = currentRecs.toMutableList()
                        
                        topicMatchRecs.forEach { topicRec ->
                            if (topicRec.destinationId !in existingDestIds) {
                                newRecs.add(topicRec)
                            }
                        }
                        
                        // Update recommendations flow
                        userRecommendations[userId]?.value = newRecs
                            .sortedByDescending { it.relevanceScore }
                            .take(15) // Limit to top 15
                    }
                } catch (e: Exception) {
                    // Log error
                    e.printStackTrace()
                }
            }
        }
    }
    
    /**
     * Generates recommendations based on trending destinations
     */
    private fun generateTrendingRecommendations(
        userId: String,
        count: Int
    ): List<TravelRecommendation> {
        val trendingDests = trendAnalyzer.trendingDestinations.value
        if (trendingDests.isEmpty()) return emptyList()
        
        // Get user preferences to personalize trending recommendations
        val userPrefs = userPreferencesCache[userId] ?: emptyList()
        
        // Convert trending destinations to recommendations
        return trendingDests
            .take(count * 2) // Take more than needed for filtering
            .mapNotNull { trending ->
                val destination = destinationsCache.find { it.id == trending.id } ?: return@mapNotNull null
                
                // Calculate relevance based on user preferences
                val relevance = calculateRelevanceForUser(destination, userPrefs, trending.trendingScore)
                
                TravelRecommendation(
                    id = trending.id + 1000, // Avoid ID conflicts
                    title = "Trending: ${trending.name}",
                    description = "This destination is currently popular among travelers",
                    destinationId = trending.id,
                    destinationName = trending.name,
                    imageUrl = trending.imageUrl,
                    confidence = (trending.trendingScore.toFloat() / 100).coerceIn(0.7f, 0.95f),
                    tags = destination.tags?.split(",")?.map { it.trim() } ?: emptyList(),
                    relevanceScore = relevance,
                    region = trending.region,
                    budgetCategory = getBudgetCategory(destination),
                    travelStyle = getTravelStyle(destination),
                    isPredictive = true
                )
            }
            .sortedByDescending { it.relevanceScore }
            .take(count)
    }
    
    /**
     * Finds destinations that match trending topics
     */
    private fun findDestinationsMatchingTopics(
        trendingTopics: List<TrendingTopic>,
        userPreferences: List<UserPreference>
    ): List<TravelRecommendation> {
        val matchingDestinations = mutableListOf<Pair<Destination, Float>>()
        
        // Extract top trending topics
        val topTopics = trendingTopics.take(10)
        
        // Find destinations that match these topics
        destinationsCache.forEach { destination ->
            var matchScore = 0f
            
            // Check for region matches
            val regionTopics = topTopics.filter { it.type == "region" }
            if (regionTopics.any { it.value == destination.region }) {
                matchScore += 0.5f
            }
            
            // Check for tag matches
            val destTags = destination.tags?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
            val tagTopics = topTopics.filter { it.type == "tag" || it.type == "search_term" }
            
            tagTopics.forEach { topic ->
                if (destTags.any { it.contains(topic.value.lowercase()) }) {
                    matchScore += 0.3f
                }
            }
            
            // Only include destinations with a significant match
            if (matchScore > 0.3f) {
                // Adjust score based on user preferences
                val finalScore = calculateRelevanceForUser(destination, userPreferences, matchScore.toInt())
                matchingDestinations.add(Pair(destination, finalScore))
            }
        }
        
        // Convert to recommendations
        return matchingDestinations
            .sortedByDescending { it.second }
            .take(5)
            .map { (destination, score) ->
                TravelRecommendation(
                    id = destination.id + 2000, // Avoid ID conflicts
                    title = "Trending Topic Match: ${destination.name}",
                    description = "This matches current trending interests among travelers",
                    destinationId = destination.id,
                    destinationName = destination.name,
                    imageUrl = destination.imageUrl,
                    confidence = score * 0.9f,
                    tags = destination.tags?.split(",")?.map { it.trim() } ?: emptyList(),
                    relevanceScore = score,
                    region = destination.region,
                    budgetCategory = getBudgetCategory(destination),
                    travelStyle = getTravelStyle(destination),
                    isPredictive = true
                )
            }
    }
    
    /**
     * Calculates relevance of a destination for a specific user
     */
    private fun calculateRelevanceForUser(
        destination: Destination,
        userPreferences: List<UserPreference>,
        trendingScore: Int
    ): Float {
        var relevance = 0.5f // Base relevance
        
        // Boost based on trending score (0.1 - 0.3 boost)
        relevance += (trendingScore.toFloat() / 100).coerceIn(0.1f, 0.3f)
        
        // Check for region preference match
        val regionPref = userPreferences.find { 
            it.preferenceType == "region" && it.preferenceValue == destination.region 
        }
        
        if (regionPref != null) {
            relevance += regionPref.preferenceStrength * 0.2f
        }
        
        // Check for tag preference matches
        val destTags = destination.tags?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
        val tagPrefs = userPreferences.filter { it.preferenceType == "tag" }
        
        tagPrefs.forEach { tagPref ->
            if (destTags.any { it.contains(tagPref.preferenceValue.lowercase()) }) {
                relevance += tagPref.preferenceStrength * 0.15f
            }
        }
        
        return relevance.coerceIn(0f, 1f)
    }
    
    /**
     * Refreshes the user preferences cache for a specific user
     */
    private suspend fun refreshUserPreferencesCache(userId: String) {
        val preferences = userPreferenceDao.getUserPreferences(userId)
        userPreferencesCache[userId] = preferences
    }
    
    /**
     * Determines budget category for a destination
     */
    private fun getBudgetCategory(destination: Destination): String {
        // In a real implementation, this would use actual budget data
        return when {
            destination.averageRating > 4.5 -> "Luxury"
            destination.averageRating > 3.5 -> "Mid-range"
            else -> "Budget"
        }
    }
    
    /**
     * Determines travel style for a destination
     */
    private fun getTravelStyle(destination: Destination): String {
        // In a real implementation, this would use actual destination categorization
        val tags = destination.tags?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
        
        return when {
            tags.any { it in listOf("beach", "island", "coast", "ocean") } -> "Beach"
            tags.any { it in listOf("wildlife", "safari", "animal") } -> "Safari"
            tags.any { it in listOf("mountain", "hiking", "trek", "adventure") } -> "Adventure"
            tags.any { it in listOf("history", "museum", "heritage", "culture") } -> "Cultural"
            tags.any { it in listOf("nature", "eco", "forest", "conservation") } -> "Eco-tourism"
            else -> "General"
        }
    }
}
