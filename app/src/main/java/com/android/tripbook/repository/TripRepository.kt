package com.android.tripbook.repository

import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.TripStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

class TripRepository {
    private val _trips = MutableStateFlow(getInitialTrips())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    fun addTrip(trip: Trip) {
        val tripWithId = trip.copy(id = UUID.randomUUID().toString())
        val currentTrips = _trips.value.toMutableList()
        currentTrips.add(tripWithId)
        _trips.value = currentTrips
    }

    fun updateTrip(trip: Trip) {
        val currentTrips = _trips.value.toMutableList()
        val index = currentTrips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            currentTrips[index] = trip
            _trips.value = currentTrips
        }
    }

    fun deleteTrip(tripId: String) {
        val currentTrips = _trips.value.toMutableList()
        currentTrips.removeAll { it.id == tripId }
        _trips.value = currentTrips
    }

    fun getTripById(tripId: String): Trip? {
        return _trips.value.find { it.id == tripId }
    }

    private fun getInitialTrips(): List<Trip> {
        // Start with an empty list - trips will be added through the creation flow
        return emptyList()
    }

    companion object {
        @Volatile
        private var INSTANCE: TripRepository? = null

        fun getInstance(): TripRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TripRepository().also { INSTANCE = it }
            }
        }
    }
}
