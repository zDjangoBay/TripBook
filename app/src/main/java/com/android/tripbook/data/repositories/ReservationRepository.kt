package com.android.tripbook.data.repositories

import com.android.tripbook.data.models.Reservation
import com.android.tripbook.data.models.ReservationRequest
import com.android.tripbook.data.models.ReservationStatus
import com.android.tripbook.data.models.ReservationType
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    
    fun getAllReservations(): Flow<List<Reservation>>
    
    fun getReservationsByUser(userId: String): Flow<List<Reservation>>
    
    suspend fun getReservationById(reservationId: String): Reservation?
    
    fun getReservationsByStatus(status: ReservationStatus): Flow<List<Reservation>>
    
    fun getReservationsByType(type: ReservationType): Flow<List<Reservation>>
    
    fun getUserReservationsByStatus(userId: String, status: ReservationStatus): Flow<List<Reservation>>
    
    suspend fun createReservation(request: ReservationRequest): Result<Reservation>
    
    suspend fun updateReservation(reservation: Reservation): Result<Unit>
    
    suspend fun updateReservationStatus(reservationId: String, status: ReservationStatus): Result<Unit>
    
    suspend fun deleteReservation(reservation: Reservation): Result<Unit>
    
    suspend fun cancelReservation(reservationId: String): Result<Unit>
    
    suspend fun confirmReservation(reservationId: String): Result<Unit>
}