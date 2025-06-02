// Enhanced ReviewViewModel.kt - Acts as persistent mock data storage
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
    companion object {
        // Static storage to persist reviews across ViewModel instances
        private var persistentReviews = mutableListOf<Review>()
        private var nextIdCounter = 1

        // Initialize with some sample data if empty
        fun initializeSampleData() {
            if (persistentReviews.isEmpty()) {
                persistentReviews.addAll(
                    listOf(
                        Review(
                            id = nextIdCounter++,
                            tripId = 1,
                            userName = "Sarah Johnson",
                            userAvatar = "https://ui-avatars.com/api/?name=Sarah+Johnson&background=0D8ABC&color=fff",
                            comment = "Amazing trip! The scenery was breathtaking and the guide was very knowledgeable.",
                            rating = 5.0f,
                            date = "2024-05-15",
                            images = listOf()
                        ),
                        Review(
                            id = nextIdCounter++,
                            tripId = 1,
                            userName = "Mike Chen",
                            userAvatar = "https://ui-avatars.com/api/?name=Mike+Chen&background=28a745&color=fff",
                            comment = "Great experience overall. Would definitely recommend to friends and family.",
                            rating = 4.5f,
                            date = "2024-05-10",
                            images = listOf()
                        ),
                        Review(
                            id = nextIdCounter++,
                            tripId = 2,
                            userName = "Emma Davis",
                            userAvatar = "https://ui-avatars.com/api/?name=Emma+Davis&background=dc3545&color=fff",
                            comment = "Beautiful locations but the weather wasn't great. Still enjoyed it though!",
                            rating = 4.0f,
                            date = "2024-05-08",
                            images = listOf()
                        ),
                        Review(
                            id = nextIdCounter++,
                            tripId = 3,
                            userName = "Alex Rodriguez",
                            userAvatar = "https://ui-avatars.com/api/?name=Alex+Rodriguez&background=6c757d&color=fff",
                            comment = "Perfect for adventure seekers! Challenging but totally worth it.",
                            rating = 4.8f,
                            date = "2024-05-05",
                            images = listOf()
                        )
                    )
                )
                Log.d("ReviewViewModel", "Initialized with ${persistentReviews.size} sample reviews")
            }
        }
    }

    // Private mutable state
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // Public read-only state
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Initialize sample data if needed
        initializeSampleData()
        // Load reviews from persistent storage
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load from our persistent mock storage
                _reviews.value = persistentReviews.toList().sortedByDescending { it.id }
                _error.value = null
                Log.d("ReviewViewModel", "Reviews loaded from persistent storage. Total reviews: ${_reviews.value.size}")
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
                    id = nextIdCounter++,
                    tripId = tripId,
                    userName = userName,
                    userAvatar = userAvatar ?: generateAvatarUrl(userName),
                    comment = comment,
                    rating = rating,
                    date = getCurrentDate(),
                    images = images
                )

                // Add to persistent storage
                persistentReviews.add(0, newReview)

                // Update StateFlow
                _reviews.value = persistentReviews.toList().sortedByDescending { it.id }

                Log.d("ReviewViewModel", "Added review to persistent storage. Total reviews: ${persistentReviews.size}")
                Log.d("ReviewViewModel", "Review details: ID=${newReview.id}, TripID=${newReview.tripId}, User=${newReview.userName}")

                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to add review: ${e.message}"
                Log.e("ReviewViewModel", "Failed to add review", e)
            }
        }
    }

    fun getReviewsForTrip(tripId: Int): List<Review> {
        val tripReviews = persistentReviews.filter { it.tripId == tripId }
        Log.d("ReviewViewModel", "Getting reviews for trip $tripId: found ${tripReviews.size} reviews")
        return tripReviews.sortedByDescending { it.id }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            try {
                val removedCount = persistentReviews.removeAll { it.id == reviewId }

                // Update StateFlow
                _reviews.value = persistentReviews.toList().sortedByDescending { it.id }

                Log.d("ReviewViewModel", "Deleted review $reviewId from persistent storage. Removed: $removedCount, Total reviews: ${persistentReviews.size}")
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
                val index = persistentReviews.indexOfFirst { it.id == reviewId }

                if (index != -1) {
                    val updatedReview = persistentReviews[index].copy(
                        comment = comment,
                        rating = rating,
                        images = images,
                        date = getCurrentDate() // Update date when modified
                    )
                    persistentReviews[index] = updatedReview

                    // Update StateFlow
                    _reviews.value = persistentReviews.toList().sortedByDescending { it.id }

                    Log.d("ReviewViewModel", "Updated review $reviewId in persistent storage with new rating $rating")
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

    fun getAllReviews(): List<Review> {
        return persistentReviews.toList().sortedByDescending { it.id }
    }

    fun getReviewById(reviewId: Int): Review? {
        return persistentReviews.find { it.id == reviewId }
    }

    fun clearAllReviews() {
        viewModelScope.launch {
            persistentReviews.clear()
            nextIdCounter = 1
            _reviews.value = emptyList()
            Log.d("ReviewViewModel", "Cleared all reviews from persistent storage")
        }
    }

    fun refreshReviews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _reviews.value = persistentReviews.toList().sortedByDescending { it.id }
                _error.value = null
                Log.d("ReviewViewModel", "Refreshed reviews from persistent storage")
            } catch (e: Exception) {
                _error.value = "Failed to refresh reviews: ${e.message}"
                Log.e("ReviewViewModel", "Failed to refresh reviews", e)
            } finally {
                _isLoading.value = false
            }
        }
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