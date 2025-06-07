package com.android.tripbook.datamining.data.algorithms

import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.UserPreference
import com.android.tripbook.datamining.data.model.TravelRecommendation
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Advanced recommendation engine using machine learning techniques
 */
object RecommendationEngine {

    /**
     * Generates recommendations using collaborative filtering
     * 
     * @param userId User ID to generate recommendations for
     * @param userPreferences Map of user IDs to their preferences
     * @param destinations Available destinations
     * @param topK Number of recommendations to generate
     * @return List of recommended destinations
     */
    fun collaborativeFilteringRecommendations(
        userId: String,
        userPreferences: Map<String, List<UserPreference>>,
        destinations: List<Destination>,
        topK: Int = 5
    ): List<TravelRecommendation> {
        // Find similar users based on preference similarity
        val similarUsers = findSimilarUsers(userId, userPreferences)
        
        // Get destinations that similar users liked but the current user hasn't visited
        val recommendedDestinations = mutableMapOf<Long, Double>()
        
        similarUsers.forEach { (similarUserId, similarity) ->
            // Skip if similarity is too low
            if (similarity <= 0.1) return@forEach
            
            // Get preferences of similar user
            val similarUserPrefs = userPreferences[similarUserId] ?: return@forEach
            
            // Find destinations this similar user liked
            val likedDestinations = similarUserPrefs
                .filter { it.preferenceType == "destination" && it.preferenceStrength > 0.6 }
                .mapNotNull { pref ->
                    // Find destination by name
                    val destName = pref.preferenceValue
                    val destination = destinations.find { it.name == destName }
                    
                    if (destination != null) {
                        // Weight by preference strength and user similarity
                        Pair(destination, pref.preferenceStrength * similarity)
                    } else {
                        null
                    }
                }
            
            // Add to recommendations with weights
            likedDestinations.forEach { (destination, weight) ->
                recommendedDestinations[destination.id] = 
                    recommendedDestinations.getOrDefault(destination.id, 0.0) + weight
            }
        }
        
        // Filter out destinations the user has already visited
        val userPrefs = userPreferences[userId] ?: emptyList()
        val visitedDestinations = userPrefs
            .filter { it.preferenceType == "destination" }
            .mapNotNull { pref -> 
                destinations.find { it.name == pref.preferenceValue }?.id 
            }
            .toSet()
        
        val filteredRecommendations = recommendedDestinations
            .filter { (destId, _) -> destId !in visitedDestinations }
        
        // Convert to TravelRecommendation objects
        return filteredRecommendations
            .toList()
            .sortedByDescending { it.second }
            .take(topK)
            .mapNotNull { (destId, score) ->
                val destination = destinations.find { it.id == destId } ?: return@mapNotNull null
                
                TravelRecommendation(
                    id = destId,
                    title = "You might like ${destination.name}",
                    description = "Users with similar preferences enjoyed this destination",
                    destinationId = destId,
                    destinationName = destination.name,
                    imageUrl = destination.imageUrl ?: "",
                    confidence = (score * 0.5 + 0.5).toFloat().coerceIn(0f, 1f),
                    tags = destination.tags?.split(",")?.map { it.trim() } ?: emptyList(),
                    relevanceScore = score.toFloat().coerceIn(0f, 1f),
                    region = destination.region,
                    budgetCategory = getBudgetCategory(destination),
                    travelStyle = getTravelStyle(destination),
                    isPredictive = true
                )
            }
    }
    
    /**
     * Generates recommendations using content-based filtering
     * 
     * @param userId User ID to generate recommendations for
     * @param userPreferences User preferences
     * @param destinations Available destinations
     * @param topK Number of recommendations to generate
     * @return List of recommended destinations
     */
    fun contentBasedFilteringRecommendations(
        userId: String,
        userPreferences: List<UserPreference>,
        destinations: List<Destination>,
        topK: Int = 5
    ): List<TravelRecommendation> {
        // Extract user preference features
        val preferredTags = userPreferences
            .filter { it.preferenceType == "tag" }
            .associate { it.preferenceValue to it.preferenceStrength }
        
        val preferredRegions = userPreferences
            .filter { it.preferenceType == "region" }
            .associate { it.preferenceValue to it.preferenceStrength }
        
        val preferredActivities = userPreferences
            .filter { it.preferenceType == "activity" }
            .associate { it.preferenceValue to it.preferenceStrength }
        
        // Score destinations based on feature similarity
        val scoredDestinations = destinations.map { destination ->
            // Calculate tag similarity
            val destTags = destination.tags?.split(",")?.map { it.trim() } ?: emptyList()
            val tagScore = destTags.sumOf { tag ->
                preferredTags[tag]?.toDouble() ?: 0.0
            } / max(1, destTags.size)
            
            // Calculate region similarity
            val regionScore = preferredRegions[destination.region]?.toDouble() ?: 0.0
            
            // Calculate overall similarity score
            val overallScore = (tagScore * 0.6 + regionScore * 0.4)
            
            Pair(destination, overallScore)
        }
        
        // Filter out destinations the user has already visited
        val visitedDestinations = userPreferences
            .filter { it.preferenceType == "destination" }
            .map { it.preferenceValue }
            .toSet()
        
        val filteredRecommendations = scoredDestinations
            .filter { (destination, _) -> destination.name !in visitedDestinations }
        
        // Convert to TravelRecommendation objects
        return filteredRecommendations
            .sortedByDescending { it.second }
            .take(topK)
            .map { (destination, score) ->
                TravelRecommendation(
                    id = destination.id,
                    title = "Based on your interests: ${destination.name}",
                    description = "This matches your preferences for ${getMatchingPreferences(destination, preferredTags, preferredRegions)}",
                    destinationId = destination.id,
                    destinationName = destination.name,
                    imageUrl = destination.imageUrl ?: "",
                    confidence = (score * 0.5 + 0.5).toFloat().coerceIn(0f, 1f),
                    tags = destination.tags?.split(",")?.map { it.trim() } ?: emptyList(),
                    relevanceScore = score.toFloat().coerceIn(0f, 1f),
                    region = destination.region,
                    budgetCategory = getBudgetCategory(destination),
                    travelStyle = getTravelStyle(destination),
                    isPredictive = true
                )
            }
    }
    
