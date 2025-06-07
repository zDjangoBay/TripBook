package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.models

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data class representing a trip
 */
data class Trip(
    val id: String,
    val title: String,
    val fromLocation: String,
    val toLocation: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val imageUrl: String,
    val basePrice: Double,
    val description: String,
    val duration: String,
    val category: TripCategory
)

/**
 * Trip categories
 */
enum class TripCategory {
    ADVENTURE,
    CULTURAL,
    RELAXATION,
    BUSINESS,
    FAMILY
}

/**
 * Transport options for a trip
 */
data class TransportOption(
    val id: String,
    val type: TransportType,
    val name: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val price: Double,
    val description: String
)

/**
 * Transport types
 */
enum class TransportType {
    PLANE,
    CAR,
    SHIP
}

/**
 * Hotel option
 */
data class HotelOption(
    val id: String,
    val name: String,
    val rating: Int,
    val roomType: String,
    val pricePerNight: Double,
    val imageUrl: String,
    val amenities: List<String>,
    val description: String
)

/**
 * Activity option
 */
data class ActivityOption(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val duration: String,
    val category: ActivityCategory,
    val imageUrl: String
)

/**
 * Activity categories
 */
enum class ActivityCategory {
    MUSEUM,
    OUTDOOR,
    FOOD,
    ENTERTAINMENT,
    CULTURAL,
    ADVENTURE
}

/**
 * Reservation session to track user selections
 */
data class ReservationSession(
    val tripId: String,
    val selectedTransport: TransportOption? = null,
    val selectedHotel: HotelOption? = null,
    val hotelNights: Int = 0,
    val selectedActivities: List<ActivityOption> = emptyList(),
    val totalCost: Double = 0.0
)

/**
 * Final reservation
 */
data class TripReservation(
    val id: String,
    val trip: Trip,
    val transport: TransportOption,
    val hotel: HotelOption?,
    val hotelNights: Int,
    val activities: List<ActivityOption>,
    val totalCost: Double,
    val status: ReservationStatus,
    val bookingDate: LocalDateTime,
    val paymentStatus: PaymentStatus
)

/**
 * Reservation status
 */
enum class ReservationStatus {
    PENDING,
    UPCOMING,
    COMPLETED,
    CANCELLED
}

/**
 * Payment status
 */
enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
