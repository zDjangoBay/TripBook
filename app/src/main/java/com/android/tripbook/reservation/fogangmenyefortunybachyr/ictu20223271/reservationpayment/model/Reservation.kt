package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing a travel reservation in the TripBook app
 */
data class Reservation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val destination: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: ReservationStatus,
    val imageUrl: String? = null,
    val price: Double,
    val currency: String = "USD",
    val bookingReference: String,
    val notes: String? = null,
    val accommodationName: String? = null,
    val accommodationAddress: String? = null,
    val transportInfo: String? = null
)

/**
 * Enum representing the possible statuses of a reservation
 */
enum class ReservationStatus {
    CONFIRMED,
    PENDING,
    CANCELLED,
    COMPLETED;
    
    fun getColorName(): String {
        return when (this) {
            CONFIRMED -> "Green"
            PENDING -> "Amber"
            CANCELLED -> "Red"
            COMPLETED -> "Blue"
        }
    }
}
