package com.android.tripbook.model

import com.android.tripbook.service.AgencyService
import java.time.LocalDate

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}

enum class ItineraryType {
    ACTIVITY, ACCOMMODATION, TRANSPORTATION
}

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val placeId: String = ""
)

data class RouteInfo(
    val distance: String = "",
    val duration: String = "",
    val polyline: String = "" // Encoded polyline for route display
)

data class ItineraryItem(
    val date: LocalDate,
    val time: String,
    val title: String,
    val location: String,
    val type: ItineraryType,
    val notes: String = "",
    val agencyService: AgencyService? = null,
    val coordinates: Location? = null,
    val routeToNext: RouteInfo? = null
)

data class Trip(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: TripStatus = TripStatus.PLANNED,
    val type: String = "",
    val description: String = "",

    // Fixed: Restore proper type safety
    val activities: List<Activity> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val travelersList: List<Traveler> = emptyList(),

    val itinerary: List<ItineraryItem> = emptyList(),
    val destinationCoordinates: Location? = null,
    val mapCenter: Location? = null
)

data class Activity(
    val name: String,
    val description: String = "",
    val location: String = "",
    val date: LocalDate? = null,
    val time: String = "" // Added back time field for granularity
)

// Fixed: Keep both name and description, use Double for consistency with TravelAgencyService
data class Expense(
    val name: String,
    val description: String = "", // Keep description for detailed expense tracking
    val amount: Double, // Changed to Double for consistency with TravelAgencyService
    val category: String = "",
    val date: LocalDate = LocalDate.now()
)

// Fixed: Re-add isLeader property while keeping new fields
data class Traveler(
    val id: String,
    val name: String,
    val email: String = "",
    val isLeader: Boolean = false // Re-added for leadership functionality
)