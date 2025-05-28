package com.android.tripbook.data

import com.android.tripbook.mockData.SampleTrips
import com.android.tripbook.model.Trip

object MockTripRepository {
    fun getTripById(id: Int): Trip? {
        return SampleTrips.get().find { it.id == id }
    }
}
