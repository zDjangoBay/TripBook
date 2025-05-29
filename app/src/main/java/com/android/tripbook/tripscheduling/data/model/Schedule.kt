package com.android.tripbook.tripscheduling.data.model

import java.time.LocalDateTime

data class Schedule(
    val id: String,
    val title: String,
    val dateTime: LocalDateTime,
    val lat: Double,
    val lng: Double
)

