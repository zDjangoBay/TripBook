package com.android.Tripbook.Datamining.modules.data.reservations.model

import java.time.LocalDateTime
import java.util.UUID

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
