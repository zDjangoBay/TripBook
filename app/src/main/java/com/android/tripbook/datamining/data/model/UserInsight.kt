package com.android.tripbook.datamining.data.model

/**
 * Model class for user insights
 */
data class UserInsight(
    val id: Long,
    val userId: String,
    val category: String,
    val value: String,
    val confidence: Float
)
