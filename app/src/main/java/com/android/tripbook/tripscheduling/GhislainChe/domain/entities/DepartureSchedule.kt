package com.android.tripbook.tripscheduling.GhislainChe.domain.entities

import kotlinx.datetime.LocalDateTime

enum class DepartureStatus {
    SCHEDULED,
    BOARDING,
    DEPARTED,
    CANCELLED
}

data class DepartureSchedule(
    val scheduleId: String,
    val routeId: String,
    val serviceCategory: ServiceCategory,
    val departureTime: LocalDateTime?,
    val estimatedDeparture: LocalDateTime?,
    val status: DepartureStatus,
    val vehicleId: String,
    val availableSeats: Int,
    val totalSeats: Int
)