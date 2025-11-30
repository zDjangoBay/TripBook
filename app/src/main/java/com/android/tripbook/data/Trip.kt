package com.tripscheduler.data

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val startDate: Instant,
    val endDate: Instant,
    val originLocationId: String, // Reference to the origin Location
    val destinationLocationId: String, // Reference to the destination Location
    val userId: String, // Who organized/owns the trip
    val itineraryItemIds: List<String> = emptyList() // References to ItineraryItems
)