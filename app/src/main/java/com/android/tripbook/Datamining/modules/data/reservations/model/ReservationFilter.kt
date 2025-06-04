package com.android.Tripbook.Datamining.modules.data.reservations.model

import java.time.LocalDateTime

data class ReservationFilter(
    val status: Set<ReservationStatus>? = null,
    val destination: String? = null,
    val startDateFrom: LocalDateTime? = null,
    val startDateTo: LocalDateTime? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val searchQuery: String? = null
)
