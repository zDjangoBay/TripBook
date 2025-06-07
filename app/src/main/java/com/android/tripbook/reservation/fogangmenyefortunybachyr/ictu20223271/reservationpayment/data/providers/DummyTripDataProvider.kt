package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.providers

import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.models.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Provides dummy trip data for the application
 */
object DummyTripDataProvider {

    fun getTrips(): List<Trip> = listOf(
        Trip(
            id = "trip_1",
            title = "Safari Adventure in Kenya",
            fromLocation = "Nairobi",
            toLocation = "Masai Mara",
            departureDate = LocalDate.now().plusDays(30),
            returnDate = LocalDate.now().plusDays(35),
            imageUrl = "https://images.unsplash.com/photo-1516026672322-bc52d61a55d5",
            basePrice = 1200.0,
            description = "Experience the magnificent wildlife of Kenya",
            duration = "5 days",
            category = TripCategory.ADVENTURE
        ),
        Trip(
            id = "trip_2",
            title = "Cultural Tour of Morocco",
            fromLocation = "Casablanca",
            toLocation = "Marrakech",
            departureDate = LocalDate.now().plusDays(45),
            returnDate = LocalDate.now().plusDays(52),
            imageUrl = "https://images.unsplash.com/photo-1539650116574-75c0c6d73f6e",
            basePrice = 800.0,
            description = "Discover the rich culture and history of Morocco",
            duration = "7 days",
            category = TripCategory.CULTURAL
        ),
        Trip(
            id = "trip_3",
            title = "Beach Relaxation in Zanzibar",
            fromLocation = "Dar es Salaam",
            toLocation = "Stone Town",
            departureDate = LocalDate.now().plusDays(60),
            returnDate = LocalDate.now().plusDays(67),
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5",
            basePrice = 950.0,
            description = "Relax on pristine beaches with crystal clear waters",
            duration = "7 days",
            category = TripCategory.RELAXATION
        ),
        Trip(
            id = "trip_4",
            title = "Business Conference in Lagos",
            fromLocation = "Abuja",
            toLocation = "Lagos",
            departureDate = LocalDate.now().plusDays(15),
            returnDate = LocalDate.now().plusDays(18),
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945",
            basePrice = 600.0,
            description = "Attend the African Business Summit",
            duration = "3 days",
            category = TripCategory.BUSINESS
        ),
        Trip(
            id = "trip_5",
            title = "Family Adventure in Cape Town",
            fromLocation = "Johannesburg",
            toLocation = "Cape Town",
            departureDate = LocalDate.now().plusDays(90),
            returnDate = LocalDate.now().plusDays(97),
            imageUrl = "https://images.unsplash.com/photo-1580060839134-75a5edca2e99",
            basePrice = 1100.0,
            description = "Perfect family vacation with Table Mountain views",
            duration = "7 days",
            category = TripCategory.FAMILY
        ),
        Trip(
            id = "trip_6",
            title = "European Adventure",
            fromLocation = "New York, NY",
            toLocation = "Paris, France",
            departureDate = LocalDate.now().plusDays(20),
            returnDate = LocalDate.now().plusDays(28),
            imageUrl = "https://images.unsplash.com/photo-1502602898536-47ad22581b52",
            basePrice = 1500.0,
            description = "Explore the City of Light and European culture",
            duration = "8 days",
            category = TripCategory.CULTURAL
        ),
        Trip(
            id = "trip_7",
            title = "Caribbean Cruise",
            fromLocation = "New York, NY",
            toLocation = "Caribbean Islands",
            departureDate = LocalDate.now().plusDays(40),
            returnDate = LocalDate.now().plusDays(47),
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5",
            basePrice = 1200.0,
            description = "Luxury cruise through tropical paradise",
            duration = "7 days",
            category = TripCategory.RELAXATION
        ),
        Trip(
            id = "trip_8",
            title = "Tokyo Business Trip",
            fromLocation = "New York, NY",
            toLocation = "Tokyo, Japan",
            departureDate = LocalDate.now().plusDays(25),
            returnDate = LocalDate.now().plusDays(30),
            imageUrl = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf",
            basePrice = 1800.0,
            description = "Business meetings in the heart of Japan",
            duration = "5 days",
            category = TripCategory.BUSINESS
        )
    )

    fun getTripById(id: String): Trip? = getTrips().find { it.id == id }

    fun getTransportOptions(tripId: String): List<TransportOption> {
        val baseDateTime = LocalDateTime.now().plusDays(30)
        return listOf(
            TransportOption(
                id = "transport_plane_1",
                type = TransportType.PLANE,
                name = "Kenya Airways Flight KQ101",
                departureTime = baseDateTime.withHour(8).withMinute(30),
                arrivalTime = baseDateTime.withHour(10).withMinute(15),
                price = 120.0,
                description = "Direct flight with in-flight meals"
            ),
            TransportOption(
                id = "transport_plane_2",
                type = TransportType.PLANE,
                name = "Ethiopian Airlines ET205",
                departureTime = baseDateTime.withHour(14).withMinute(45),
                arrivalTime = baseDateTime.withHour(16).withMinute(30),
                price = 135.0,
                description = "Premium service with extra legroom"
            ),
            TransportOption(
                id = "transport_car_1",
                type = TransportType.CAR,
                name = "Safari Land Cruiser",
                departureTime = baseDateTime.withHour(6).withMinute(0),
                arrivalTime = baseDateTime.withHour(12).withMinute(0),
                price = 60.0,
                description = "4WD vehicle perfect for safari roads"
            ),
            TransportOption(
                id = "transport_ship_1",
                type = TransportType.SHIP,
                name = "Coastal Ferry Express",
                departureTime = baseDateTime.withHour(18).withMinute(0),
                arrivalTime = baseDateTime.plusDays(1).withHour(8).withMinute(0),
                price = 80.0,
                description = "Overnight ferry with cabin accommodation"
            )
        )
    }

    fun getTransportOptionsByType(type: TransportType): List<TransportOption> {
        return getTransportOptions("").filter { it.type == type }
    }
}
