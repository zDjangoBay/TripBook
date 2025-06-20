package com.android.tripbook.domain.usecases

import com.android.tripbook.data.models.Reservation
import com.android.tripbook.data.models.ReservationRequest
import com.android.tripbook.data.models.ReservationStatus
import com.android.tripbook.data.models.ReservationType
import com.android.tripbook.data.repositories.ReservationRepository
import kotlinx.coroutines.flow.Flow

class ReservationManager(
    private val reservationRepository: ReservationRepository,
    private val createReservationUseCase: CreateReservationUseCase
) {
    
    fun getAllReservations(): Flow<List<Reservation>> {
        return reservationRepository.getAllReservations()
    }
    
    fun getUserReservations(userId: String): Flow<List<Reservation>> {
        return reservationRepository.getReservationsByUser(userId)
    }
    
    suspend fun getReservationById(reservationId: String): Reservation? {
        return reservationRepository.getReservationById(reservationId)
    }
    
    fun getReservationsByStatus(status: ReservationStatus): Flow<List<Reservation>> {
        return reservationRepository.getReservationsByStatus(status)
    }
    
    fun getReservationsByType(type: ReservationType): Flow<List<Reservation>> {
        return reservationRepository.getReservationsByType(type)
    }
    
    fun getUserReservationsByStatus(userId: String, status: ReservationStatus): Flow<List<Reservation>> {
        return reservationRepository.getUserReservationsByStatus(userId, status)
    }
    
    suspend fun createReservation(request: ReservationRequest): Result<Reservation> {
        return createReservationUseCase(request)
    }
    
    suspend fun updateReservation(reservation: Reservation): Result<Unit> {
        return reservationRepository.updateReservation(reservation)
    }
    
    suspend fun cancelReservation(reservationId: String): Result<Unit> {
        return try {
            val reservation = reservationRepository.getReservationById(reservationId)
            if (reservation == null) {
                Result.failure(IllegalArgumentException("Reservation not found"))
            } else {
                when (reservation.status) {
                    ReservationStatus.CONFIRMED, ReservationStatus.PENDING -> {
                        reservationRepository.cancelReservation(reservationId)
                    }
                    ReservationStatus.CANCELLED -> {
                        Result.failure(IllegalStateException("Reservation is already cancelled"))
                    }
                    ReservationStatus.COMPLETED -> {
                        Result.failure(IllegalStateException("Cannot cancel completed reservation"))
                    }
                    ReservationStatus.EXPIRED -> {
                        Result.failure(IllegalStateException("Cannot cancel expired reservation"))
                    }
                    ReservationStatus.PROCESSING_PAYMENT -> {
                        Result.failure(IllegalStateException("Cannot cancel reservation while processing payment"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun confirmReservation(reservationId: String): Result<Unit> {
        return try {
            val reservation = reservationRepository.getReservationById(reservationId)
            if (reservation == null) {
                Result.failure(IllegalArgumentException("Reservation not found"))
            } else {
                when (reservation.status) {
                    ReservationStatus.PENDING, ReservationStatus.PROCESSING_PAYMENT -> {
                        reservationRepository.confirmReservation(reservationId)
                    }
                    ReservationStatus.CONFIRMED -> {
                        Result.failure(IllegalStateException("Reservation is already confirmed"))
                    }
                    ReservationStatus.CANCELLED -> {
                        Result.failure(IllegalStateException("Cannot confirm cancelled reservation"))
                    }
                    ReservationStatus.COMPLETED -> {
                        Result.failure(IllegalStateException("Reservation is already completed"))
                    }
                    ReservationStatus.EXPIRED -> {
                        Result.failure(IllegalStateException("Cannot confirm expired reservation"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun completeReservation(reservationId: String): Result<Unit> {
        return try {
            val reservation = reservationRepository.getReservationById(reservationId)
            if (reservation == null) {
                Result.failure(IllegalArgumentException("Reservation not found"))
            } else {
                when (reservation.status) {
                    ReservationStatus.CONFIRMED -> {
                        reservationRepository.updateReservationStatus(reservationId, ReservationStatus.COMPLETED)
                    }
                    ReservationStatus.COMPLETED -> {
                        Result.failure(IllegalStateException("Reservation is already completed"))
                    }
                    else -> {
                        Result.failure(IllegalStateException("Can only complete confirmed reservations"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteReservation(reservation: Reservation): Result<Unit> {
        return reservationRepository.deleteReservation(reservation)
    }
}