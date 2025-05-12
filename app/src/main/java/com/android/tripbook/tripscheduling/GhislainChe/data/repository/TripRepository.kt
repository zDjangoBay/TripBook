package com.android.tripbook.tripscheduling.GhislainChe.data.repository

import com.android.tripbook.tripscheduling.GhislainChe.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.LocalTime

class TripRepository {
    // In a real app, this would come from a database or API
    private val _trips = MutableStateFlow(generateSampleTrips())
    val trips: Flow<List<Trip>> = _trips
    
    // Filter trips by service tier
    fun getTripsByServiceTier(serviceTier: ServiceTier?): Flow<List<Trip>> {
        return trips.map { tripList ->
            if (serviceTier == null) {
                tripList
            } else {
                tripList.filter { it.serviceTier == serviceTier }
            }
        }
    }
    
    // Get active trips for today
    fun getActiveTrips(): Flow<List<Trip>> {
        val today = LocalDateTime.now()
        return trips.map { tripList ->
            tripList.filter { trip ->
                trip.departureTime?.let { it.toLocalDate() == today.toLocalDate() } ?: false
            }
        }
    }
    
    // Get trip by ID
    fun getTripById(id: String): Flow<Trip?> {
        return trips.map { tripList ->
            tripList.find { it.id == id }
        }
    }
    
    // Reserve a seat on a trip
    suspend fun reserveSeat(tripId: String): Boolean {
        val currentTrips = _trips.value.toMutableList()
        val tripIndex = currentTrips.indexOfFirst { it.id == tripId }
        
        if (tripIndex == -1) return false
        
        val trip = currentTrips[tripIndex]
        if (trip.seatsAvailable <= 0) return false
        
        // Update the trip with one more filled seat
        currentTrips[tripIndex] = trip.copy(filledSeats = trip.filledSeats + 1)
        _trips.value = currentTrips
        
        return true
    }
    
    // Sample data generation
    private fun generateSampleTrips(): List<Trip> {
        val douala = Location("Douala")
        val yaounde = Location("Yaoundé")
        val limbe = Location("Limbe")
        val kribi = Location("Kribi")
        val buea = Location("Buea")
        val bamenda = Location("Bamenda")
        
        val now = LocalDateTime.now()
        val today14_30 = LocalDateTime.of(now.toLocalDate(), LocalTime.of(14, 30))
        val today16_00 = LocalDateTime.of(now.toLocalDate(), LocalTime.of(16, 0))
        val tomorrow08_30 = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.of(8, 30))
        
        return listOf(
            Trip(
                id = "DLA-YAO-REG-1",
                origin = douala,
                destination = yaounde,
                serviceTier = ServiceTier.REGULAR,
                departureTime = today14_30,
                isEstimatedTime = true,
                distance = 250,
                totalSeats = 28,
                filledSeats = 13,
                checkpoints = listOf(
                    Checkpoint(douala, today14_30.minusHours(2), "Douala Station"),
                    Checkpoint(Location("Edéa"), today14_30.minusHours(1), "Rest Stop", 30),
                    Checkpoint(yaounde, today14_30.plusHours(1).plusMinutes(30), "Yaoundé Terminal")
                ),
                price = 3000.0
            ),
            Trip(
                id = "LMB-KRI-VIP-1",
                origin = limbe,
                destination = kribi,
                serviceTier = ServiceTier.VIP,
                departureTime = today16_00,
                isEstimatedTime = false,
                distance = 180,
                totalSeats = 12,
                filledSeats = 8,
                amenities = listOf("Snacks", "Drinks", "AC"),
                price = 8000.0
            ),
            Trip(
                id = "BUE-BAM-PRE-1",
                origin = buea,
                destination = bamenda,
                serviceTier = ServiceTier.PREMIUM,
                departureTime = tomorrow08_30,
                isEstimatedTime = false,
                distance = 300,
                totalSeats = 8,
                filledSeats = 4,
                amenities = listOf("Meals", "Drinks", "AC", "WiFi", "Entertainment"),
                price = 15000.0
            )
        )
    }
}