package com.android.tripbook.data

import com.android.tripbook.model.AirlineCompany
import com.android.tripbook.model.FlightDestination

// Extension functions for FlightDestination
fun FlightDestination.addRating(newRating: Float): FlightDestination {
    val totalScore = this.rating * this.totalRatings + newRating
    val newTotalRatings = this.totalRatings + 1
    val newAverageRating = totalScore / newTotalRatings

    return this.copy(
        rating = newAverageRating,
        totalRatings = newTotalRatings
    )
}

fun FlightDestination.getDisplayRating(): String {
    return if (totalRatings > 0) {
        String.format("%.1f (%d)", rating, totalRatings)
    } else {
        "No ratings yet"
    }
}

object AirlineMockData {

    val airlineCompanies = AirlineCompany(
        id = 1,
        name = "CAMERCO Airlines",
        logoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTuuVv6QZnbPPHZfjzEwfV3H5AXDjr0zUrhiA&s",
        description = "Premium airline service connecting Cameroon to the world",
        rating = 4.2f,
        priceRange = "From 450,000FCFA"
    )

    // Made mutable to allow rating updates
    val flightDestinations = mutableListOf(
        FlightDestination(
            id = 1,
            name = "Paris",
            imageUrl = "https://img.static-af.com/transform/45cb9a13-b167-4842-8ea8-05d0cc7a4d04/",
            description = "The City of Light awaits with its iconic landmarks and rich culture",
            duration = "6h 30min",
            price = "from 450,000 FCFA",
            distance = "285 km",
            popularTimes = "Morning & Evening",
            rating = 4.3f,
            totalRatings = 127
        ),
        FlightDestination(
            id = 2,
            name = "Dubai",
            imageUrl = "https://mediaoffice.ae/-/media/2025/february/09-02/04/9f895655-09cf-440c-ad2e-d0cd4f8542b0.jpg",
            description = "Modern metropolis with luxury shopping and stunning architecture",
            duration = "8h 15min",
            price = "From 380,000 FCFA",
            distance = "500 km",
            popularTimes = "Night Service",
            rating = 4.6f,
            totalRatings = 89
        ),
        FlightDestination(
            id = 3,
            name = "London",
            imageUrl = "https://images.squarespace-cdn.com/content/v1/5b192c6e75f9ee0e3184ccee/1694715878518-WRESZAYO38THVKOZ8WZS/image-asset.jpeg",
            description = "Historic capital with royal palaces and world-class museums",
            duration = "7h 45min",
            price = "From 520,000 FCFA",
            distance = "900 km",
            popularTimes = "Early Morning",
            rating = 4.1f,
            totalRatings = 203
        ),
        FlightDestination(
            id = 4,
            name = "Istanbul",
            imageUrl = "https://www.hotelgift.com/media/wp/HG/2022/08/blue-mosque-Turkey-where-to-stay-in-istanbul.webp",
            description = "Where Europe meets Asia, rich in history and culture",
            duration = "6h 15min",
            price = "From 320,000 FCFA",
            distance = "2000 km",
            popularTimes = "Evenings",
            rating = 4.4f,
            totalRatings = 156
        ),
        FlightDestination(
            id = 5,
            name = "Casablanca",
            imageUrl = "https://moroccomwtours.com/wp-content/uploads/2024/10/city-evening-casablanca-morocco.jpg",
            description = "Gateway to Morocco with beautiful Atlantic coastline",
            duration = "4h 15min",
            price = "From 280,000 FCFA",
            distance = "400 km",
            popularTimes = "All day",
            rating = 3.9f,
            totalRatings = 74
        )
    )

    fun updateDestinationRating(destinationId: Int, newRating: Float) {
        val destinationIndex = flightDestinations.indexOfFirst { it.id == destinationId }
        if (destinationIndex != -1) {
            val currentDestination = flightDestinations[destinationIndex]
            val updatedDestination = currentDestination.addRating(newRating)
            flightDestinations[destinationIndex] = updatedDestination
        }
    }
}