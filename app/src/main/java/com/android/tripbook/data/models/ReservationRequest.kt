package com.android.tripbook.data.models

import java.util.Date

data class ReservationRequest(
    val userId: String,
    val type: ReservationType,
    val title: String,
    val description: String? = null,
    val location: String,
    val checkInDate: Date,
    val checkOutDate: Date? = null,
    val guestCount: Int = 1,
    val totalPrice: Double,
    val currency: String = "XAF",
    val providerName: String,
    val providerContact: String? = null,
    val specialRequests: String? = null
) {
    fun toReservation(): Reservation {
        return Reservation(
            userId = userId,
            type = type,
            title = title,
            description = description,
            location = location,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            guestCount = guestCount,
            totalPrice = totalPrice,
            currency = currency,
            providerName = providerName,
            providerContact = providerContact,
            specialRequests = specialRequests
        )
    }
}