    /**
     * Generates recommendations using a hybrid approach combining collaborative and content-based filtering
     */
    fun hybridRecommendations(
        userId: String,
        userPreferences: Map<String, List<UserPreference>>,
        destinations: List<Destination>,
        topK: Int = 5
    ): List<TravelRecommendation> {
        // Get recommendations from both methods
        val collaborativeRecs = collaborativeFilteringRecommendations(
            userId, userPreferences, destinations, topK * 2
        )
        
        val contentBasedRecs = contentBasedFilteringRecommendations(
            userId, userPreferences[userId] ?: emptyList(), destinations, topK * 2
        )
        
        // Combine and re-rank recommendations
        val allRecs = (collaborativeRecs + contentBasedRecs)
            .groupBy { it.destinationId }
            .map { (_, recs) ->
                // For destinations recommended by both methods, boost the score
                val rec = recs.first()
                val combinedScore = if (recs.size > 1) {
                    // Average the scores with a bonus for being recommended by both methods
                    (recs.sumOf { it.relevanceScore.toDouble() } / recs.size) * 1.2
                } else {
                    recs.first().relevanceScore.toDouble()
                }
                
                rec.copy(relevanceScore = combinedScore.toFloat().coerceIn(0f, 1f))
            }
            .sortedByDescending { it.relevanceScore }
            .take(topK)
        
        return allRecs
    }
    
    /**
     * Finds users with similar preferences
     */
    private fun findSimilarUsers(
        userId: String,
        userPreferences: Map<String, List<UserPreference>>
    ): Map<String, Double> {
        val currentUserPrefs = userPreferences[userId] ?: return emptyMap()
        
        // Convert preferences to feature vectors
        val currentUserVector = preferencesToVector(currentUserPrefs)
        
        // Calculate similarity with other users
        return userPreferences
            .filter { it.key != userId }
            .mapValues { (_, prefs) ->
                val otherUserVector = preferencesToVector(prefs)
                cosineSimilarity(currentUserVector, otherUserVector)
            }
            .filter { it.value > 0 } // Only keep users with positive similarity
    }
    
    /**
     * Converts user preferences to a feature vector
     */
    private fun preferencesToVector(preferences: List<UserPreference>): Map<String, Double> {
        return preferences.associate { 
            "${it.preferenceType}:${it.preferenceValue}" to it.preferenceStrength.toDouble()
        }
    }
    
    /**
     * Calculates cosine similarity between two feature vectors
     */
    private fun cosineSimilarity(vec1: Map<String, Double>, vec2: Map<String, Double>): Double {
        // Find common features
        val commonFeatures = vec1.keys.intersect(vec2.keys)
        
        if (commonFeatures.isEmpty()) return 0.0
        
        // Calculate dot product
        val dotProduct = commonFeatures.sumOf { feature ->
            vec1[feature]!! * vec2[feature]!!
        }
        
        // Calculate magnitudes
        val mag1 = sqrt(vec1.values.sumOf { it.pow(2) })
        val mag2 = sqrt(vec2.values.sumOf { it.pow(2) })
        
        return if (mag1 > 0 && mag2 > 0) {
            dotProduct / (mag1 * mag2)
        } else {
            0.0
        }
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
    
    /**
     * Gets a description of matching preferences for content-based recommendations
     */
    private fun getMatchingPreferences(
        destination: Destination,
        preferredTags: Map<String, Float>,
        preferredRegions: Map<String, Float>
    ): String {
        val matches = mutableListOf<String>()
        
        // Check for matching region
        if (preferredRegions.containsKey(destination.region)) {
            matches.add(destination.region)
        }
        
        // Check for matching tags
        val destTags = destination.tags?.split(",")?.map { it.trim() } ?: emptyList()
        val matchingTags = destTags.filter { preferredTags.containsKey(it) }
        
        if (matchingTags.isNotEmpty()) {
            matches.addAll(matchingTags)
        }
        
        return if (matches.isNotEmpty()) {
            matches.joinToString(", ")
        } else {
            "similar destinations"
        }
    }
}
