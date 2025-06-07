package com.android.tripbook.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.android.tripbook.data.models.PaymentStatus
import com.android.tripbook.data.models.ReservationStatus
import java.time.LocalDateTime

/**
 * Room Entity for Trip Reservations
 *
 * This is the central entity that ties together all booking information.
 * It represents a complete reservation made by a user and includes
 * references to the trip, transport, hotel, and payment details.
 *
 * Key Features:
 * - Foreign key relationships with Trip, User, Transport, and Hotel
 * - Reservation status tracking (PENDING, UPCOMING, COMPLETED, CANCELLED)
 * - Payment status tracking
 * - Pricing breakdown
 * - Booking timestamps
 *
 * Used by:
 * - ReservationListScreen for displaying user reservations
 * - ReservationFlow for creating new bookings
 * - PaymentScreen for payment processing
 * - Dashboard for reservation overview
 */
@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransportOptionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transport_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = HotelEntity::class,
            parentColumns = ["id"],
            childColumns = ["hotel_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        androidx.room.Index(value = ["user_id"]),
        androidx.room.Index(value = ["trip_id"]),
        androidx.room.Index(value = ["status"]),
        androidx.room.Index(value = ["payment_status"]),
        androidx.room.Index(value = ["booking_date"]),
        androidx.room.Index(value = ["transport_id"]),
        androidx.room.Index(value = ["hotel_id"])
    ]
)
data class ReservationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "trip_id")
    val tripId: String,

    @ColumnInfo(name = "transport_id")
    val transportId: String?,

    @ColumnInfo(name = "hotel_id")
    val hotelId: String?,

    @ColumnInfo(name = "hotel_nights")
    val hotelNights: Int = 0,

    @ColumnInfo(name = "total_cost")
    val totalCost: Double,

    @ColumnInfo(name = "trip_cost")
    val tripCost: Double = 0.0,

    @ColumnInfo(name = "transport_cost")
    val transportCost: Double = 0.0,

    @ColumnInfo(name = "hotel_cost")
    val hotelCost: Double = 0.0,

    @ColumnInfo(name = "activities_cost")
    val activitiesCost: Double = 0.0,

    @ColumnInfo(name = "status")
    val status: String, // ReservationStatus enum stored as String

    @ColumnInfo(name = "payment_status")
    val paymentStatus: String, // PaymentStatus enum stored as String

    @ColumnInfo(name = "booking_date")
    val bookingDate: LocalDateTime,

    @ColumnInfo(name = "confirmation_number")
    val confirmationNumber: String? = null,

    @ColumnInfo(name = "special_requests")
    val specialRequests: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "cancelled_at")
    val cancelledAt: LocalDateTime? = null,

    @ColumnInfo(name = "cancellation_reason")
    val cancellationReason: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Gets the reservation status as enum
     */
    fun getReservationStatusEnum(): ReservationStatus {
        return ReservationStatus.valueOf(status)
    }

    /**
     * Gets the payment status as enum
     */
    fun getPaymentStatusEnum(): PaymentStatus {
        return PaymentStatus.valueOf(paymentStatus)
    }

    /**
     * Checks if the reservation can be cancelled
     */
    fun canBeCancelled(): Boolean {
        val currentStatus = getReservationStatusEnum()
        return currentStatus == ReservationStatus.PENDING ||
               currentStatus == ReservationStatus.UPCOMING
    }

    /**
     * Checks if the reservation requires payment
     */
    fun requiresPayment(): Boolean {
        return getPaymentStatusEnum() == PaymentStatus.PENDING
    }

    /**
     * Gets a breakdown of costs
     */
    fun getCostBreakdown(): Map<String, Double> {
        return mapOf(
            "Trip" to tripCost,
            "Transport" to transportCost,
            "Hotel" to hotelCost,
            "Activities" to activitiesCost,
            "Total" to totalCost
        )
    }

    companion object {
        /**
         * Creates a new ReservationEntity from booking session
         */
        fun createFromSession(
            id: String,
            userId: String,
            tripId: String,
            transportId: String?,
            hotelId: String?,
            hotelNights: Int,
            totalCost: Double,
            transportCost: Double = 0.0,
            hotelCost: Double = 0.0,
            activitiesCost: Double = 0.0
        ): ReservationEntity {
            return ReservationEntity(
                id = id,
                userId = userId,
                tripId = tripId,
                transportId = transportId,
                hotelId = hotelId,
                hotelNights = hotelNights,
                totalCost = totalCost,
                transportCost = transportCost,
                hotelCost = hotelCost,
                activitiesCost = activitiesCost,
                status = ReservationStatus.PENDING.name,
                paymentStatus = PaymentStatus.PENDING.name,
                bookingDate = LocalDateTime.now()
            )
        }
    }
}
