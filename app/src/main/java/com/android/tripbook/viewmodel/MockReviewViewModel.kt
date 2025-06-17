package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleReviews
import com.android.tripbook.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockReviewViewModel : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(SampleReviews.get())
    val reviews: StateFlow<List<Review>> = _reviews

    private var currentSort: ((Review, Review) -> Int)? = null
    private var currentPage: Int = 1
    private val pageSize = 5

    fun getReviewsForTrip(tripId: Int): List<Review> {
        val filtered = _reviews.value.filter { it.tripId == tripId }
        val sorted = currentSort?.let { filtered.sortedWith(it) } ?: filtered
        return sorted.take(currentPage * pageSize)
    }

    fun loadMoreReviews(tripId: Int) {
        currentPage++
    }

    fun sortReviewsByVotes(tripId: Int) {
        // currentSort = compareByDescending { it.votes }
        currentPage = 1
    }

    fun sortReviewsByDate(tripId: Int) {
        // Placeholder: if Review had a date, sort by it
        currentSort = null // No-op for now
        currentPage = 1
    }

    fun voteReview(review: Review, upvote: Boolean) {
        val updated = _reviews.value.map {
            if (it == review) it.copy(votes = it.votes + if (upvote) 1 else -1) else it
        }
        _reviews.value = updated
    }
}
