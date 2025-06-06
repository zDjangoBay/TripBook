package com.tripbook.models

import java.util.Date

data class Trip(
    val id: String,
    val title: String,
    val destination: String,
    val startDate: Date,
    val endDate: Date,
    val description: String,
    val userId: String,
    val agencyId: String? = null,
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 