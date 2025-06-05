package com.example.tripbooktest.data

class TripRepository(private val apiService: ApiService) {
    suspend fun fetchTrips(): List<Trip> {
        return apiService.getTrips()
    }
}
