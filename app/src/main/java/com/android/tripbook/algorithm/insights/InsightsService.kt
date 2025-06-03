package com.android.tripbook.algorithm.insights

import com.android.tripbook.data.repository.InsightsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsightsService @Inject constructor(
    private val repository: InsightsRepository,
    private val destinationRecommender: DestinationRecommender,
    private val tripSchedulingSuggester: TripSchedulingSuggester,
    private val travelTrendAnalyzer: TravelTrendAnalyzer
) {
    suspend fun generateInsights(batchSize: Int = 50) = withContext(Dispatchers.Default) {
        val unprocessedData = repository.getUnprocessedClassifiedData(batchSize)
        
        if (unprocessedData.isNotEmpty()) {
            // Generate destination recommendations
            val recommendations = destinationRecommender.generateRecommendations(unprocessedData)
            recommendations.forEach { repository.saveInsight(it) }
            
            // Generate trip scheduling suggestions
            val suggestions = tripSchedulingSuggester.generateSuggestions(unprocessedData)
            suggestions.forEach { repository.saveInsight(it) }
            
            // Analyze travel trends
            val trends = travelTrendAnalyzer.analyzeTrends(unprocessedData)
            trends.forEach { repository.saveInsight(it) }
            
            // Mark classified data as processed
            unprocessedData.forEach { repository.markClassifiedDataAsProcessed(it.id) }
        }
    }
}





