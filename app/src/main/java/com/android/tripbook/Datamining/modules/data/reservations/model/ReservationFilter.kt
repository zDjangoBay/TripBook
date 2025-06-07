package com.android.reservations.model

import java.time.LocalDateTime
import kotlinx.serialization.*
import org.litote.kmongo.serialization.LocalDateTimeSerializer


@Serializable
data class ReservationFilter(
    val status: Set<ReservationStatus>? = null,
    val destination: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDateFrom: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDateTo: LocalDateTime? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val searchQuery: String? = null
)
