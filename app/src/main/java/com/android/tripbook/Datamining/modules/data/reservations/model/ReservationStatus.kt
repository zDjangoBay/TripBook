package com.android.reservations.model

import kotlinx.serialization.*

@Serializable
enum class ReservationStatus {
    CONFIRMED,
    PENDING,
    CANCELLED,
    COMPLETED;

    fun getColorName(): String {
        return when (this) {
            CONFIRMED -> "Green"
            PENDING -> "Amber"
            CANCELLED -> "Red"
            COMPLETED -> "Blue"
        }
    }
}
