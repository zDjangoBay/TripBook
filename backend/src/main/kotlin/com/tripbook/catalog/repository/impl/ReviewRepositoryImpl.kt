package com.tripbook.catalog.repository.impl

import com.tripbook.catalog.model.Review
import com.tripbook.catalog.model.ReviewSummary
import com.tripbook.catalog.repository.ReviewRepository
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of the ReviewRepository interface.
 * Uses Exposed SQL framework for database operations.
 */
class ReviewRepositoryImpl : ReviewRepository {

    /**
     * In-memory data store for development and testing.
     * This would be replaced with actual database operations in production.
     */
    private val reviews = mutableListOf<Review>()
    private val helpfulMarks = mutableMapOf<Pair<UUID, UUID>, Boolean>() // (reviewId, userId) -> isMarkedHelpful

    override suspend fun getReviewsByCompany(companyId: UUID): List<Review> = withContext(Dispatchers.IO) {
        // In a real implementation, this would query the database by company ID
        return@withContext reviews.filter { it.companyId == companyId }
    }

    override suspend fun getReviewById(id: UUID): Review? = withContext(Dispatchers.IO) {
        // In a real implementation, this would query the database by review ID
        return@withContext reviews.find { it.id == id }
    }

    override suspend fun saveReview(review: Review): Review = withContext(Dispatchers.IO) {
        // In a real implementation, this would insert or update in the database
        val index = reviews.indexOfFirst { it.id == review.id }
        if (index >= 0) {
            reviews[index] = review
        } else {
            reviews.add(review)
        }
        return@withContext review
    }

    override suspend fun deleteReview(id: UUID): Boolean = withContext(Dispatchers.IO) {
        // In a real implementation, this would delete from the database
        val size = reviews.size
        reviews.removeIf { it.id == id }
        return@withContext reviews.size < size
    }

    override suspend fun markReviewAsHelpful(reviewId: UUID, userId: UUID): Boolean = withContext(Dispatchers.IO) {
        // In a real implementation, this would update a join table or counter
        val key = Pair(reviewId, userId)
        if (helpfulMarks.containsKey(key)) {
            return@withContext false // Already marked
        }
        
        val reviewIndex = reviews.indexOfFirst { it.id == reviewId }
        if (reviewIndex < 0) {
            return@withContext false // Review not found
        }
        
        helpfulMarks[key] = true
        
        // Update the helpful count on the review
        val currentReview = reviews[reviewIndex]
        reviews[reviewIndex] = currentReview.copy(helpfulCount = currentReview.helpfulCount + 1)
        
        return@withContext true
    }

    override suspend fun getReviewSummaryForCompany(companyId: UUID): ReviewSummary = withContext(Dispatchers.IO) {
        // In a real implementation, this would use SQL aggregation functions
        val companyReviews = reviews.filter { it.companyId == companyId }
        
        if (companyReviews.isEmpty()) {
            return@withContext ReviewSummary(
                companyId = companyId,
                averageRating = 0.0f,
                reviewCount = 0,
                ratingDistribution = mapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0)
            )
        }
        
        val averageRating = companyReviews.map { it.rating }.average().toFloat()
        val reviewCount = companyReviews.size
        
        // Calculate distribution of ratings
        val distribution = (1..5).associateWith { rating ->
            companyReviews.count { it.rating == rating }
        }
        
        return@withContext ReviewSummary(
            companyId = companyId,
            averageRating = averageRating,
            reviewCount = reviewCount,
            ratingDistribution = distribution
        )
    }

    override suspend fun getReviewsByUser(userId: UUID): List<Review> = withContext(Dispatchers.IO) {
        // In a real implementation, this would query the database by user ID
        return@withContext reviews.filter { it.userId == userId }
    }
}