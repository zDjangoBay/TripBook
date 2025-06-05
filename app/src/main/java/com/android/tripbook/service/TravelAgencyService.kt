package com.android.tripbook.service

import com.android.tripbook.model.AgencyService
import kotlinx.coroutines.delay

class TravelAgencyService {
    suspend fun getAgenciesForDestination(destination: String): List<AgencyService> {
        // Simulate network delay
        delay(1000)
        
        // Mock data
        return listOf(
            AgencyService(
                id = "1",
                name = "Safari Adventures",
                location = destination,
                price = 299.99,
                rating = 4.7,
                description = "Guided safari tours with experienced rangers"
            ),
            AgencyService(
                id = "2",
                name = "African Explorers",
                location = destination,
                price = 199.99,
                rating = 4.5,
                description = "Cultural tours and wildlife experiences"
            ),
            AgencyService(
                id = "3",
                name = "Luxury Safaris",
                location = destination,
                price = 499.99,
                rating = 4.9,
                description = "Premium safari experiences with luxury accommodations"
            )
        )
    }
    
    suspend fun getAccommodationsForDestination(destination: String): List<AgencyService> {
        // Simulate network delay
        delay(1000)
        
        // Mock data
        return listOf(
            AgencyService(
                id = "4",
                name = "Safari Lodge",
                location = destination,
                price = 150.0,
                rating = 4.6,
                description = "Comfortable lodge with views of the savanna"
            ),
            AgencyService(
                id = "5",
                name = "Luxury Tented Camp",
                location = destination,
                price = 250.0,
                rating = 4.8,
                description = "Luxury tents with en-suite bathrooms and private decks"
            ),
            AgencyService(
                id = "6",
                name = "Eco Resort",
                location = destination,
                price = 180.0,
                rating = 4.4,
                description = "Sustainable eco-friendly resort with solar power"
            )
        )
    }
    
    suspend fun getTransportationForDestination(destination: String): List<AgencyService> {
        // Simulate network delay
        delay(1000)
        
        // Mock data
        return listOf(
            AgencyService(
                id = "7",
                name = "Safari Jeep Rental",
                location = destination,
                price = 80.0,
                rating = 4.3,
                description = "4x4 vehicles suitable for safari terrain"
            ),
            AgencyService(
                id = "8",
                name = "Airport Transfers",
                location = destination,
                price = 50.0,
                rating = 4.5,
                description = "Reliable airport pickup and drop-off service"
            ),
            AgencyService(
                id = "9",
                name = "Private Guide & Driver",
                location = destination,
                price = 120.0,
                rating = 4.7,
                description = "Experienced local guides with private vehicles"
            )
        )
    }
}
