package com.tripbook.catalog.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing a review for a travel company.
 * 
 * @property id The unique identifier for the review
 * @property companyId The ID of the company being reviewed
 * @property userId The ID of the user who wrote the review
 * @property rating The rating given (1-5)
 * @property comment The textual review comment
 * @property photos List of photo URLs attached to the review
 * @property helpfulCount Number of users who found this review helpful
 * @property createdAt When the review was created
 * @property updatedAt When the review was last updated
 */
data class Review(
    val id: UUID = UUID.randomUUID(),
    val companyId: UUID,
    val userId: UUID,
    val rating: Int,
    val comment: String,
    val photos: List<String> = listOf(),
    val helpfulCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Data class representing a review summary for a company, with aggregated statistics.
 * 
 * @property companyId The ID of the company
 * @property averageRating The average rating across all reviews
 * @property reviewCount Total number of reviews
 * @property ratingDistribution Distribution of ratings (count per star rating)
 */
data class ReviewSummary(
    val companyId: UUID,
    val averageRating: Float,
    val reviewCount: Int,
    val ratingDistribution: Map<Int, Int> // Maps rating (1-5) to count
)