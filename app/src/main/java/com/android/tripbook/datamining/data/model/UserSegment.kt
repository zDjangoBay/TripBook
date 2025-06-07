package com.android.tripbook.datamining.data.model

/**
 * Model representing a segment of users with similar preferences
 */
data class UserSegment(
    val id: Int,
    val name: String,
    val description: String,
    val userIds: List<String>,
    val primaryFeatures: List<Pair<String, String>>, // List of (feature type, feature value) pairs
    val size: Int
)
