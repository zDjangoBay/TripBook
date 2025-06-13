package com.tripscheduler.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

// Helper for JSON serialization/deserialization, used across data classes
val AppJson = Json { prettyPrint = true; ignoreUnknownKeys = true }

@Serializable
data class Location(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)