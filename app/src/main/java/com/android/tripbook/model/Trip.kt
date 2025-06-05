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
    val agencyService: AgencyService? = null,
    @Ignore
    val coordinates: Location? = null,
    @Ignore
    val routeToNext: RouteInfo? = null
)

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val id: String,
    val name: String,
    // @Ignore removed for TypeConverter
    val startDate: LocalDate,
    // @Ignore removed for TypeConverter
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
    // @Ignore removed for TypeConverter
    val status: TripStatus = TripStatus.PLANNED,
    val type: String = "",
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
    val mapCenter: Location? = null
)