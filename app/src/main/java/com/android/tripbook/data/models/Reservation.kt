package com.android.tripbook.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: ReservationType,
    val status: ReservationStatus = ReservationStatus.PENDING,
    val title: String,
    val description: String?,
    val location: String,
    val checkInDate: Date,
    val checkOutDate: Date?,
    val guestCount: Int = 1,
    val totalPrice: Double,
    val currency: String = "XAF", // Central African Franc
    val providerName: String,
    val providerContact: String?,
    val bookingReference: String?,
    val specialRequests: String?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)