package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleReviews
import com.android.tripbook.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockReviewViewModel : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(SampleReviews.get())
    val reviews: StateFlow<List<Review>> = _reviews

    fun getReviewsForTrip(tripId: Int): List<Review> {
        return _reviews.value.filter { it.tripId == tripId }
    }
}
