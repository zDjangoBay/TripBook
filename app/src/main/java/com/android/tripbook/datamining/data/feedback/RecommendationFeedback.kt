package com.android.tripbook.datamining.data.feedback

import com.android.tripbook.datamining.data.database.dao.UserPreferenceDao
import com.android.tripbook.datamining.data.database.entities.UserPreference
import com.android.tripbook.datamining.data.model.TravelRecommendation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles user feedback on recommendations and incorporates it into the recommendation system
 */
class RecommendationFeedback(
    private val userPreferenceDao: UserPreferenceDao,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // Feedback types
    enum class FeedbackType {
        LIKE,
        DISLIKE,
        SAVE,
        DISMISS,
        CLICK,
        RATE
    }
    
    // Feedback data class
    data class Feedback(
        val userId: String,
        val recommendationId: Long,
        val destinationId: Long?,
        val type: FeedbackType,
        val rating: Float? = null,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    // Feedback statistics for each recommendation strategy
    private data class StrategyStats(
        var impressions: Int = 0,
        var likes: Int = 0,
        var dislikes: Int = 0,
        var saves: Int = 0,
        var dismisses: Int = 0,
        var clicks: Int = 0,
        var ratings: MutableList<Float> = mutableListOf()
    )
    
    // Recent feedback cache
    private val recentFeedback = ConcurrentHashMap<String, MutableList<Feedback>>()
    
    // Strategy performance tracking
    private val strategyPerformance = ConcurrentHashMap<String, StrategyStats>()
    
    // A/B testing variants
    private val abTestVariants = ConcurrentHashMap<String, String>()
    
    // Feedback metrics
    private val _feedbackMetrics = MutableStateFlow<Map<String, Double>>(emptyMap())
    val feedbackMetrics: StateFlow<Map<String, Double>> = _feedbackMetrics.asStateFlow()
    
    /**
     * Records feedback on a recommendation
     */
    suspend fun recordFeedback(feedback: Feedback) {
        // Add to recent feedback cache
        recentFeedback.getOrPut(feedback.userId) { mutableListOf() }
            .add(feedback)
        
        // Process feedback
        processFeedback(feedback)
        
        // Update metrics
        updateFeedbackMetrics()
    }
    
    /**
     * Records an impression of a recommendation
     */
    fun recordImpression(userId: String, recommendation: TravelRecommendation) {
        // Extract strategy from recommendation
        val strategy = getRecommendationStrategy(recommendation)
        
        // Update strategy stats
        strategyPerformance.getOrPut(strategy) { StrategyStats() }
            .impressions++
        
        // Update metrics
        updateFeedbackMetrics()
    }
    
    /**
     * Assigns a user to an A/B test variant
     */
    fun assignToTestVariant(userId: String, testName: String, variants: List<String>): String {
        // Check if user is already assigned
        val key = "${testName}_${userId}"
        val existingVariant = abTestVariants[key]
        
        if (existingVariant != null) {
            return existingVariant
        }
        
        // Assign randomly
        val variant = variants.random()
        abTestVariants[key] = variant
        
        return variant
    }
    
    /**
     * Gets the current A/B test variant for a user
     */
    fun getTestVariant(userId: String, testName: String): String? {
        return abTestVariants["${testName}_${userId}"]
    }
    
    /**
     * Gets performance metrics for A/B test variants
     */
    fun getTestVariantPerformance(testName: String): Map<String, Map<String, Double>> {
        // Group strategies by variant
        val variantStrategies = strategyPerformance.keys
            .filter { it.startsWith("${testName}_") }
            .groupBy { strategy ->
                strategy.substringAfter("${testName}_").substringBefore("_")
            }
        
        // Calculate metrics for each variant
        return variantStrategies.mapValues { (_, strategies) ->
            val metrics = mutableMapOf<String, Double>()
            
            // Aggregate stats across all strategies in this variant
            var totalImpressions = 0
            var totalLikes = 0
            var totalDislikes = 0
            var totalSaves = 0
            var totalDismisses = 0
            var totalClicks = 0
            val allRatings = mutableListOf<Float>()
            
            strategies.forEach { strategy ->
                val stats = strategyPerformance[strategy] ?: return@forEach
                totalImpressions += stats.impressions
                totalLikes += stats.likes
                totalDislikes += stats.dislikes
                totalSaves += stats.saves
                totalDismisses += stats.dismisses
                totalClicks += stats.clicks
                allRatings.addAll(stats.ratings)
            }
            
            // Calculate metrics
            if (totalImpressions > 0) {
                metrics["ctr"] = totalClicks.toDouble() / totalImpressions
                metrics["like_rate"] = totalLikes.toDouble() / totalImpressions
                metrics["save_rate"] = totalSaves.toDouble() / totalImpressions
                metrics["dismiss_rate"] = totalDismisses.toDouble() / totalImpressions
            }
            
            if (allRatings.isNotEmpty()) {
                metrics["avg_rating"] = allRatings.average()
            }
            
            metrics
        }
    }
    
    /**
     * Processes feedback and updates user preferences
     */
    private suspend fun processFeedback(feedback: Feedback) {
        when (feedback.type) {
            FeedbackType.LIKE -> processLikeFeedback(feedback)
            FeedbackType.DISLIKE -> processDislikeFeedback(feedback)
            FeedbackType.SAVE -> processSaveFeedback(feedback)
            FeedbackType.DISMISS -> processDismissFeedback(feedback)
            FeedbackType.CLICK -> processClickFeedback(feedback)
            FeedbackType.RATE -> processRatingFeedback(feedback)
        }
        
        // Update strategy performance
        updateStrategyPerformance(feedback)
    }
    
    /**
     * Processes "like" feedback
     */
    private suspend fun processLikeFeedback(feedback: Feedback) {
        if (feedback.destinationId == null) return
        
        // Create or update preference for this destination
        updatePreference(
            userId = feedback.userId,
            preferenceType = "destination",
            preferenceValue = feedback.destinationId.toString(),
            preferenceStrength = 0.8f,
            source = "explicit_like"
        )
    }
    
    /**
     * Processes "dislike" feedback
     */
    private suspend fun processDislikeFeedback(feedback: Feedback) {
        if (feedback.destinationId == null) return
        
        // Create or update preference for this destination (negative)
        updatePreference(
            userId = feedback.userId,
            preferenceType = "destination",
            preferenceValue = feedback.destinationId.toString(),
            preferenceStrength = 0.2f,
            source = "explicit_dislike"
        )
    }
    
    /**
     * Processes "save" feedback
     */
    private suspend fun processSaveFeedback(feedback: Feedback) {
        if (feedback.destinationId == null) return
        
        // Create or update preference for this destination
        updatePreference(
            userId = feedback.userId,
            preferenceType = "destination",
            preferenceValue = feedback.destinationId.toString(),
            preferenceStrength = 0.9f,
            source = "explicit_save"
        )
    }
    
    /**
     * Processes "dismiss" feedback
     */
    private suspend fun processDismissFeedback(feedback: Feedback) {
        if (feedback.destinationId == null) return
        
        // Create or update preference for this destination (negative)
        updatePreference(
            userId = feedback.userId,
            preferenceType = "destination",
            preferenceValue = feedback.destinationId.toString(),
            preferenceStrength = 0.1f,
            source = "explicit_dismiss"
        )
    }
    
    /**
     * Processes "click" feedback
     */
    private suspend fun processClickFeedback(feedback: Feedback) {
        if (feedback.destinationId == null) return
        
        // Create or update preference for this destination
        updatePreference(
            userId = feedback.userId,
            preferenceType = "destination",
            preferenceValue = feedback.destinationId.toString(),
            preferenceStrength = 0.6f,
            source = "implicit_click"
        )
    }
    
    /**
     * Processes rating feedback
     */
    private suspend fun processRatingFeedback(feedback: Feedback) {
        if (feedback.destinationId == null || feedback.rating == null) return
        
        // Convert rating (1-5) to preference strength (0-1)
        val preferenceStrength = (feedback.rating / 5.0f).coerceIn(0.0f, 1.0f)
        
        // Create or update preference for this destination
        updatePreference(
            userId = feedback.userId,
            preferenceType = "destination",
            preferenceValue = feedback.destinationId.toString(),
            preferenceStrength = preferenceStrength,
            source = "explicit_rating"
        )
    }
    
    /**
     * Updates a user preference based on feedback
     */
    private suspend fun updatePreference(
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
            
            // Only update if new source is explicit or strength is significantly different
            if (source.startsWith("explicit") || 
                Math.abs(preferenceStrength - existingPref.preferenceStrength) > 0.2f) {
                
                userPreferenceDao.update(
                    existingPref.copy(
                        preferenceStrength = if (source.startsWith("explicit")) {
                            preferenceStrength
                        } else {
                            (existingPref.preferenceStrength * 0.7f + preferenceStrength * 0.3f)
                                .coerceIn(0.0f, 1.0f)
                        },
                        lastUpdated = Date(),
                        source = if (source.startsWith("explicit")) source else existingPref.source,
                        confidence = if (source.startsWith("explicit")) 0.9f else 0.7f
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
                    confidence = if (source.startsWith("explicit")) 0.9f else 0.7f
                )
            )
        }
    }
    
    /**
     * Updates strategy performance metrics based on feedback
     */
    private fun updateStrategyPerformance(feedback: Feedback) {
        coroutineScope.launch {
            try {
                // Get the recommendation that received feedback
                val userId = feedback.userId
                val recId = feedback.recommendationId
                
                // Find the recommendation in recent feedback
                val userFeedback = recentFeedback[userId] ?: return@launch
                val impressionFeedback = userFeedback.find { 
                    it.recommendationId == recId && it.type == FeedbackType.CLICK 
                }
                
                // If we can't determine the strategy, use a default
                val strategy = "unknown"
                
                // Update strategy stats
                val stats = strategyPerformance.getOrPut(strategy) { StrategyStats() }
                
                when (feedback.type) {
                    FeedbackType.LIKE -> stats.likes++
                    FeedbackType.DISLIKE -> stats.dislikes++
                    FeedbackType.SAVE -> stats.saves++
                    FeedbackType.DISMISS -> stats.dismisses++
                    FeedbackType.CLICK -> stats.clicks++
                    FeedbackType.RATE -> feedback.rating?.let { stats.ratings.add(it) }
                }
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Updates feedback metrics for monitoring
     */
    private fun updateFeedbackMetrics() {
        coroutineScope.launch {
            try {
                val metrics = mutableMapOf<String, Double>()
                
                // Calculate overall metrics
                var totalImpressions = 0
                var totalLikes = 0
                var totalDislikes = 0
                var totalSaves = 0
                var totalDismisses = 0
                var totalClicks = 0
                val allRatings = mutableListOf<Float>()
                
                strategyPerformance.values.forEach { stats ->
                    totalImpressions += stats.impressions
                    totalLikes += stats.likes
                    totalDislikes += stats.dislikes
                    totalSaves += stats.saves
                    totalDismisses += stats.dismisses
                    totalClicks += stats.clicks
                    allRatings.addAll(stats.ratings)
                }
                
                // Calculate metrics
                if (totalImpressions > 0) {
                    metrics["ctr"] = totalClicks.toDouble() / totalImpressions
                    metrics["like_rate"] = totalLikes.toDouble() / totalImpressions
                    metrics["save_rate"] = totalSaves.toDouble() / totalImpressions
                    metrics["dismiss_rate"] = totalDismisses.toDouble() / totalImpressions
                }
                
                if (allRatings.isNotEmpty()) {
                    metrics["avg_rating"] = allRatings.average()
                }
                
                // Update metrics flow
                _feedbackMetrics.value = metrics
                
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Extracts the recommendation strategy from a recommendation
     */
    private fun getRecommendationStrategy(recommendation: TravelRecommendation): String {
        // In a real implementation, this would extract the strategy from the recommendation
        return if (recommendation.isPredictive) {
            "predictive"
        } else {
            "standard"
        }
    }
}
