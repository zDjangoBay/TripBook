package com.tripscheduler.data

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class ItineraryItem(
    val id: String = UUID.randomUUID().toString(),
    val tripId: String, // Reference to the Trip
    val activity: String,
    val startTime: Instant,
    val endTime: Instant,
    val locationId: String // Reference to the Location
)