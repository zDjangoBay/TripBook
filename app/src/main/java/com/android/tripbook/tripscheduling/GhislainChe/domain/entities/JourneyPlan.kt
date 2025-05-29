package com.android.tripbook.tripscheduling.GhislainChe.domain.entities

import kotlinx.datetime.Duration

data class JourneyPlan(
    val routeId: String,
    val segments: List<RouteSegment>,
    val accommodationOptions: List<AccommodationStop>,
    val checkpoints: List<SecurityCheckpoint>,
    val restStops: List<RestStop>,
    val totalDuration: Duration,
    val securityLevel: SecurityRating
)

data class RouteSegment(
    val segmentId: String,
    val origin: String,
    val destination: String,
    val duration: Duration
)

data class AccommodationStop(
    val name: String,
    val rating: Double,
    val contactInfo: String
)

data class SecurityCheckpoint(
    val name: String,
    val location: String
)

data class RestStop(
    val name: String,
    val duration: Duration
)

enum class SecurityRating {
    LOW, MEDIUM, HIGH
}