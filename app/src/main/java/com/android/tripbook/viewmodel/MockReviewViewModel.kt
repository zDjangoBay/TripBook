package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleReviews
import com.android.tripbook.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockReviewViewModel : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(SampleReviews.get())
    val reviews: StateFlow<List<Review>> = _reviews

    fun toggleLike(tripId: Int, username: String) {
        val updatedList = _reviews.value.map { review ->
            if (review.tripId == tripId && review.username == username) {
                val isLiked = !review.isLiked
                review.copy(
                    isLiked = isLiked,
                    likeCount = if (isLiked) review.likeCount + 1 else review.likeCount - 1
                )
            } else review
        }
        _reviews.value = updatedList
    }

    fun toggleFlag(tripId: Int, username: String) {
        // (optional, leave empty for now)
    }
}
