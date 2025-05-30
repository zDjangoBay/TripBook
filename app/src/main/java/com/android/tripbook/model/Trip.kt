package com.android.tripbook.model

import java.time.LocalDate

enum class  TripStatus{
    PLANNED,ACTIVE,COMPLETED
}


class Trip (
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: TripStatus,
    val type: String = ""
)