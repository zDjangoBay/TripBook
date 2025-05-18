package com.tripbook.catalog.service

import com.tripbook.catalog.model.Review
import com.tripbook.catalog.model.ReviewSummary
import java.util.UUID

/**
 * Service interface for business logic related to review management.
 * Defines methods for CRUD operations and queries related to company reviews.
 */
interface ReviewService {
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
     * Creates a new review.
     * 
     * @param review The review to create
     * @return The created review
     */
    suspend fun createReview(review: Review): Review
    
    /**
     * Updates an existing review.
     * 
     * @param id The ID of the review to update
     * @param review The updated review data
     * @return The updated review if found, null otherwise
     */
    suspend fun updateReview(id: UUID, review: Review): Review?
    
    /**
     * Deletes a review by ID.
     * 
     * @param id The ID of the review to delete
     * @return true if deleted, false if not found
     */
    suspend fun deleteReview(id: UUID): Boolean
    
    /**
     * Marks a review as helpful.
     * 
     * @param reviewId The ID of the review
     * @param userId The ID of the user marking the review as helpful
     * @return true if successful, false otherwise
     */
    suspend fun markReviewAsHelpful(reviewId: UUID, userId: UUID): Boolean
    
    /**
     * Retrieves the review summary for a company.
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