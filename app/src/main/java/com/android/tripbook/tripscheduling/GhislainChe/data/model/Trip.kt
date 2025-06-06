package com.android.tripbook.tripscheduling.GhislainChe.data.model

import java.time.LocalDateTime

enum class ServiceTier {
    REGULAR,
    VIP,
    PREMIUM
}

data class Location(val name: String)

data class Checkpoint(
    val location: Location,
    val scheduledTime: LocalDateTime,
    val description: String = "",
    val durationMinutes: Int = 0 // For rest stops
)

data class Trip(
    val id: String,
    val origin: Location,
    val destination: Location,
    val serviceTier: ServiceTier,
    val departureTime: LocalDateTime?,  // Null for Regular service with flexible departure
    val isEstimatedTime: Boolean = true,
    val distance: Int, // in kilometers
    val totalSeats: Int,
    val filledSeats: Int,
    val checkpoints: List<Checkpoint> = emptyList(),
    val amenities: List<String> = emptyList(),
    val price: Double
) {
    val route: String get() = "${origin.name} → ${destination.name}"
    val seatsAvailable: Int get() = totalSeats - filledSeats
    val fillPercentage: Int get() = (filledSeats * 100 / totalSeats)
    
    // For display in the UI
    val serviceLabel: String get() = when(serviceTier) {
        ServiceTier.REGULAR -> "Regular"
        ServiceTier.VIP -> "VIP"
        ServiceTier.PREMIUM -> "Premium"
    }
    
    val departureTimeFormatted: String get() {
        return departureTime?.let {
            val prefix = if (isEstimatedTime) "Est. " else "Fixed: "
            "$prefix${it.hour}:${String.format("%02d", it.minute)}"
        } ?: "When full"
    }
    
    val seatsStatusFormatted: String get() = "$filledSeats/$totalSeats seats filled"
}