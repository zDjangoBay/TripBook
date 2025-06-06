package com.android.tripbook.model

data class Trip(
    val name: String,
    val startDate: String = "",
    val destination: String = "",
    val travelers: Int = 1,
    val budget: Int = 0,
    val status: TripStatus = TripStatus.PLANNED,
    val time: String = ""
)