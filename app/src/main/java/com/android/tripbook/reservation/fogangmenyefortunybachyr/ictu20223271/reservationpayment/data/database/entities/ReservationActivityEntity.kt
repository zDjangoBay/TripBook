package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Room Entity for Reservation-Activity Junction Table
 * 
 * This entity represents the many-to-many relationship between
 * reservations and activities. A reservation can have multiple
 * activities, and an activity can be part of multiple reservations.
 * 
 * Key Features:
 * - Junction table for many-to-many relationship
 * - Foreign key constraints to ensure data integrity
 * - Quantity tracking for activities
 * - Individual activity pricing
 * - Booking timestamps
 * 
 * Used by:
 * - SummaryStep for displaying selected activities
 * - ReservationFlow for activity management
 * - Activity booking and cancellation
 */
@Entity(
    tableName = "reservation_activities",
    foreignKeys = [
        ForeignKey(
            entity = ReservationEntity::class,
            parentColumns = ["id"],
            childColumns = ["reservation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activity_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index(value = ["reservation_id"]),
        androidx.room.Index(value = ["activity_id"]),
        androidx.room.Index(value = ["reservation_id", "activity_id"], unique = true)
    ]
)
data class ReservationActivityEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "reservation_id")
    val reservationId: String,
    
    @ColumnInfo(name = "activity_id")
    val activityId: String,
    
    @ColumnInfo(name = "quantity")
    val quantity: Int = 1,
    
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,
    
    @ColumnInfo(name = "total_price")
    val totalPrice: Double,
    
    @ColumnInfo(name = "scheduled_date")
    val scheduledDate: String? = null, // ISO date string
    
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: String? = null, // Time string
    
    @ColumnInfo(name = "participants")
    val participants: Int = 1,
    
    @ColumnInfo(name = "special_requirements")
    val specialRequirements: String? = null,
    
    @ColumnInfo(name = "confirmation_code")
    val confirmationCode: String? = null,
    
    @ColumnInfo(name = "is_confirmed")
    val isConfirmed: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Calculates the total cost for this activity booking
     */
    fun calculateTotalCost(): Double {
        return unitPrice * quantity
    }
    
    /**
     * Checks if this activity booking is confirmed
     */
    fun isBookingConfirmed(): Boolean {
        return isConfirmed && confirmationCode != null
    }
    
    /**
     * Gets formatted scheduling information
     */
    fun getScheduleInfo(): String? {
        return when {
            scheduledDate != null && scheduledTime != null -> "$scheduledDate at $scheduledTime"
            scheduledDate != null -> scheduledDate
            else -> null
        }
    }
    
    companion object {
        /**
         * Creates a new ReservationActivityEntity
         */
        fun create(
            reservationId: String,
            activityId: String,
            quantity: Int = 1,
            unitPrice: Double,
            participants: Int = 1
        ): ReservationActivityEntity {
            val id = "res_act_${System.currentTimeMillis()}_${(1000..9999).random()}"
            return ReservationActivityEntity(
                id = id,
                reservationId = reservationId,
                activityId = activityId,
                quantity = quantity,
                unitPrice = unitPrice,
                totalPrice = unitPrice * quantity,
                participants = participants
            )
        }
    }
}
