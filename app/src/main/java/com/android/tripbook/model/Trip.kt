package com.android.tripbook.model
import androidx.room.TypeConverter
import com.android.tripbook.service.AgencyService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    val name: String,
    val address: String = "",
    val rating: Double = 0.0,
    val types: List<String> = emptyList(),
    val placeId: String? = null,
    val phoneNumber: String? = null,
    val website: String? = null,
    val openingHours: List<String>? = null,
    val priceLevel: Int? = null
) {
}

data class RouteInfo(
    val distance: String = "",
    val duration: String = "",
    val polyline: String = ""
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
    val routeToNext: RouteInfo? = null
) {
    // Helper to check if item is today
    fun isToday(): Boolean = date == LocalDate.now()

    // Helper to check if item is in the past
    fun isPast(): Boolean = date.isBefore(LocalDate.now())
}

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
    // New fields for Maps integration
    val destinationCoordinates: Location? = null,
    val mapCenter: Location? = null
) {
    // Computed properties
    val duration: Long get() = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
    val isActive: Boolean get() = status == TripStatus.ACTIVE
    val isCompleted: Boolean get() = status == TripStatus.COMPLETED

}

// Type converters for Room database
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toRouteInfo(json: String?): RouteInfo? {
        return json?.let { gson.fromJson(it, RouteInfo::class.java) }
    }

    @TypeConverter
    fun fromItineraryList(list: List<ItineraryItem>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toItineraryList(json: String): List<ItineraryItem> {
        val type = object : TypeToken<List<ItineraryItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromAgencyService(service: AgencyService?): String? {
        return service?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toAgencyService(json: String?): AgencyService? {
        return json?.let { gson.fromJson(it, AgencyService::class.java) }
    }
}