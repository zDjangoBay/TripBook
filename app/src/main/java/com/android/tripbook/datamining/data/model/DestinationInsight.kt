package com.android.tripbook.datamining.data.model

/**
 * Model class for destination insights
 */
data class DestinationInsight(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val popularity: Float,
    val category: String,
    val tags: List<String>,
    val region: String = "",
    val budgetCategory: String = ""
)
