package com.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbook.models.Review
import com.tripbook.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadReviewsForTrip(tripId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            reviewRepository.getReviewsForTrip(tripId)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { reviews ->
                    _reviews.value = reviews
                    _isLoading.value = false
                }
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            _isLoading.value = true
            reviewRepository.addReview(review)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun updateReview(review: Review) {
        viewModelScope.launch {
            _isLoading.value = true
            reviewRepository.updateReview(review)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            reviewRepository.deleteReview(reviewId)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun markReviewAsHelpful(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.markReviewAsHelpful(reviewId)
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun likeReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.likeReview(reviewId)
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }
} 