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
        return listOf(
            Trip(
                id = "1",
                name = "Safari Adventure",
                startDate = LocalDate.of(2024, 12, 15),
                endDate = LocalDate.of(2024, 12, 22),
                destination = "Kenya, Tanzania",
                travelers = 4,
                budget = 2400,
                status = TripStatus.PLANNED,
                category = TripCategory.WILDLIFE
            ),
            Trip(
                id = "2",
                name = "Morocco Discovery",
                startDate = LocalDate.of(2025, 1, 10),
                endDate = LocalDate.of(2025, 1, 18),
                destination = "Marrakech, Fez",
                travelers = 2,
                budget = 1800,
                status = TripStatus.ACTIVE,
                category = TripCategory.CULTURAL
            ),
            Trip(
                id = "3",
                name = "Cape Town Explorer",
                startDate = LocalDate.of(2024, 9, 5),
                endDate = LocalDate.of(2024, 9, 12),
                destination = "South Africa",
                travelers = 6,
                budget = 3200,
                status = TripStatus.COMPLETED,
                category = TripCategory.ADVENTURE
            )
        )
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
