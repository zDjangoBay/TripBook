package com.android.tripbook.datamining.data.model

/**
 * Model class for travel insights
 */
data class TravelInsight(
    val id: Long,
    val name: String,
    val description: String,
    val type: String,
    val data: String,
    val confidence: Float
)
