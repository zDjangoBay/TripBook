package com.android.tripbook.model

import java.time.LocalDateTime

data class Bus(
    val busId: Int,
    val busName: String,
    val timeOfDeparture: LocalDateTime,
    val agencyId: Int,
    val destinationId: Int,
    // Additional fields for UI display
    val destinationName: String = "",
    val agencyName: String = ""
)
