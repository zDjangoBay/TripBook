package com.android.Tripbook.Datamining.modules.data.reservations.model

import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.*
import org.litote.kmongo.serialization.LocalDateTimeSerializer

@Serializable
data class Reservation(
    val id: String = UUID.randomUUID().toString(),
    val userId: String, // ADDED: To associate the reservation with a user
    val title: String,
    val destination: String,
    @Serializable(with= LocalDateTimeSerializer::class)
    val startDate: LocalDateTime,
    @Serializable(with= LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,
    val status: ReservationStatus,
    val imageUrl: String? = null,
    val price: Double,
    val currency: String = "USD",
    val bookingReference: String,
    val notes: String? = null,
    val accommodationName: String? = null,
    val accommodationAddress: String? = null,
    val transportInfo: String? = null,
    @Serializable(with= LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(), // ADDED: Useful for tracking
    @Serializable(with= LocalDateTimeSerializer::class)
    var updatedAt: LocalDateTime = LocalDateTime.now()  // ADDED: Useful for tracking updates
)