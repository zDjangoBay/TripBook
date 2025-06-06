package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Review
import com.android.tripbook.model.Rating
import com.android.tripbook.model.ReviewSummary
import com.android.tripbook.model.ReviewType
import com.android.tripbook.model.ReviewStatus
import com.android.tripbook.repository.SupabaseReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class ReviewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val reviews: List<Review> = emptyList(),
    val reviewSummary: ReviewSummary? = null,
    val userRating: Rating? = null,
    val userReview: Review? = null,
    val isSubmittingReview: Boolean = false,
    val isSubmittingRating: Boolean = false,
    val showReviewDialog: Boolean = false,
    val showRatingDialog: Boolean = false
)

class ReviewViewModel(
    private val reviewRepository: SupabaseReviewRepository = SupabaseReviewRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    // Current user ID - in a real app, this would come from authentication
    private val currentUserId = "user_${System.currentTimeMillis()}" // Mock user ID
    private val currentUserName = "Anonymous User" // Mock user name

    fun loadReviewsForTarget(reviewType: ReviewType, targetId: String, targetName: String = "") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Load reviews, summary, and user's existing rating/review in parallel
                val reviews = reviewRepository.getReviewsForTarget(reviewType, targetId)
                val summary = reviewRepository.getReviewSummary(reviewType, targetId)
                val userRating = reviewRepository.getUserRating(currentUserId, reviewType, targetId)
                val userReview = reviewRepository.getUserReview(currentUserId, reviewType, targetId)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reviews = reviews,
                    reviewSummary = summary,
                    userRating = userRating,
                    userReview = userReview
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load reviews: ${e.message}"
                )
            }
        }
    }

    fun submitRating(reviewType: ReviewType, targetId: String, rating: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingRating = true)
            
            val ratingObj = Rating(
                userId = currentUserId,
                reviewType = reviewType,
                targetId = targetId,
                rating = rating,
                createdAt = LocalDateTime.now()
            )
            
            val result = reviewRepository.submitRating(ratingObj)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingRating = false,
                    userRating = result.getOrNull(),
                    showRatingDialog = false
                )
                // Reload summary to get updated average
                loadReviewSummary(reviewType, targetId)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingRating = false,
                    error = "Failed to submit rating: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun submitReview(
        reviewType: ReviewType,
        targetId: String,
        targetName: String,
        rating: Float,
        title: String,
        content: String,
        pros: List<String> = emptyList(),
        cons: List<String> = emptyList(),
        photos: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true)
            
            val review = Review(
                userId = currentUserId,
                userName = currentUserName,
                reviewType = reviewType,
                targetId = targetId,
                targetName = targetName,
                rating = rating,
                title = title,
                content = content,
                pros = pros,
                cons = cons,
                photos = photos,
                isVerified = true, // In a real app, this would be determined by actual usage
                status = ReviewStatus.APPROVED, // In a real app, this might be PENDING for moderation
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            val result = reviewRepository.submitReview(review)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    userReview = result.getOrNull(),
                    showReviewDialog = false
                )
                // Reload reviews and summary
                loadReviewsForTarget(reviewType, targetId, targetName)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    error = "Failed to submit review: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun markReviewHelpful(reviewId: String, isHelpful: Boolean) {
        viewModelScope.launch {
            val result = reviewRepository.markReviewHelpful(reviewId, currentUserId, isHelpful)
            
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to mark review: ${result.exceptionOrNull()?.message}"
                )
            }
            // Note: The helpful count will be updated automatically by the database trigger
            // In a real app, you might want to optimistically update the UI
        }
    }

    private fun loadReviewSummary(reviewType: ReviewType, targetId: String) {
        viewModelScope.launch {
            val summary = reviewRepository.getReviewSummary(reviewType, targetId)
            _uiState.value = _uiState.value.copy(reviewSummary = summary)
        }
    }

    fun showReviewDialog() {
        _uiState.value = _uiState.value.copy(showReviewDialog = true)
    }

    fun hideReviewDialog() {
        _uiState.value = _uiState.value.copy(showReviewDialog = false)
    }

    fun showRatingDialog() {
        _uiState.value = _uiState.value.copy(showRatingDialog = true)
    }

    fun hideRatingDialog() {
        _uiState.value = _uiState.value.copy(showRatingDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        reviewRepository.clearError()
    }

    // Get recent reviews for community feed
    fun loadRecentReviews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val recentReviews = reviewRepository.getRecentReviews(20)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reviews = recentReviews
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load recent reviews: ${e.message}"
                )
            }
        }
    }

    // Check if user can review (has completed the trip/used the service)
    fun canUserReview(reviewType: ReviewType, targetId: String): Boolean {
        // In a real app, this would check if the user has actually completed the trip,
        // used the agency, visited the destination, or participated in the activity
        return when (reviewType) {
            ReviewType.TRIP -> {
                // Check if trip is completed and user was part of it
                true // Simplified for demo
            }
            ReviewType.AGENCY -> {
                // Check if user has booked with this agency
                true // Simplified for demo
            }
            ReviewType.DESTINATION -> {
                // Check if user has visited this destination
                true // Simplified for demo
            }
            ReviewType.ACTIVITY -> {
                // Check if user has participated in this activity
                true // Simplified for demo
            }
        }
    }
}
