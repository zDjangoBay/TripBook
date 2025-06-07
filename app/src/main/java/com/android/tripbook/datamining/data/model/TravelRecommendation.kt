package com.android.tripbook.datamining.data.model

/**
 * Model class for travel recommendations
 */
data class TravelRecommendation(
    val id: Long,
    val title: String,
    val description: String,
    val destinationId: Long,
    val destinationName: String,
    val imageUrl: String,
    val confidence: Float,
    val tags: List<String>,
    val relevanceScore: Float,
    val region: String = "",
    val budgetCategory: String = "",
    val travelStyle: String = "",
    val isPredictive: Boolean = false
)
