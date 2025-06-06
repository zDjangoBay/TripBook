package com.android.tripbook.model

import com.android.tripbook.service.AgencyService
import java.time.LocalDate

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}

enum class ItineraryType {
    ACTIVITY, ACCOMMODATION, TRANSPORTATION
}

// Location data classes for Maps integration
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val placeId: String = "" // For Places API
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
    // New fields for Maps integration
    val coordinates: Location? = null,
    val routeToNext: RouteInfo? = null // Route to next itinerary item
)

// Add these enums and data classes for the Journal feature
enum class Mood(val icon: String) {
    HAPPY("😊"),
    NEUTRAL("😐"),
    SAD("😔"),
    EXCITED("🤩"),
    TIRED("😴")
}

enum class Privacy {
    PUBLIC, FRIENDS, PRIVATE
}

data class JournalEntry(
    val id: String,
    val date: LocalDate,
    val title: String,
    val content: String,
    val mood: Mood = Mood.HAPPY,
    val privacy: Privacy = Privacy.PRIVATE,
    val tags: List<String> = emptyList(),
    val photos: List<Uri> = emptyList()
)

// Update the Trip data class to include journal entries
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
    val activities: List<String> = emptyList(),
    val expenses: List<String> = emptyList(),
    val travelersList: List<String> = emptyList(),
    val itinerary: List<ItineraryItem> = emptyList(),
    val destinationCoordinates: Location? = null,
    val mapCenter: Location? = null,
    val journalEntries: List<JournalEntry> = emptyList() // New field
)
