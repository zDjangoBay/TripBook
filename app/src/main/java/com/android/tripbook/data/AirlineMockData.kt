package com.android.tripbook.data

import com.android.tripbook.model.AirlineCompany
import com.android.tripbook.model.FlightDestination

object AirlineMockData {

    val airlineCompanies = AirlineCompany(
        id = 1,
        name = "CAMERCO Airlines",
        logoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTuuVv6QZnbPPHZfjzEwfV3H5AXDjr0zUrhiA&s",
        description = "Premium airline service connecting Cameroon to the world",
        rating = 4.2f,
        priceRange = "From 450,000FCFA"
    )

    val flightDestinations = listOf(
        FlightDestination(
            id = 1,
            name = "Paris",
            imageUrl = "https://img.static-af.com/transform/45cb9a13-b167-4842-8ea8-05d0cc7a4d04/",
            description = "The City of Light awaits with its iconic landmarks and rich culture",
            duration = "6h 30min",
            price = "from 450,000 FCFA",
            distance = "285 km",
            popularTimes = "Morning & Evening"
        ),
        FlightDestination(
            id = 2,
            name = "Dubai",
            imageUrl = "https://mediaoffice.ae/-/media/2025/february/09-02/04/9f895655-09cf-440c-ad2e-d0cd4f8542b0.jpg",
            description = "Modern metropolis with luxury shopping and stunning architecture",
            duration = "8h 15min",
            price = "From 380,000 FCFA",
            distance = "500 km",
            popularTimes = "Night Service"
        ),
        FlightDestination(
            id = 3,
            name = "London",
            imageUrl = "https://images.squarespace-cdn.com/content/v1/5b192c6e75f9ee0e3184ccee/1694715878518-WRESZAYO38THVKOZ8WZS/image-asset.jpeg",
            description = "Historic capital with royal palaces and world-class museums",
            duration = "7h 45min",
            price = "From 520,000 FCFA",
            distance = "900 km",
            popularTimes = "Early Morning"
        ),
        FlightDestination(
            id = 4,
            name = "Istanbul",
            imageUrl = "https://www.hotelgift.com/media/wp/HG/2022/08/blue-mosque-Turkey-where-to-stay-in-istanbul.webp",
            description = "Where Europe meets Asia, rich in history and culture",
            duration = "6h 15min",
            price = "From 320,000 FCFA",
            distance = "2000 km",
            popularTimes = "Evenings"
        ),
        FlightDestination(
            id = 5,
            name = "Casablanca",
            imageUrl = "https://moroccomwtours.com/wp-content/uploads/2024/10/city-evening-casablanca-morocco.jpg",
            description = "Gateway to Morocco with beautiful Atlantic coastline",
            duration = "4h 15min",
            price = "From 280,000 FCFA",
            distance = "400 km",
            popularTimes = "All day"
        )
    )
}