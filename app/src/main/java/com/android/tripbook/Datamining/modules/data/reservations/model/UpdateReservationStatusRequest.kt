package com.android.Tripbook.Datamining.modules.data.reservations.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReservationStatusRequest(
    val status: ReservationStatus
)
