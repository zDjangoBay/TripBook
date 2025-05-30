package com.android.tripbook.tripscheduling.GhislainChe.domain.usecases

import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.DepartureSchedule
import com.android.tripbook.tripscheduling.GhislainChe.domain.entities.ServiceCategory
import kotlinx.datetime.LocalDateTime

class CalculateDepartureTimeUseCase {
    fun execute(
        serviceCategory: ServiceCategory,
        bookings: Int,
        totalSeats: Int,
        scheduledTime: LocalDateTime?,
        historicalData: List<LocalDateTime> = emptyList()
    ): LocalDateTime? {
        return when (serviceCategory) {
            ServiceCategory.REGULAR -> predictFlexibleDeparture(bookings, totalSeats, historicalData)
            ServiceCategory.VIP -> scheduledTime // Semi-fixed
            ServiceCategory.PREMIUM -> scheduledTime // Fixed
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
            return LocalDateTime(today.year, today.monthNumber, today.dayOfMonth, avgHour, avgMinute)
        }
        return null
    }
}