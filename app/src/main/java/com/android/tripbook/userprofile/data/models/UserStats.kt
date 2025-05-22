package com.android.tripbook.userprofile.data.models

data class UserStats(
    val tripsBooked: Int,
    val reviewsGiven: Int,
    val favoriteDestinations: List<String>,
    val accountCreated: String
)
