package com.android.tripbook.tripscheduling.GhislainChe.data.repository

import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.DepartureSchedule
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.ServiceCategory
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.VehicleCapacity
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.DepartureStatus
import com.android.tripbook.tripscheduling.GhislainChe.domain.repositories.SeatReservation
import com.android.tripbook.tripscheduling.GhislainChe.domain.repositories.TripScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class TripScheduleRepositoryImpl : TripScheduleRepository {
    private val schedules = mutableListOf<DepartureSchedule>()
    private val capacityFlows = mutableMapOf<String, MutableStateFlow<VehicleCapacity>>()

    override suspend fun getAvailableSchedules(
        origin: String,
        destination: String,
        date: LocalDate,
        serviceCategory: ServiceCategory?
    ): List<DepartureSchedule> {
        // Dummy implementation: filter schedules based on criteria
        // In a real app, this would fetch from a local database or remote API
        return schedules.filter {
            it.origin.equals(origin, ignoreCase = true) &&
            it.destination.equals(destination, ignoreCase = true) &&
            // it.departureTime.date == date && // Assuming DepartureSchedule has a LocalDateTime for departureTime
            (serviceCategory == null || it.serviceCategory == serviceCategory) &&
            it.status == DepartureStatus.SCHEDULED
        }
    }

    override suspend fun getScheduleById(scheduleId: String): DepartureSchedule? {
        // Dummy implementation: find a schedule by its ID
        return schedules.find { it.scheduleId == scheduleId }
    }

    override suspend fun observeScheduleCapacity(scheduleId: String): Flow<VehicleCapacity> {
        // Dummy implementation: return a flow for capacity updates
        // In a real app, this would observe changes from a reactive data source
        return capacityFlows.getOrPut(scheduleId) { 
            MutableStateFlow(VehicleCapacity(scheduleId, 0, 50, 0, com.android.tripbook.tripscheduling.GhislainChe.domain.entities.ReservationStatus.ACTIVE)) 
        }.asStateFlow()
    }

    override suspend fun reserveSeats(scheduleId: String, seatCount: Int): SeatReservation {
        // Dummy implementation: simulate seat reservation
        val schedule = schedules.find { it.scheduleId == scheduleId }
        val capacity = capacityFlows[scheduleId]?.value
        
        if (schedule != null && capacity != null && (capacity.availableSeats - seatCount) >= 0) {
            val newBookedSeats = capacity.bookedSeats + seatCount
            val newAvailableSeats = capacity.totalSeats - newBookedSeats
            capacityFlows[scheduleId]?.value = capacity.copy(
                bookedSeats = newBookedSeats,
                availableSeats = newAvailableSeats
            )
            return SeatReservation(
                reservationId = "RSV_${System.currentTimeMillis()}",
                scheduleId = scheduleId,
                seatCount = seatCount,
                status = "CONFIRMED"
            )
        } else {
            return SeatReservation(
                reservationId = "FAIL_${System.currentTimeMillis()}",
                scheduleId = scheduleId,
                seatCount = seatCount,
                status = "FAILED_NO_CAPACITY"
            )
        }
    }

    override suspend fun updateScheduleStatus(scheduleId: String, status: DepartureStatus) {
        // Dummy implementation: update the status of a schedule
        schedules.find { it.scheduleId == scheduleId }?.let {
            val index = schedules.indexOf(it)
            if (index != -1) {
                schedules[index] = it.copy(status = status)
            }
        }
    }

    // Helper to add some dummy data for testing
    fun addDummySchedule(schedule: DepartureSchedule) {
        schedules.add(schedule)
        capacityFlows.getOrPut(schedule.scheduleId) { 
            MutableStateFlow(VehicleCapacity(schedule.scheduleId, 0, schedule.vehicleTotalCapacity, schedule.vehicleTotalCapacity, com.android.tripbook.tripscheduling.GhislainChe.domain.entities.ReservationStatus.ACTIVE)) 
        }
    }
}