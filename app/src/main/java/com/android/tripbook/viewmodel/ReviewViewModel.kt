// Create this as a NEW file (this replaces your MockReviewViewModel)
package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.android.tripbook.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class ReviewViewModel : ViewModel() {
    // Private mutable state
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // Public read-only state
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()

    private var nextId = 1

    init {
        // Load any existing reviews (you can integrate with Room database later)
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Here you would load from database
                // For now, we start with empty list
                _reviews.value = emptyList()
                _error.value = null
                Log.d("ReviewViewModel", "Reviews loaded. Total reviews: ${_reviews.value.size}")
            } catch (e: Exception) {
                _error.value = "Failed to load reviews: ${e.message}"
                Log.e("ReviewViewModel", "Failed to load reviews", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReview(
        tripId: Int,
        userName: String,
        userAvatar: String? = null,
        comment: String,
        rating: Float,
        images: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                Log.d("ReviewViewModel", "Adding review for trip $tripId by $userName with rating $rating")

                val newReview = Review(
                    id = nextId++,
                    tripId = tripId,
                    userName = userName,
                    userAvatar = userAvatar ?: generateAvatarUrl(userName),
                    comment = comment,
                    rating = rating,
                    date = getCurrentDate(),
                    images = images
                )

                val currentReviews = _reviews.value.toMutableList()
                currentReviews.add(0, newReview) // Add to the beginning
                _reviews.value = currentReviews

                Log.d("ReviewViewModel", "Added review. Total reviews: ${_reviews.value.size}")
                Log.d("ReviewViewModel", "Review details: ID=${newReview.id}, TripID=${newReview.tripId}, User=${newReview.userName}")

                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to add review: ${e.message}"
                Log.e("ReviewViewModel", "Failed to add review", e)
            }
        }
    }

    fun getReviewsForTrip(tripId: Int): List<Review> {
        val tripReviews = _reviews.value.filter { it.tripId == tripId }
        Log.d("ReviewViewModel", "Getting reviews for trip $tripId: found ${tripReviews.size} reviews")
        return tripReviews
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            try {
                val currentReviews = _reviews.value.toMutableList()
                val removedCount = currentReviews.removeAll { it.id == reviewId }
                _reviews.value = currentReviews

                Log.d("ReviewViewModel", "Deleted review $reviewId. Removed: $removedCount, Total reviews: ${_reviews.value.size}")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to delete review: ${e.message}"
                Log.e("ReviewViewModel", "Failed to delete review", e)
            }
        }
    }

    fun updateReview(
        reviewId: Int,
        comment: String,
        rating: Float,
        images: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                val currentReviews = _reviews.value.toMutableList()
                val index = currentReviews.indexOfFirst { it.id == reviewId }

                if (index != -1) {
                    val updatedReview = currentReviews[index].copy(
                        comment = comment,
                        rating = rating,
                        images = images,
                        date = getCurrentDate() // Update date when modified
                    )
                    currentReviews[index] = updatedReview
                    _reviews.value = currentReviews

                    Log.d("ReviewViewModel", "Updated review $reviewId with new rating $rating")
                } else {
                    Log.w("ReviewViewModel", "Review $reviewId not found for update")
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update review: ${e.message}"
                Log.e("ReviewViewModel", "Failed to update review", e)
            }
        }
    }

    fun getAverageRating(tripId: Int): Float {
        val tripReviews = getReviewsForTrip(tripId)
        val average = if (tripReviews.isNotEmpty()) {
            tripReviews.map { it.rating }.average().toFloat()
        } else {
            0f
        }
        Log.d("ReviewViewModel", "Average rating for trip $tripId: $average (from ${tripReviews.size} reviews)")
        return average
    }

    fun getTotalReviewCount(tripId: Int): Int {
        val count = getReviewsForTrip(tripId).size
        Log.d("ReviewViewModel", "Total review count for trip $tripId: $count")
        return count
    }

    fun clearError() {
        _error.value = null
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun generateAvatarUrl(userName: String): String {
        val encodedName = userName.replace(" ", "+")
        val colors = listOf("0D8ABC", "28a745", "dc3545", "6c757d", "fd7e14", "6f42c1")
        val color = colors[userName.hashCode().absoluteValue % colors.size]
        return "https://ui-avatars.com/api/?name=$encodedName&background=$color&color=fff"
    }
}