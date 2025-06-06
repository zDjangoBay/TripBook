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
) {
    companion object {
        fun default() = Trip(
            name = "",
            startDate = LocalDate.now().toString(),
            destination = "",
            travelers = 1,
            budget = 0,
            status = TripStatus.PLANNED,
            time = LocalTime.now().toString()
        )
    }

}