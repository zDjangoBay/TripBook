package com.android.tripbook.model

import com.android.tripbook.service.AgencyService
import java.time.LocalDate
import java.time.LocalDateTime

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}

enum class TripCategory {
    CULTURAL, ADVENTURE, RELAXATION, BUSINESS, FAMILY, ROMANTIC, WILDLIFE, HISTORICAL
}

enum class ItineraryType {
    ACTIVITY, ACCOMMODATION, TRANSPORTATION
}

enum class ReviewType {
    TRIP, DESTINATION, AGENCY, ACTIVITY
}

enum class ReviewStatus {
    PENDING, APPROVED, REJECTED
}

// Location data classes for Maps integration///
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

// Review and Rating System Models
data class Review(
    val id: String = "",
    val userId: String = "", // User who wrote the review
    val userName: String = "", // Display name of the reviewer
    val userAvatar: String = "", // Profile picture URL
    val reviewType: ReviewType,
    val targetId: String, // ID of trip, agency, destination, or activity being reviewed
    val targetName: String, // Name of the target for display
    val rating: Float, // 1.0 to 5.0
    val title: String,
    val content: String,
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList(),
    val photos: List<String> = emptyList(), // URLs to review photos
    val helpfulCount: Int = 0, // Number of users who found this helpful
    val isVerified: Boolean = false, // Whether the reviewer actually completed the trip/used the service
    val status: ReviewStatus = ReviewStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class Rating(
    val id: String = "",
    val userId: String,
    val reviewType: ReviewType,
    val targetId: String,
    val rating: Float, // 1.0 to 5.0
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class ReviewSummary(
    val targetId: String,
    val reviewType: ReviewType,
    val averageRating: Float,
    val totalReviews: Int,
    val ratingDistribution: Map<Int, Int> = emptyMap(), // Star count to number of reviews
    val recentReviews: List<Review> = emptyList()
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
    val budget: Int = 0
) {
    fun canProceedToNextStep(): Boolean {
        return when (currentStep) {
            1 -> destination.isNotBlank()
            2 -> true // Agency selection is optional
            3 -> startDate != null && endDate != null && startDate.isBefore(endDate)
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
