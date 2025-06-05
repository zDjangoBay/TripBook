package com.android.tripbook.model

import java.time.LocalDate

enum class TripStatus {
    PLANNED,
    ACTIVE,
    COMPLETED
}

enum class ItineraryType {
    ACTIVITY,
    ACCOMMODATION,
    TRANSPORTATION
}

data class Trip(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: TripStatus,
    val type: String = "",
    val description: String = "",
    val itinerary: List<ItineraryItem> = emptyList()
)

data class ItineraryItem(
    val date: LocalDate,
    val time: String,
    val title: String,
    val location: String,
    val type: ItineraryType,
    val notes: String = "",
    val agencyService: AgencyService? = null
)

data class AgencyService(
    val id: String,
    val name: String,
    val location: String,
    val price: Double,
    val rating: Double,
    val description: String
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String
)
