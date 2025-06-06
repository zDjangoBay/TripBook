package com.tripbook.data.model

data class UserLocation(
    val uid: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
