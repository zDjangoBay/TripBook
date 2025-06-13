package com.android.tripbook.model

import java.time.LocalDate

data class Booking(
    val tripId: Int,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val adultCount: Int = 1,
    val childCount: Int = 0,
    val contactName: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val specialRequirements: String = "",
    val selectedOptions: List<TripOption> = emptyList(),
    val agreedToTerms: Boolean = false,
    val agencyId: Int? = null,
    val departureTime: String = "" //no specification yet since each agency has its own time
)

data class TripOption(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val isSelected: Boolean = false
)

enum class BookingStep {
    DATE_SELECTION,
    AGENCY_SELECTION,
    TRAVELER_INFO,
    ADDITIONAL_OPTIONS,
    SUMMARY
}