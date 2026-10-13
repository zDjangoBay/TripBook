package com.android.tripbook.model
import androidx.room.TypeConverter
import com.android.tripbook.service.AgencyService // Assuming AgencyService is defined and Gson-serializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    // Validation helper
    fun isValid(): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
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

    // Helper methods
    fun getTodayItinerary(): List<ItineraryItem> {
        return itinerary.filter { it.isToday() }
    }

    fun getItineraryForDate(date: LocalDate): List<ItineraryItem> {
        return itinerary.filter { it.date == date }
    }

    // Validation
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                (startDate.isBefore(endDate) || startDate.isEqual(endDate)) && // Corrected logic for clarity
                travelers > 0 &&
                budget >= 0
    }
}

// Type converters for Room database
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }

    @TypeConverter
    fun fromTripStatus(status: TripStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTripStatus(status: String): TripStatus {
        return TripStatus.valueOf(status)
    }

    @TypeConverter
    fun fromItineraryType(type: ItineraryType): String {
        return type.name
    }

    @TypeConverter
    fun toItineraryType(type: String): ItineraryType {
        return ItineraryType.valueOf(type)
    }

    // --- MODIFIED for Nullability Handling ---
    @TypeConverter
    fun fromStringList(list: List<String>?): String? { // Made input nullable
        return gson.toJson(list) // Gson will handle null lists gracefully (produces "null" string)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> { // Made input nullable
        if (json == null || json == "null") return emptyList() // Handle explicit null or "null" string
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return location?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLocation(json: String?): Location? {
        return json?.let { gson.fromJson(it, Location::class.java) }
    }

    @TypeConverter
    fun fromRouteInfo(routeInfo: RouteInfo?): String? {
        return routeInfo?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toRouteInfo(json: String?): RouteInfo? {
        return json?.let { gson.fromJson(it, RouteInfo::class.java) }
    }

    @TypeConverter
    fun fromItineraryList(list: List<ItineraryItem>?): String? { // Made input nullable
        return gson.toJson(list) // Gson handles null lists gracefully
    }

    @TypeConverter
    fun toItineraryList(json: String?): List<ItineraryItem> { // Made input nullable
        if (json == null || json == "null") return emptyList() // Handle explicit null or "null" string
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