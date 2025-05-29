package com.android.tripbook.tripscheduling.GhislainChe.domain.entities

enum class ReservationStatus {
    ACTIVE,
    CONFIRMED,
    EXPIRED,
    CANCELLED
}

data class VehicleCapacity(
    val vehicleId: String,
    val totalSeats: Int,
    val availableSeats: Int,
    val reservedSeats: Int,
    val reservationStatus: ReservationStatus
)