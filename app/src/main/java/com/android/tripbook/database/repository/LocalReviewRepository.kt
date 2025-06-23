package com.android.tripbook.database.repository

import com.android.tripbook.Model.Review
import com.android.tripbook.database.dao.ReviewDao
import com.android.tripbook.database.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for Review data using Room database
 * Provides clean API for accessing review data locally
 */
class LocalReviewRepository(private val reviewDao: ReviewDao) {
    
    /**
     * Get all reviews as Flow for reactive UI updates
     */
    fun getAllReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews().map { entities ->
            entities.map { it.toReview() }
        }
    }
    
    /**
     * Get all reviews as list (for one-time queries)
     */
    suspend fun getAllReviewsOnce(): List<Review> {
        return reviewDao.getAllReviewsOnce().map { it.toReview() }
    }
    
    /**
     * Get reviews for specific trip
     */
    fun getReviewsForTrip(tripId: Int): Flow<List<Review>> {
        return reviewDao.getReviewsForTrip(tripId).map { entities ->
            entities.map { it.toReview() }
        }
    }
    
    /**
     * Get reviews for specific trip as list
     */
    suspend fun getReviewsForTripOnce(tripId: Int): List<Review> {
        return reviewDao.getReviewsForTripOnce(tripId).map { it.toReview() }
    }
    
    /**
     * Get review by ID
     */
    suspend fun getReviewById(reviewId: Int): Review? {
        return reviewDao.getReviewById(reviewId)?.toReview()
    }
    
    /**
     * Get review by ID as Flow
     */
    fun getReviewByIdFlow(reviewId: Int): Flow<Review?> {
        return reviewDao.getReviewByIdFlow(reviewId).map { entity ->
            entity?.toReview()
        }
    }
    
    /**
     * Insert single review
     */
    suspend fun insertReview(review: Review) {
        reviewDao.insertReview(ReviewEntity.fromReview(review))
    }
    
    /**
     * Insert multiple reviews
     */
    suspend fun insertReviews(reviews: List<Review>) {
        val entities = reviews.map { ReviewEntity.fromReview(it) }
        reviewDao.insertReviews(entities)
    }
    
    /**
     * Update review
     */
    suspend fun updateReview(review: Review) {
        reviewDao.updateReview(ReviewEntity.fromReview(review))
    }
    
    /**
     * Delete review
     */
    suspend fun deleteReview(review: Review) {
        reviewDao.deleteReview(ReviewEntity.fromReview(review))
    }
    
    /**
     * Delete review by ID
     */
    suspend fun deleteReviewById(reviewId: Int) {
        reviewDao.deleteReviewById(reviewId)
    }
    
    /**
     * Delete all reviews for a trip
     */
    suspend fun deleteReviewsForTrip(tripId: Int) {
        reviewDao.deleteReviewsForTrip(tripId)
    }
    
    /**
     * Delete all reviews
     */
    suspend fun deleteAllReviews() {
        reviewDao.deleteAllReviews()
    }
    
    /**
     * Get reviews count for trip
     */
    suspend fun getReviewsCountForTrip(tripId: Int): Int {
        return reviewDao.getReviewsCountForTrip(tripId)
    }
    
    /**
     * Get average rating for trip
     */
    suspend fun getAverageRatingForTrip(tripId: Int): Double? {
        return reviewDao.getAverageRatingForTrip(tripId)
    }
    
    /**
     * Update review like status
     */
    suspend fun updateReviewLike(reviewId: Int, isLiked: Boolean, likeCount: Int) {
        reviewDao.updateReviewLike(reviewId, isLiked, likeCount)
    }
    
    /**
     * Check if database is empty and seed with sample data if needed
     */
    suspend fun seedDataIfEmpty() {
        if (getAllReviewsOnce().isEmpty()) {
            val sampleReviews = getSampleReviews()
            insertReviews(sampleReviews)
        }
    }
    
    /**
     * Get sample reviews data (same as existing SampleReviews)
     */
    private fun getSampleReviews(): List<Review> {
        return listOf(
            Review(
                id = 6,
                tripId = 2,
                rating = 3,
                username = "Briyand",
                comment = "Absolutely breathtaking!",
                images = listOf(
                    "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
                )
            ),
            Review(
                id = 7,
                tripId = 1,
                rating = 5,
                username = "Sarah",
                comment = "Amazing experience in Yaounde! The city has so much to offer.",
                images = listOf(
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
                )
            ),
            Review(
                id = 8,
                tripId = 3,
                rating = 4,
                username = "Mike",
                comment = "Kribi beaches are stunning! Perfect for relaxation.",
                images = listOf(
                    "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48="
                )
            ),
            Review(
                id = 9,
                tripId = 4,
                rating = 5,
                username = "Emma",
                comment = "Bamenda's highland culture is fascinating!",
                images = listOf(
                    "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA="
                )
            ),
            Review(
                id = 10,
                tripId = 1,
                rating = 4,
                username = "John",
                comment = "Great food and friendly people in Yaounde!",
                images = listOf()
            ),
            Review(
                id = 11,
                tripId = 2,
                rating = 5,
                username = "Lisa",
                comment = "Mount Cameroon hike was challenging but rewarding!",
                images = listOf(
                    "https://source.unsplash.com/800x600/?mountains"
                )
            )
        )
    }
}
