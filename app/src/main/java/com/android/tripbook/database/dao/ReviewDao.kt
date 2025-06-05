package com.android.tripbook.database.dao

import androidx.room.*
import com.android.tripbook.database.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Review operations
 * Provides database operations for reviews
 */
@Dao
interface ReviewDao {
    
    /**
     * Get all reviews as Flow
     */
    @Query("SELECT * FROM reviews ORDER BY id ASC")
    fun getAllReviews(): Flow<List<ReviewEntity>>
    
    /**
     * Get all reviews as list (for one-time queries)
     */
    @Query("SELECT * FROM reviews ORDER BY id ASC")
    suspend fun getAllReviewsOnce(): List<ReviewEntity>
    
    /**
     * Get reviews for specific trip
     */
    @Query("SELECT * FROM reviews WHERE tripId = :tripId ORDER BY id ASC")
    fun getReviewsForTrip(tripId: Int): Flow<List<ReviewEntity>>
    
    /**
     * Get reviews for specific trip as list
     */
    @Query("SELECT * FROM reviews WHERE tripId = :tripId ORDER BY id ASC")
    suspend fun getReviewsForTripOnce(tripId: Int): List<ReviewEntity>
    
    /**
     * Get review by ID
     */
    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Int): ReviewEntity?
    
    /**
     * Get review by ID as Flow
     */
    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    fun getReviewByIdFlow(reviewId: Int): Flow<ReviewEntity?>
    
    /**
     * Insert single review
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
    
    /**
     * Insert multiple reviews
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
    
    /**
     * Update review
     */
    @Update
    suspend fun updateReview(review: ReviewEntity)
    
    /**
     * Delete review
     */
    @Delete
    suspend fun deleteReview(review: ReviewEntity)
    
    /**
     * Delete review by ID
     */
    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: Int)
    
    /**
     * Delete all reviews for a trip
     */
    @Query("DELETE FROM reviews WHERE tripId = :tripId")
    suspend fun deleteReviewsForTrip(tripId: Int)
    
    /**
     * Delete all reviews
     */
    @Query("DELETE FROM reviews")
    suspend fun deleteAllReviews()
    
    /**
     * Get reviews count for trip
     */
    @Query("SELECT COUNT(*) FROM reviews WHERE tripId = :tripId")
    suspend fun getReviewsCountForTrip(tripId: Int): Int
    
    /**
     * Get average rating for trip
     */
    @Query("SELECT AVG(rating) FROM reviews WHERE tripId = :tripId")
    suspend fun getAverageRatingForTrip(tripId: Int): Double?
    
    /**
     * Update review like status
     */
    @Query("UPDATE reviews SET isLiked = :isLiked, likeCount = :likeCount WHERE id = :reviewId")
    suspend fun updateReviewLike(reviewId: Int, isLiked: Boolean, likeCount: Int)
}
