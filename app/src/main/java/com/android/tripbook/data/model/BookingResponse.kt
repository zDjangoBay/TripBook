package com.android.tripbook.data.model


import com.google.gson.annotations.SerializedName

/**
 * Represents the response received from the backend after a booking request is made.
 * It provides confirmation details and the status of the booking.
 */
data class BookingResponses(
    @SerializedName("bookingId") val bookingId: String, // Unique ID for the confirmed booking
    @SerializedName("status") val status: String,       // Current status of the booking (e.g., "pending", "confirmed", "failed", "cancelled")
    @SerializedName("message") val message: String?,    // An optional human-readable message about the booking outcome
    @SerializedName("confirmationUrl") val confirmationUrl: String? = null // Optional: URL to a booking confirmation page/document
)
