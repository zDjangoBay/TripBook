package com.android.Tripbook.Datamining.modules.data.reservations.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UpdateReservationRequest(
    val title: String? = null,
    val destination: String? = null,
    val startDate: String? = null, // Expect ISO-8601 string for instance : 8/17/2018 7:00:00 AM
    val endDate: String? = null,   //
    val status: ReservationStatus? = null,
    val imageUrl: String? = null,
    val price: Double? = null,
    val currency: String? = null,
    val bookingReference: String? = null,
    val notes: String? = null,
    val accommodationName: String? = null,
    val accommodationAddress: String? = null,
    val transportInfo: String? = null
)