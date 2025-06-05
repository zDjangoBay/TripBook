package com.tripbook.models

import java.util.Date

data class Review(
    val id: String,
    val tripId: String,
    val userId: String,
    val rating: Float,
    val title: String,
    val content: String,
    val photos: List<String> = emptyList(),
    val likes: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isVerifiedTrip: Boolean = false,
    val helpfulVotes: Int = 0
) 