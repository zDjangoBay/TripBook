package com.android.Tripbook.Datamining.modules.data.reservations.model

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