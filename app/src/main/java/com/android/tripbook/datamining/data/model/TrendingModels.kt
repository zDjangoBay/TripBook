package com.android.tripbook.datamining.data.model

/**
 * Model representing a trending destination
 */
data class TrendingDestination(
    val id: Long,
    val name: String,
    val region: String,
    val imageUrl: String? = null,
    val trendingScore: Int,
    val hourlyInteractions: Int,
    val dailyInteractions: Int,
    val weeklyInteractions: Int
)

/**
 * Model representing a trending topic
 */
data class TrendingTopic(
    val type: String, // e.g., "tag", "region", "activity"
    val value: String, // e.g., "beach", "East Africa", "hiking"
    val trendingScore: Int,
    val hourlyInteractions: Int,
    val dailyInteractions: Int,
    val weeklyInteractions: Int
)
