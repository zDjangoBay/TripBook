package com.android.tripbook.data.model

/**
 * Data class representing the request body for scheduling a trip.
 * Placeholder fields - replace with actual required fields.
 */
data class TripScheduleRequest(
    val destination: String, // Example field
    val startDate: String,   // Example field (consider using Date/Timestamp)
    val endDate: String,     // Example field
    val notes: String? = null // Example optional field
)
