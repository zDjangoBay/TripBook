package com.android.Tripbook.Datamining.modules.data.reservations.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime



@Serializable
data class CreateReservationRequest(
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val status: ReservationStatus = ReservationStatus.PENDING, // Default status
    val imageUrl: String? = null,
    val price: Double,
    val currency: String? = "FCFA",
    val bookingReference: String,
    val notes: String? = null,
    val accommodationName: String? = null,
    val accommodationAddress: String? = null,
    val transportInfo: String? = null,
    val User_id: String
    // The user_id is not suppose to stay in the request body , i know right , we are going to implement JWToken , and in the token's payload , we will fetch the user_id
)