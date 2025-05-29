package com.android.tripbook.data.model

/**
 * Data class representing the response body after scheduling a trip.
 * Placeholder fields - replace with actual response fields.
 */
data class TripScheduleResponse(
    val tripId: String,      // Example field: ID of the newly created trip
    val status: String,      // Example field: Confirmation status (e.g., "SCHEDULED")
    val message: String? = null // Example optional field for confirmation messages
)
