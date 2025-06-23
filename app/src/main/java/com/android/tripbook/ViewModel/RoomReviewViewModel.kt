package com.android.tripbook.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.Model.Review
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.repository.LocalReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * NEW Room-based ViewModel for Review data
 * This is an ALTERNATIVE to MockReviewViewModel
 * Team members can choose which one to use:
 * - MockReviewViewModel: Uses static data (existing, unchanged)
 * - RoomReviewViewModel: Uses Room database (new, optional)
 */
class RoomReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TripBookDatabase.getDatabase(application)
    private val repository = LocalReviewRepository(database.reviewDao())

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    init {
        // Load reviews from database
        viewModelScope.launch {
            repository.getAllReviews().collect { reviewList ->
                _reviews.value = reviewList
            }
        }
        
        // Seed data if database is empty
        viewModelScope.launch {
            repository.seedDataIfEmpty()
        }
    }

    /**
     * Same interface as MockReviewViewModel for compatibility
     */
    fun getReviewsForTrip(tripId: Int): List<Review> {
        return _reviews.value.filter { it.tripId == tripId }
    }
    
    /**
     * NEW Room-specific methods (not available in MockReviewViewModel)
     */
    fun getReviewsForTripFlow(tripId: Int): StateFlow<List<Review>> {
        val tripReviews = MutableStateFlow<List<Review>>(emptyList())
        viewModelScope.launch {
            repository.getReviewsForTrip(tripId).collect { reviewList ->
                tripReviews.value = reviewList
            }
        }
        return tripReviews.asStateFlow()
    }
    
    fun addReview(review: Review) {
        viewModelScope.launch {
            repository.insertReview(review)
        }
    }
    
    fun updateReview(review: Review) {
        viewModelScope.launch {
            repository.updateReview(review)
        }
    }
    
    fun deleteReview(review: Review) {
        viewModelScope.launch {
            repository.deleteReview(review)
        }
    }
    
    fun updateReviewLike(reviewId: Int, isLiked: Boolean, likeCount: Int) {
        viewModelScope.launch {
            repository.updateReviewLike(reviewId, isLiked, likeCount)
        }
    }
}
