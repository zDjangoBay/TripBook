package com.android.tripbook.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.tripbook.service.AgencyService
import java.time.LocalDate

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}

enum class ItineraryType {
    ACTIVITY, ACCOMMODATION, TRANSPORTATION
}

// Location data classes for Maps integration
// Marked with @Ignore if used directly in Trip, or handle with TypeConverters/Embedded later
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
    @Ignore // Temporarily ignore if not an entity itself yet for Trip conversion
    val date: LocalDate,
    val time: String,
    val title: String,
    val location: String,
    @Ignore // Temporarily ignore if not an entity itself yet for Trip conversion
    val type: ItineraryType,
    val notes: String = "",
    @Ignore // Temporarily ignore if not an entity itself yet for Trip conversion
    val agencyService: AgencyService? = null,
    @Ignore // Location is a complex type
    val coordinates: Location? = null,
    @Ignore // RouteInfo is a complex type
    val routeToNext: RouteInfo? = null // Route to next itinerary item
)

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val id: String,
    val name: String,
    @Ignore // Room needs a TypeConverter for LocalDate
    val startDate: LocalDate,
    @Ignore // Room needs a TypeConverter for LocalDate
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int, // Assuming this is the planned budget amount as a simple Int
    @Ignore // Room needs a TypeConverter for Enums
    val status: TripStatus = TripStatus.PLANNED,
    val type: String = "", // Assuming this 'type' is a simple String
    val description: String = "",
    @Ignore // To be handled with TypeConverter or separate entity later
    val activities: List<String> = emptyList(),
    @Ignore // This will be replaced by the new Expense entity and a relationship
    val expenses: List<String> = emptyList(),
    @Ignore // To be handled with TypeConverter or separate entity later
    val travelersList: List<String> = emptyList(),
    @Ignore // ItineraryItem will become its own entity with a relationship
    val itinerary: List<ItineraryItem> = emptyList(),
    @Ignore // Location is a complex type, handle later or with @Embedded + TypeConverters
    val destinationCoordinates: Location? = null,
    @Ignore // Location is a complex type, handle later or with @Embedded + TypeConverters
    val mapCenter: Location? = null // Center point for map display
)