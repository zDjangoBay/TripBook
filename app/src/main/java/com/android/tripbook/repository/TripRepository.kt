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

    // --- Budget/Statistics/Category Management Methods ---

    fun getTotalPlannedBudget(): Double {
        return _trips.value.sumOf { (it.plannedBudget as? Double?) ?: (it.budget as? Double?) ?: 0.0 }
    }

    fun getTotalActualCost(): Double {
        return _trips.value.sumOf { (it.actualCost as? Double?) ?: 0.0 }
    }

    fun getBudgetByCategory(category: TripCategory): Double {
        return _trips.value
            .filter { it.category == category || it.categories.contains(category) }
            .sumOf { (it.plannedBudget as? Double?) ?: (it.budget as? Double?) ?: 0.0 }
    }

    fun getTripsByCategory(category: TripCategory): List<Trip> {
        return _trips.value.filter { it.category == category || it.categories.contains(category) }
    }

    fun getTripCountByStatus(status: TripStatus): Int {
        return _trips.value.count { it.status == status }
    }

    fun getAllCategories(): Set<TripCategory> {
        return _trips.value.flatMap { it.categories }.toSet()
    }

    fun updateTripCategories(tripId: String, categories: List<TripCategory>) {
        val currentTrips = _trips.value.toMutableList()
        val index = currentTrips.indexOfFirst { it.id == tripId }
        if (index != -1) {
            val trip = currentTrips[index]
            currentTrips[index] = trip.copy(categories = categories)
            _trips.value = currentTrips
        }
    }

    fun updateTripPlannedBudget(tripId: String, plannedBudget: Double) {
        val currentTrips = _trips.value.toMutableList()
        val index = currentTrips.indexOfFirst { it.id == tripId }
        if (index != -1) {
            val trip = currentTrips[index]
            currentTrips[index] = trip.copy(plannedBudget = plannedBudget)
            _trips.value = currentTrips
        }
    }

    fun updateTripActualCost(tripId: String, actualCost: Double) {
        val currentTrips = _trips.value.toMutableList()
        val index = currentTrips.indexOfFirst { it.id == tripId }
        if (index != -1) {
            val trip = currentTrips[index]
            currentTrips[index] = trip.copy(actualCost = actualCost)
            _trips.value = currentTrips
        }
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