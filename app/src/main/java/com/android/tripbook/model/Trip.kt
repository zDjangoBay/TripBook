package com.android.tripbook.model

import java.time.LocalDate
import java.time.LocalTime

data class Trip(
    val name: String,
    val startDate: String = "",
    val destination: String = "",
    val travelers: Int = 1,
    val budget: Int = 0,
    val status: TripStatus = TripStatus.PLANNED,
    val time: String = ""
)

data class Activity(
    val date: LocalDate,
    val time: LocalTime,
    val title: String,
    val location: String,
    val description: String = ""
)