package com.android.tripbook.tripscheduling.GhislainChe.domain.repositories

import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.DepartureSchedule
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.ServiceCategory
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.VehicleCapacity
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.DepartureStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TripScheduleRepository {
    suspend fun getAvailableSchedules(
        origin: String,
        destination: String,
        date: LocalDate,
        serviceCategory: ServiceCategory? = null
    ): List<DepartureSchedule>

    suspend fun getScheduleById(scheduleId: String): DepartureSchedule?
    suspend fun observeScheduleCapacity(scheduleId: String): Flow<VehicleCapacity>
    suspend fun reserveSeats(scheduleId: String, seatCount: Int): SeatReservation
    suspend fun updateScheduleStatus(scheduleId: String, status: DepartureStatus)
}

// Placeholder for SeatReservation entity
data class SeatReservation(
    val reservationId: String,
    val scheduleId: String,
    val seatCount: Int,
    val status: String
)