package com.android.tripbook.tripscheduling.GhislainChe.domain.interfaces

interface UserProfileInterface {
    suspend fun getUserPreferences(userId: String): UserTravelPreferences
    suspend fun getUserBookingHistory(userId: String): List<BookingHistory>
}

// Placeholder data classes
data class UserTravelPreferences(
    val preferredService: String,
    val frequentRoutes: List<String>
)

data class BookingHistory(
    val bookingId: String,
    val route: String,
    val date: String,
    val status: String
)