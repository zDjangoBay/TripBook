package com.android.tripbook.tripscheduling.GhislainChe.domain.usecases

import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.DepartureSchedule
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.ServiceCategory
import kotlinx.datetime.LocalDateTime
import com.android.tripbook.tripscheduling.GhislainChe.domain.repositories.PublicTransportRepository
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime

class CalculateDepartureTimeUseCase @Inject constructor(
    private val publicTransportRepository: PublicTransportRepository
): List<JourneyPlan> {
    fun execute(
        serviceCategory: ServiceCategory,
        departureTime: LocalDateTime,
        isHoliday: Boolean
    ): LocalDateTime? {
        val today = java.time.LocalDateTime.now().toKotlinLocalDateTime()

        val isWeekend = departureTime.dayOfWeek == DayOfWeek.SATURDAY || departureTime.dayOfWeek == DayOfWeek.SUNDAY

        val baseDepartureTime = if (isHoliday) {
            departureTime.toJavaLocalDateTime().plusDays(1).toKotlinLocalDateTime()
        } else {
            departureTime
        }

        return when (serviceCategory) {
            ServiceCategory.PREMIUM -> baseDepartureTime
            ServiceCategory.STANDARD -> {
                if (isWeekend) {
                    baseDepartureTime.toJavaLocalDateTime().plusHours(2).toKotlinLocalDateTime()
                } else {
                    baseDepartureTime
                }
            }
            ServiceCategory.ECONOMY -> {
                if (isWeekend) {
                    baseDepartureTime.toJavaLocalDateTime().plusHours(4).toKotlinLocalDateTime()
                } else {
                    baseDepartureTime.toJavaLocalDateTime().plusHours(2).toKotlinLocalDateTime()
                }
            }
            else -> null
        }
    }

    private fun predictFlexibleDeparture(
        bookings: Int,
        totalSeats: Int,
        historicalData: List<LocalDateTime>
    ): LocalDateTime? {
        // Simple prediction: if full, depart now; else estimate based on historical average
        if (bookings >= totalSeats && historicalData.isNotEmpty()) {
            return LocalDateTime.now() // Depart immediately
        }
        if (historicalData.isNotEmpty()) {
            val avgHour = historicalData.map { it.hour }.average().toInt()
            val avgMinute = historicalData.map { it.minute }.average().toInt()
            val today = LocalDateTime.now().date
            return LocalDateTime(today.year, today.month, today.dayOfMonth, avgHour, avgMinute)
        }
        return null
    }

    override fun iterator(): Iterator<JourneyPlan> {
        val today = LocalDateTime.now()
        val journeyPlans = mutableListOf<JourneyPlan>()

        // Case 1: Departure within the next 2 hours
        val twoHoursFromNow = today.plusHours(2)
        if (scheduledTime.isAfter(today) && scheduledTime.isBefore(twoHoursFromNow)) {
            val journeyPlan = JourneyPlan(
                departureTime = scheduledTime,
                transportMode = "Train",
                estimatedArrival = scheduledTime.plusHours(1)
            )
            journeyPlans.add(journeyPlan)
        }

        // Case 2: Departure later today
        if (scheduledTime.isAfter(twoHoursFromNow) && scheduledTime.toLocalDate().isEqual(today.toLocalDate())) {
            val journeyPlan = JourneyPlan(
                departureTime = scheduledTime,
                transportMode = "Bus",
                estimatedArrival = scheduledTime.plusHours(2)
            )
            journeyPlans.add(journeyPlan)
        }

        // Case 3: Departure on a future date
        if (scheduledTime.toLocalDate().isAfter(today.toLocalDate())) {
            val journeyPlan = JourneyPlan(
                departureTime = scheduledTime,
                transportMode = "Plane",
                estimatedArrival = scheduledTime.plusHours(8)
            )
            journeyPlans.add(journeyPlan)
        }

        return journeyPlans.iterator()
    }
}