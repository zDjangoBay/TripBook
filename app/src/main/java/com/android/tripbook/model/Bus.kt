package com.android.tripbook.model

import java.time.OffsetDateTime

data class Bus(
    val busId: Int,
    val busName: String,
    val timeOfDeparture: OffsetDateTime, // Changed to OffsetDateTime
    val agencyId: Int,
    val destinationId: Int,
    val destinationName: String = "",
    val agencyName: String = ""
)
