package com.android.tripbook.datamining.data.model

import java.util.Date

/**
 * Model representing insights about a destination
 */
data class DestinationInsight(
    val id: Long,
    val name: String,
    val country: String,
    val region: String,
    val popularity: Float,
    val rating: Float,
    val imageUrl: String? = null,
    val description: String = "",
    val tags: List<String> = emptyList()
)

/**
 * Model representing insights about travel patterns
 */
data class TravelInsight(
    val id: Long,
    val name: String,
    val type: String,
    val value: Float,
    val confidence: Float,
    val startDate: Date,
    val endDate: Date,
    val data: String // JSON string with detailed pattern data
)

/**
 * Model representing insights about user preferences
 */
data class UserInsight(
    val id: Long,
    val type: String,
    val value: String,
    val strength: Float,
    val source: String
)

/**
 * Model representing a chart data point
 */
data class ChartDataPoint(
    val label: String,
    val value: Float
)

/**
 * Model representing a complete chart dataset
 */
data class ChartDataSet(
    val title: String,
    val description: String = "",
    val dataPoints: List<ChartDataPoint>,
    val maxValue: Float = dataPoints.maxOfOrNull { it.value } ?: 0f,
    val minValue: Float = dataPoints.minOfOrNull { it.value } ?: 0f
)

/**
 * Model representing a recommendation based on data mining
 */
data class TravelRecommendation(
    val id: Long,
    val title: String,
    val description: String,
    val destinationId: Long? = null,
    val destinationName: String? = null,
    val imageUrl: String? = null,
    val confidence: Float,
    val tags: List<String> = emptyList(),
    val relevanceScore: Float // How relevant this recommendation is to the user
)
