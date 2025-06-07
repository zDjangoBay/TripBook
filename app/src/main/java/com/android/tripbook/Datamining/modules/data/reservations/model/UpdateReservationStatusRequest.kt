package com.android.reservations.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReservationStatusRequest(
    val status: ReservationStatus
)
