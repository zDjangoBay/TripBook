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
    val id: String = "",
    val tripId: String = "",
    val date: LocalDate,
    val time: String,
    val title: String,
    val location: String,
    val type: ItineraryType,
    val notes: String = "",
    val description: String = "",
    val duration: String = "", // e.g., "2 hours", "All day"
    val cost: Double = 0.0,
    val isCompleted: Boolean = false,
    val agencyService: AgencyService? = null,
    // New fields for Maps integration
    val coordinates: Location? = null,
    val routeToNext: RouteInfo? = null // Route to next itinerary item
)

data class Trip(
    val id: String = "",
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val category: TripCategory,
    val status: TripStatus = TripStatus.PLANNED,
    val description: String = "",
    val budget: Double = 0.0,
    val travelers: Int = 1,
    val companions: List<TravelCompanion> = emptyList(),
    val itinerary: List<ItineraryItem> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val destinationCoordinates: Location? = null,
    val notes: String = "",
    // Using constructor parameter to capture actual creation time
    val createdAt: LocalDate,
    // Using constructor parameter to capture actual update time
    val updatedAt: LocalDate
) {
    constructor(
        id: String = "",
        name: String,
        destination: String,
        startDate: LocalDate,
        endDate: LocalDate,
        category: TripCategory,
        status: TripStatus = TripStatus.PLANNED,
        description: String = "",
        budget: Double = 0.0,
        travelers: Int = 1,
        companions: List<TravelCompanion> = emptyList(),
        itinerary: List<ItineraryItem> = emptyList(),
        journalEntries: List<JournalEntry> = emptyList(),
        destinationCoordinates: Location? = null,
        notes: String = ""
    ) : this(
        id = id,
        name = name,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        category = category,
        status = status,
        description = description,
        budget = budget,
        travelers = travelers,
        companions = companions,
        itinerary = itinerary,
        journalEntries = journalEntries,
        destinationCoordinates = destinationCoordinates,
        notes = notes,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )
}

// Data class for managing trip creation state across multiple steps
data class TripCreationState(
    val currentStep: Int = 1,
    val totalSteps: Int = 6, // Increased to 6 to include agency selection
    val destination: String = "",
    val selectedAgency: Agency? = null, // Added agency selection
    val selectedBus: Bus? = null, // Added bus selection
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val tripName: String = "",
    val category: TripCategory = TripCategory.CULTURAL,
    val description: String = "",
    val companions: List<TravelCompanion> = emptyList(),
    val budget: Double = 0.0
) {
    fun canProceedToNextStep(): Boolean {
        return when (currentStep) {
            1 -> destination.isNotBlank()
            2 -> true // Agency selection is optional
            3 -> startDate != null && endDate != null && !startDate.isAfter(endDate) // Allow same-day trips
            4 -> true // Companions step is optional
            5 -> tripName.isNotBlank()
            6 -> true // Review step
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
