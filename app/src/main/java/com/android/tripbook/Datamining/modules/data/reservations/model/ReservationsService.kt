package com.android.reservations.model

interface ReservationsService {
    suspend fun createReservation(userId: String, request: CreateReservationRequest): Reservation?
    
    suspend fun getReservationById(reservationId: String, userId: String): Reservation?
    
    
    suspend fun getUserReservations(userId: String, filter: ReservationFilter?, page: Int = 1, pageSize: Int = 20): List<Reservation>
          
    suspend fun updateReservation(reservationId: String, userId: String, request: UpdateReservationRequest): Reservation?
    
        suspend fun updateReservationStatus(reservationId: String, userId: String, newStatus: ReservationStatus): Reservation?
    suspend fun deleteReservation(reservationId: String, userId: String): Boolean
}
