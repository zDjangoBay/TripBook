package com.android.tripbook.model

import com.android.tripbook.service.AgencyService
import java.time.LocalDate
import java.util.UUID

enum class TripStatus {
    PLANNED,
    ACTIVE,
    COMPLETED
}

data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Double = 0.0,
    val destinationCoordinates: Coordinates? = null,
    val itinerary: List<ItineraryItem> = emptyList(),
    val travelers: Int = 1,
    val status: TripStatus = TripStatus.PLANNED,
    val type: String = "Vacation"
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

enum class ItineraryType {
    ACTIVITY,
    ACCOMMODATION,
    TRANSPORTATION,
    FOOD,
    OTHER
}

data class ItineraryItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: LocalDate,
    val time: String,
    val location: String,
    val type: ItineraryType,
    val notes: String = "",
    val coordinates: Coordinates? = null,
    val routeToNext: RouteInfo? = null,
    val agencyService: AgencyService? = null
)

data class RouteInfo(
    val distance: String,
    val duration: String,
    val polyline: String
)