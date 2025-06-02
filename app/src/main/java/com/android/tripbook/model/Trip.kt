package com.android.tripbook.model

import java.time.LocalDate

enum class TripStatus {
    PLANNED, ACTIVE, COMPLETED
}

enum class ItineraryType {
    ACTIVITY, ACCOMMODATION, TRANSPORTATION
}

data class Trip(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: TripStatus,
    val type: String = "",
    val description: String = "",
    val activities: List<Activity> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val travelersList: List<Traveler> = emptyList(),
    val itinerary: List<ItineraryItem> = emptyList()
)

data class ItineraryItem(
    val date: LocalDate,
    val time: String,
    val title: String,
    val location: String,
    val type: ItineraryType,
    val notes: String = ""
)

data class Activity(
    val date: LocalDate,
    val time: String,
    val title: String,
    val location: String,
    val description: String = ""
)

data class Expense(
    val category: String,
    val description: String,
    val amount: Int
)

data class Traveler(
    val name: String,
    val isLeader: Boolean = false
)