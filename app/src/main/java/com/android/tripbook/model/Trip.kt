package com.android.tripbook.model

import com.android.tripbook.service.AgencyService
import java.time.LocalDate

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}

enum class TripCategory {
    CULTURAL, ADVENTURE, RELAXATION, BUSINESS, FAMILY, ROMANTIC, WILDLIFE, HISTORICAL
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

data class TravelCompanion(
    val name: String,
    val email: String = "",
    val phone: String = ""
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

data class Trip(
    val id: String = "",
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: TripStatus = TripStatus.PLANNED,
    val category: TripCategory = TripCategory.CULTURAL,
    val description: String = "",
    val companions: List<TravelCompanion> = emptyList(),
    val activities: List<String> = emptyList(),
    val expenses: List<String> = emptyList(),
    val itinerary: List<ItineraryItem> = emptyList(),
    // New fields for Maps integration
    val destinationCoordinates: Location? = null,
    val mapCenter: Location? = null // Center point for map display
)

// Data class for managing trip creation state across multiple steps
data class TripCreationState(
    val currentStep: Int = 1,
    val totalSteps: Int = 5,
    val destination: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val tripName: String = "",
    val category: TripCategory = TripCategory.CULTURAL,
    val description: String = "",
    val companions: List<TravelCompanion> = emptyList(),
    val budget: Int = 0
) {
    fun canProceedToNextStep(): Boolean {
        return when (currentStep) {
            1 -> destination.isNotBlank()
            2 -> startDate != null && endDate != null && startDate.isBefore(endDate)
            3 -> true // Companions step is optional
            4 -> tripName.isNotBlank()
            5 -> true // Review step
            else -> false
        }
    }

    fun toTrip(): Trip {
        return Trip(
            name = tripName,
            startDate = startDate ?: LocalDate.now(),
            endDate = endDate ?: LocalDate.now().plusDays(1),
            destination = destination,
            travelers = companions.size + 1, // +1 for the trip creator
            budget = budget,
            status = TripStatus.PLANNED,
            category = category,
            description = description,
            companions = companions
        )
    }
}
