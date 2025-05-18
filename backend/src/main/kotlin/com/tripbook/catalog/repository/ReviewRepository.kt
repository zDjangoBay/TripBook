package com.tripbook.catalog.repository

import com.tripbook.catalog.model.Review
import com.tripbook.catalog.model.ReviewSummary
import java.util.UUID

/**
 * Repository interface for Review data operations.
 * Defines methods for CRUD operations and queries related to company reviews.
 */
interface ReviewRepository {
    /**
     * Retrieves all reviews for a specific company.
     * 
     * @param companyId The ID of the company
     * @return List of reviews for the company
     */
    suspend fun getReviewsByCompany(companyId: UUID): List<Review>
    
    /**
     * Retrieves a specific review by ID.
     * 
     * @param id The review ID
     * @return The review if found, null otherwise
     */
    suspend fun getReviewById(id: UUID): Review?
    
    /**
     * Saves a new review or updates an existing one.
     * 
     * @param review The review to save
     * @return The saved review with any auto-generated fields populated
     */
    suspend fun saveReview(review: Review): Review
    
    /**
     * Deletes a review by ID.
     * 
     * @param id The ID of the review to delete
     * @return true if deleted, false if not found
     */
    suspend fun deleteReview(id: UUID): Boolean
    
    /**
     * Marks a review as helpful, incrementing its helpful count.
     * 
     * @param reviewId The ID of the review
     * @param userId The ID of the user marking the review as helpful
     * @return true if successful, false otherwise
     */
    suspend fun markReviewAsHelpful(reviewId: UUID, userId: UUID): Boolean
    
    /**
     * Calculates and retrieves the review summary for a company.
     * 
     * @param companyId The ID of the company
     * @return The review summary with statistics
     */
    suspend fun getReviewSummaryForCompany(companyId: UUID): ReviewSummary
    
    /**
     * Retrieves all reviews submitted by a specific user.
     * 
     * @param userId The ID of the user
     * @return List of reviews by the user
     */
    suspend fun getReviewsByUser(userId: UUID): List<Review>
}