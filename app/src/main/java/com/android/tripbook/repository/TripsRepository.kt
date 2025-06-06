package com.android.tripbook.repository

import com.android.tripbook.model.Place
import android.util.Log
import com.android.tripbook.model.Triphome

class TripsRepository {
    // Mock data implementation since Firebase is removed
    suspend fun getUpcomingTrips(): List<Triphome> {
        return try {
            // Return mock data instead of Firebase data
            listOf(
                Triphome(
                    companyName = "Paris Airways",
                    from = "New York",
                    fromshort = "NYC",
                    to = "Paris",
                    toshort = "PAR",
                    date = "2024-06-01",
                    time = "14:30",
                    arriveTime = "08:45",
                    price = 899.99,
                    score = 4.5
                ),
                Triphome(
                    companyName = "Tokyo Express",
                    from = "Los Angeles",
                    fromshort = "LAX",
                    to = "Tokyo",
                    toshort = "NRT",
                    date = "2024-07-15",
                    time = "11:20",
                    arriveTime = "16:30",
                    price = 1299.99,
                    score = 4.8
                )
            )
        } catch (e: Exception) {
            Log.e("TripsRepository", "Error loading trips: ${e.message}")
            emptyList()
        }
    }

    suspend fun getRecommendedTrips(): List<Place> {
        return try {
            // Return mock data instead of Firebase data
            listOf(
                Place(
                    title = "Eiffel Tower",
                    picUrl = ""
                ),
                Place(
                    title = "Tokyo Tower",
                    picUrl = ""
                )
            )
        } catch (e: Exception) {
            Log.e("TripsRepository", "Error loading recommended places: ${e.message}")
            emptyList()
        }
    }
}
