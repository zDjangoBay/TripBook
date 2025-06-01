package com.android.tripbook.reservation.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a reservation in the system
 */
data class Reservation(
    val id: String,
    val userId: String,
    val title: String,
    val type: ReservationType,
    val status: ReservationStatus,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val location: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    // Keep these for backward compatibility
    val bookingDate: LocalDateTime = LocalDateTime.now(),
    val totalPrice: Double = 0.0,
    val currency: String = "USD",
    val accommodationDetails: AccommodationReservation? = null,
    val tourDetails: TourReservation? = null,
    val activityDetails: ActivityReservation? = null,
    val paymentInfo: PaymentInfo? = null,
    val notes: String = ""
)

/**
 * Types of reservations supported by the system
 */
enum class ReservationType {
    ACCOMMODATION,
    TOUR,
    ACTIVITY
}

/**
 * Possible statuses for a reservation
 */
enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED,
    REFUNDED
}

/**
 * Details specific to accommodation reservations
 */
data class AccommodationReservation(
    val accommodationId: String,
    val accommodationName: String,
    val accommodationType: AccommodationType,
    val location: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val guestCount: Int,
    val roomType: String,
    val amenities: List<String> = emptyList(),
    val imageUrl: String = ""
)

/**
 * Types of accommodations
 */
enum class AccommodationType {
    HOTEL,
    HOSTEL,
    APARTMENT,
    GUESTHOUSE,
    RESORT,
    VILLA,
    HOMESTAY
}

/**
 * Details specific to tour reservations
 */
data class TourReservation(
    val tourId: String,
    val tourName: String,
    val tourType: TourType,
    val location: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val participantCount: Int,
    val guideIncluded: Boolean = true,
    val itinerary: List<String> = emptyList(),
    val imageUrl: String = ""
)

/**
 * Types of tours
 */
enum class TourType {
    CITY,
    ADVENTURE,
    CULTURAL,
    WILDLIFE,
    CULINARY,
    HISTORICAL
}

/**
 * Details specific to activity reservations
 */
data class ActivityReservation(
    val activityId: String,
    val activityName: String,
    val activityType: ActivityType,
    val location: String,
    val date: LocalDate,
    val startTime: String,
    val duration: Int, // in minutes
    val participantCount: Int,
    val equipmentIncluded: Boolean = false,
    val imageUrl: String = ""
)

/**
 * Types of activities
 */
enum class ActivityType {
    SIGHTSEEING,
    WATER_SPORTS,
    HIKING,
    SAFARI,
    WORKSHOP,
    ENTERTAINMENT
}

/**
 * Payment information for a reservation
 */
data class PaymentInfo(
    val paymentId: String = UUID.randomUUID().toString(),
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val paymentDate: LocalDateTime? = null,
    val cardLastFourDigits: String? = null,
    val receiptUrl: String? = null
)

/**
 * Payment methods supported by the system
 */
enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    MOBILE_PAYMENT
}

/**
 * Possible statuses for a payment
 */
enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
