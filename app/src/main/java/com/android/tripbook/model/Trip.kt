package com.android.tripbook.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
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

// ItineraryItem will likely become its own @Entity later
data class ItineraryItem(
    @Ignore
    val date: LocalDate,
    val time: String,
    val title: String,
    val location: String,
    @Ignore
    val type: ItineraryType,
    val notes: String = "",
    @Ignore
    val agencyService: com.android.tripbook.service.AgencyService? = null,
    @Ignore
    val coordinates: Location? = null,
    @Ignore
    val routeToNext: RouteInfo? = null
)

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var startDate: LocalDate = LocalDate.now(),
    var endDate: LocalDate = LocalDate.now(),
    var destination: String = "",
    var travelers: Int = 0,
    var budget: Int = 0,
    var status: TripStatus = TripStatus.PLANNED,
    var type: String = "",
    var description: String = "",
    @Ignore
    var activities: List<String> = emptyList(),
    @Ignore
    var expenses: List<String> = emptyList(),
    @Ignore
    var travelersList: List<String> = emptyList(),
    @Ignore
    var itinerary: List<ItineraryItem> = emptyList(),
    @Ignore
    var destinationCoordinates: Location? = null,
    @Ignore
    var mapCenter: Location? = null
)