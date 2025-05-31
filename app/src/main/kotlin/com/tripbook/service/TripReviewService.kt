package com.tripbook.service

import com.tripbook.models.TripReview
import com.tripbook.models.TravelAgency
import com.tripbook.repository.TripReviewRepository
import com.tripbook.repository.TravelAgencyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TripReviewService(
    private val tripReviewRepository: TripReviewRepository,
    private val travelAgencyRepository: TravelAgencyRepository
) {
    @Transactional
    fun createReview(review: TripReview): TripReview {
        validateRating(review.rating)
        validateRating(review.safetyRating)
        validateRating(review.valueForMoneyRating)
        validateRating(review.experienceRating)
        
        val savedReview = tripReviewRepository.save(review)
        
        // Update agency statistics if review is for an agency
        review.travelAgency?.let { agency ->
            updateAgencyStatistics(agency.id)
        }
        
        return savedReview
    }
    
    fun getReviewsByUser(userId: Long): List<TripReview> {
        return tripReviewRepository.findByUserId(userId)
    }
    
    fun getReviewsByAgency(agencyId: Long): List<TripReview> {
        return tripReviewRepository.findByTravelAgencyId(agencyId)
    }
    
    fun getReviewsByDestination(destination: String): List<TripReview> {
        return tripReviewRepository.findByDestination(destination)
    }
    
    fun getVerifiedReviews(): List<TripReview> {
        return tripReviewRepository.findByIsVerifiedTrue()
    }
    
    @Transactional
    fun updateReview(reviewId: Long, updatedReview: TripReview): TripReview {
        val existingReview = tripReviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review not found") }
        
        validateRating(updatedReview.rating)
        validateRating(updatedReview.safetyRating)
        validateRating(updatedReview.valueForMoneyRating)
        validateRating(updatedReview.experienceRating)
        
        val savedReview = tripReviewRepository.save(updatedReview.copy(
            id = reviewId,
            createdAt = existingReview.createdAt,
            updatedAt = LocalDateTime.now()
        ))
        
        // Update agency statistics if review is for an agency
        savedReview.travelAgency?.let { agency ->
            updateAgencyStatistics(agency.id)
        }
        
        return savedReview
    }
    
    @Transactional
    fun deleteReview(reviewId: Long) {
        val review = tripReviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review not found") }
        
        val agencyId = review.travelAgency?.id
        
        tripReviewRepository.deleteById(reviewId)
        
        // Update agency statistics if review was for an agency
        agencyId?.let { updateAgencyStatistics(it) }
    }
    
    private fun validateRating(rating: Int) {
        if (rating < 1 || rating > 5) {
            throw IllegalArgumentException("Rating must be between 1 and 5")
        }
    }
    
    private fun updateAgencyStatistics(agencyId: Long) {
        val averageRating = tripReviewRepository.getAverageRatingForAgency(agencyId) ?: 0.0
        val totalReviews = tripReviewRepository.getTotalReviewsForAgency(agencyId)
        
        travelAgencyRepository.findById(agencyId).ifPresent { agency ->
            travelAgencyRepository.save(agency.copy(
                averageRating = averageRating,
                totalReviews = totalReviews.toInt()
            ))
        }
    }
} 