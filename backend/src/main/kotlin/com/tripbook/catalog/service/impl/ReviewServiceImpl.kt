package com.tripbook.catalog.service.impl

import com.tripbook.catalog.model.Review
import com.tripbook.catalog.model.ReviewSummary
import com.tripbook.catalog.repository.CompanyRepository
import com.tripbook.catalog.repository.ReviewRepository
import com.tripbook.catalog.service.ReviewService
import java.time.LocalDateTime
import java.util.UUID

/**
 * Implementation of the ReviewService interface.
 * Contains business logic for review-related operations.
 */
class ReviewServiceImpl(
    private val reviewRepository: ReviewRepository,
    private val companyRepository: CompanyRepository
) : ReviewService {

    override suspend fun getReviewsByCompany(companyId: UUID): List<Review> {
        return reviewRepository.getReviewsByCompany(companyId)
    }

    override suspend fun getReviewById(id: UUID): Review? {
        return reviewRepository.getReviewById(id)
    }

    override suspend fun createReview(review: Review): Review {
        // Ensure a new ID is generated
        val newReview = review.copy(
            id = UUID.randomUUID(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // Validate the review rating is between 1 and 5
        if (newReview.rating < 1 || newReview.rating > 5) {
            throw IllegalArgumentException("Rating must be between 1 and 5")
        }
        
        // Verify company exists
        val company = companyRepository.getCompanyById(newReview.companyId)
            ?: throw IllegalArgumentException("Company not found")
        
        // Save the review
        val savedReview = reviewRepository.saveReview(newReview)
        
        // Update company rating statistics
        updateCompanyRatingStatistics(newReview.companyId)
        
        return savedReview
    }

    override suspend fun updateReview(id: UUID, review: Review): Review? {
        // Check if review exists
        val existingReview = reviewRepository.getReviewById(id) ?: return null
        
        // Update with new data while preserving creation date, ID, and help count
        val updatedReview = review.copy(
            id = existingReview.id,
            createdAt = existingReview.createdAt,
            helpfulCount = existingReview.helpfulCount,
            updatedAt = LocalDateTime.now()
        )
        
        // Validate the review rating is between 1 and 5
        if (updatedReview.rating < 1 || updatedReview.rating > 5) {
            throw IllegalArgumentException("Rating must be between 1 and 5")
        }
        
        // Save the updated review
        val savedReview = reviewRepository.saveReview(updatedReview)
        
        // Update company rating statistics if rating changed
        if (existingReview.rating != updatedReview.rating) {
            updateCompanyRatingStatistics(updatedReview.companyId)
        }
        
        return savedReview
    }

    override suspend fun deleteReview(id: UUID): Boolean {
        val review = reviewRepository.getReviewById(id) ?: return false
        val companyId = review.companyId
        
        val result = reviewRepository.deleteReview(id)
        
        // Update company rating statistics
        if (result) {
            updateCompanyRatingStatistics(companyId)
        }
        
        return result
    }

    override suspend fun markReviewAsHelpful(reviewId: UUID, userId: UUID): Boolean {
        return reviewRepository.markReviewAsHelpful(reviewId, userId)
    }

    override suspend fun getReviewSummaryForCompany(companyId: UUID): ReviewSummary {
        return reviewRepository.getReviewSummaryForCompany(companyId)
    }

    override suspend fun getReviewsByUser(userId: UUID): List<Review> {
        return reviewRepository.getReviewsByUser(userId)
    }
    
    /**
     * Updates the rating statistics for a company based on its reviews.
     * 
     * @param companyId The ID of the company to update
     */
    private suspend fun updateCompanyRatingStatistics(companyId: UUID) {
        val company = companyRepository.getCompanyById(companyId) ?: return
        
        val summary = reviewRepository.getReviewSummaryForCompany(companyId)
        
        val updatedCompany = company.copy(
            rating = summary.averageRating,
            reviewCount = summary.reviewCount,
            updatedAt = LocalDateTime.now()
        )
        
        companyRepository.saveCompany(updatedCompany)
    }
}