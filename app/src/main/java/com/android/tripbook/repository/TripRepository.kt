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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun addTrip(trip: Trip) {
        try {
            _isLoading.value = true
            _error.value = null

            // Only generate new ID if one isn't provided
            val tripToAdd = if (trip.id.isBlank()) {
                trip.copy(id = UUID.randomUUID().toString())
            } else {
                trip
            }

            val currentTrips = _trips.value.toMutableList()
            // Check for ID conflicts
            if (currentTrips.any { it.id == tripToAdd.id }) {
                throw IllegalArgumentException("Trip with ID ${tripToAdd.id} already exists")
            }

            currentTrips.add(tripToAdd)
            _trips.value = currentTrips.sortedByDescending { it.startDate }
        } catch (e: Exception) {
            _error.value = "Failed to add trip: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun updateTrip(trip: Trip) {
        try {
            _isLoading.value = true
            _error.value = null

            if (trip.id.isBlank()) {
                throw IllegalArgumentException("Cannot update trip with empty ID")
            }

            val currentTrips = _trips.value.toMutableList()
            val index = currentTrips.indexOfFirst { it.id == trip.id }
            if (index != -1) {
                currentTrips[index] = trip.copy(
                    // Preserve creation timestamp if it exists
                    createdAt = currentTrips[index].createdAt,
                    // Update the modification timestamp
                    updatedAt = LocalDate.now()
                )
                _trips.value = currentTrips.sortedByDescending { it.startDate }
            } else {
                throw IllegalArgumentException("Trip with ID ${trip.id} not found")
            }
        } catch (e: Exception) {
            _error.value = "Failed to update trip: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteTrip(tripId: String) {
        try {
            _isLoading.value = true
            _error.value = null

            if (tripId.isBlank()) {
                throw IllegalArgumentException("Cannot delete trip with empty ID")
            }

            val currentTrips = _trips.value.toMutableList()
            val removed = currentTrips.removeAll { it.id == tripId }

            if (!removed) {
                throw IllegalArgumentException("Trip with ID $tripId not found")
            }

            _trips.value = currentTrips
        } catch (e: Exception) {
            _error.value = "Failed to delete trip: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun getTripById(tripId: String): Trip? {
        return if (tripId.isBlank()) {
            null
        } else {
            _trips.value.find { it.id == tripId }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun cleanup() {
        _trips.value = emptyList()
        _error.value = null
        _isLoading.value = false
    }

    private fun getInitialTrips(): List<Trip> {
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
