package com.android.tripbook.model

import com.android.tripbook.service.AgencyService
import java.time.LocalDate
import java.util.UUID

// Journal Entry related models
data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val title: String,
    val content: String,
    val mood: Mood,
    val privacy: Privacy,
    val tags: List<String> = emptyList(),
    val photoUrls: List<String> = emptyList()
)

enum class Mood(val icon: String) {
    HAPPY("😊"),
    EXCITED("🤩"),
    RELAXED("😌"),
    ADVENTUROUS("🤠"),
    GRATEFUL("🙏"),
    AMAZED("😍"),
    PEACEFUL("😇"),
    NOSTALGIC("🥺")
}

enum class Privacy {
    PUBLIC,
    FRIENDS,
    PRIVATE
}

// Location data class for the app
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val placeId: String = ""
)

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
    val journalEntries: List<JournalEntry> = emptyList(),
    val transportationBookings: List<TransportationBooking> = emptyList(),
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

// Transportation Models
enum class TransportationType {
    FLIGHT,
    TRAIN,
    BUS,
    CAR_RENTAL,
    TAXI_UBER,
    FERRY,
    OTHER
}

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

data class TransportationBooking(
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val type: TransportationType,
    val title: String,
    val provider: String, // Airline, Train company, etc.
    val bookingReference: String = "",
    val departureLocation: String,
    val arrivalLocation: String,
    val departureDateTime: String, // ISO format
    val arrivalDateTime: String, // ISO format
    val passengers: Int = 1,
    val cost: Double = 0.0,
    val currency: String = "USD",
    val status: BookingStatus = BookingStatus.PENDING,
    val seatNumbers: List<String> = emptyList(),
    val notes: String = "",
    val confirmationDetails: String = "",
    val cancellationPolicy: String = "",
    val contactInfo: String = "",
    val regulations: List<String> = emptyList() // Compliance requirements
)