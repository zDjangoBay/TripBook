package com.android.tripbook.model

/**
 * Data class representing a Travel Agency.
 * Placeholder fields - adjust based on actual API response.
 */
data class TravelAgency(
    val id: String,
    val name: String,
    val rating: Float? = null, // e.g., 4.5
    val description: String? = null,
    val servicesOffered: List<String>? = null, // e.g., ["Flights", "Tours", "Accommodation"]
    val imageUrl: String? = null,
    val contactEmail: String? = null,
    val phoneNumber: String? = null
)
