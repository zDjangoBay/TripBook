package com.android.tripbook.algorithm.insights

import com.android.tripbook.data.model.ClassifiedData
import com.android.tripbook.data.model.InsightType
import com.android.tripbook.data.model.TravelInsight
import java.util.UUID
import javax.inject.Inject

class DestinationRecommender @Inject constructor() {
    fun generateRecommendations(classifiedData: List<ClassifiedData>): List<TravelInsight> {
        val insights = mutableListOf<TravelInsight>()
        
        // Simple destination recommendations based on categories
        val destinations = mapOf(
            "adventure" to listOf("Costa Rica", "New Zealand", "Nepal"),
            "beach" to listOf("Maldives", "Thailand", "Hawaii"),
            "city" to listOf("Tokyo", "Paris", "New York"),
            "culture" to listOf("Rome", "Kyoto", "Istanbul"),
            "food" to listOf("Italy", "Japan", "Mexico"),
            "nature" to listOf("Iceland", "Norway", "Switzerland"),
            "wildlife" to listOf("Kenya", "Tanzania", "Galapagos Islands")
        )
        
        classifiedData.forEach { data ->
            val categories = data.categories.map { it.name }
            
            categories.forEach { category ->
                destinations[category.lowercase()]?.let { recommendedPlaces ->
                    val place = recommendedPlaces.random()
                    
                    insights.add(
                        TravelInsight(
                            id = UUID.randomUUID().toString(),
                            userId = "user1", // In a real app, this would come from the user data
                            type = InsightType.DESTINATION_RECOMMENDATION,
                            title = "Recommended Destination: $place",
                            description = "Based on your interest in $category, you might enjoy visiting $place.",
                            relevanceScore = 0.8f,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
        
        return insights
    }
}

class TripSchedulingSuggester @Inject constructor() {
    fun generateSuggestions(classifiedData: List<ClassifiedData>): List<TravelInsight> {
        val insights = mutableListOf<TravelInsight>()
        
        // Simple scheduling suggestions
        val seasonalDestinations = mapOf(
            "winter" to listOf("Switzerland", "Japan", "Canada"),
            "spring" to listOf("Netherlands", "Japan", "France"),
            "summer" to listOf("Greece", "Italy", "Spain"),
            "fall" to listOf("New England", "Japan", "Germany")
        )
        
        // Current season (simplified)
        val currentMonth = java.time.LocalDate.now().monthValue
        val currentSeason = when (currentMonth) {
            in 12..2 -> "winter"
            in 3..5 -> "spring"
            in 6..8 -> "summer"
            else -> "fall"
        }
        
        // Generate a scheduling suggestion
        val destinations = seasonalDestinations[currentSeason] ?: listOf()
        if (destinations.isNotEmpty()) {
            val destination = destinations.random()
            
            insights.add(
                TravelInsight(
                    id = UUID.randomUUID().toString(),
                    userId = "user1", // In a real app, this would come from the user data
                    type = InsightType.TRIP_SCHEDULING_SUGGESTION,
                    title = "Perfect Time to Visit $destination",
                    description = "It's currently $currentSeason, which is an ideal time to visit $destination.",
                    relevanceScore = 0.7f,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        
        return insights
    }
}

class TravelTrendAnalyzer @Inject constructor() {
    fun analyzeTrends(classifiedData: List<ClassifiedData>): List<TravelInsight> {
        val insights = mutableListOf<TravelInsight>()
        
        // Simple trend analysis (in a real app, this would analyze data across users)
        if (classifiedData.isNotEmpty()) {
            // Count categories
            val categoryCount = mutableMapOf<String, Int>()
            classifiedData.forEach { data ->
                data.categories.forEach { category ->
                    categoryCount[category.name] = (categoryCount[category.name] ?: 0) + 1
                }
            }
            
            // Find most popular category
            val mostPopularCategory = categoryCount.maxByOrNull { it.value }
            
            mostPopularCategory?.let { (category, count) ->
                insights.add(
                    TravelInsight(
                        id = UUID.randomUUID().toString(),
                        userId = "user1", // In a real app, this would be a general insight
                        type = InsightType.TRAVEL_TREND,
                        title = "Popular Travel Trend: $category",
                        description = "$category travel is trending right now. Many travelers are exploring this type of experience.",
                        relevanceScore = 0.6f,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
        
        return insights
    }
}



