package com.android.tripbook.shared.model

import kotlinx.serialization.Serializable

/**
 * Represents a geographical location with coordinates and address information.
 */
@Serializable
data class Location(
    val name: String,
    val city: String = "",
    val country: String = "",
    val coordinates: Coordinates? = null,
    val placeId: String? = null, // For Google Places API or similar
    val address: String = ""
) {
    /**
     * Returns a formatted display name for the location.
     */
    fun getDisplayName(): String {
        val parts = mutableListOf<String>()
        parts.add(name)

        if (city.isNotEmpty() && city.lowercase() != name.lowercase()) {
            parts.add(city)
        }

        if (country.isNotEmpty()) {
            parts.add(country)
        }

        return parts.joinToString(", ")
    }

    /**
     * Returns true if this location has valid coordinates.
     */
    fun hasCoordinates(): Boolean = coordinates != null
}

/**
 * Represents geographical coordinates (latitude and longitude).
 */
@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
) {
    /**
     * Validates that the coordinates are within valid ranges.
     */
    fun isValid(): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
}
