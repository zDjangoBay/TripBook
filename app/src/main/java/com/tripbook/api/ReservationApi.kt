package com.tripbook.api

import retrofit2.http.*

data class Trip(
    val id: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Double,
    val availableSeats: Int
)

data class Reservation(
    val id: String,
    val tripId: String,
    val userId: String,
    val seats: List<Seat>,
    val totalPrice: Double,
    val status: ReservationStatus,
    val createdAt: String
)

data class Seat(
    val id: String,
    val number: String,
    val row: Int,
    val column: Int,
    val price: Double
)

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    REFUNDED
}

data class PaymentRequest(
    val reservationId: String,
    val amount: Double,
    val paymentMethod: String,
    val cardDetails: CardDetails
)

data class CardDetails(
    val number: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String,
    val cardholderName: String
)

data class CancellationRequest(
    val reservationId: String,
    val reason: String
)

interface ReservationApi {
    @GET("trips")
    suspend fun getTrips(): List<Trip>

    @GET("trips/{tripId}")
    suspend fun getTripDetails(@Path("tripId") tripId: String): Trip

    @GET("trips/{tripId}/seats")
    suspend fun getAvailableSeats(@Path("tripId") tripId: String): List<Seat>

    @POST("reservations")
    suspend fun createReservation(
        @Body reservation: Reservation
    ): Reservation

    @GET("reservations/{reservationId}")
    suspend fun getReservationDetails(
        @Path("reservationId") reservationId: String
    ): Reservation

    @POST("payments")
    suspend fun processPayment(
        @Body paymentRequest: PaymentRequest
    ): Reservation

    @POST("reservations/{reservationId}/cancel")
    suspend fun cancelReservation(
        @Path("reservationId") reservationId: String,
        @Body request: CancellationRequest
    ): Reservation

    @GET("reservations/user/{userId}")
    suspend fun getUserReservations(
        @Path("userId") userId: String
    ): List<Reservation>
} 