package com.android.tripbook.data

import com.android.tripbook.mockData.SampleReviews
import com.android.tripbook.model.Review

class MockReviewRepository {
    fun getReviewsForTrip(tripId: Int): List<Review> {
        return SampleReviews.reviews.filter { it.tripId == tripId }
    }
}
