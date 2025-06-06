package com.android.tripbook.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the request payload sent to the backend to initiate a booking.
 */
data class BookingRequests(
    /**
     * The unique identifier of the travel agency the user is booking with.
     */
    @SerializedName("agencyId") val agencyId: String,

    /**
     * The unique identifier of the specific service being booked from the agency.
     * This might be a unique service ID from the backend, or for simpler cases,
     * it could temporarily be the service name as shown in the UI.
     */
    @SerializedName("serviceId") val serviceId: String,

    /**
     * The unique identifier of the user making the booking.
     * This would typically come from your authentication system.
     */
    @SerializedName("userId") val userId: String,

    /**
     * A flexible map to hold various details about the booking.
     * This allows for different types of booking information depending on the service.
     * Examples: traveler name, email, phone, preferred dates, number of travelers, etc.
     */
    @SerializedName("bookingDetails") val bookingDetails: Map<String, Any>
)