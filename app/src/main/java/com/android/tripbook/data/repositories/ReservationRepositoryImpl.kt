package com.android.tripbook.data.repositories

import com.android.tripbook.data.dao.ReservationDao
import com.android.tripbook.data.models.Reservation
import com.android.tripbook.data.models.ReservationRequest
import com.android.tripbook.data.models.ReservationStatus
import com.android.tripbook.data.models.ReservationType
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ReservationRepositoryImpl(
    private val reservationDao: ReservationDao
) : ReservationRepository {
    
    override fun getAllReservations(): Flow<List<Reservation>> {
        return reservationDao.getAllReservations()
    }
    
    override fun getReservationsByUser(userId: String): Flow<List<Reservation>> {
        return reservationDao.getReservationsByUser(userId)
    }
    
    override suspend fun getReservationById(reservationId: String): Reservation? {
        return reservationDao.getReservationById(reservationId)
    }
    
    override fun getReservationsByStatus(status: ReservationStatus): Flow<List<Reservation>> {
        return reservationDao.getReservationsByStatus(status)
    }
    
    override fun getReservationsByType(type: ReservationType): Flow<List<Reservation>> {
        return reservationDao.getReservationsByType(type)
    }
    
    override fun getUserReservationsByStatus(userId: String, status: ReservationStatus): Flow<List<Reservation>> {
        return reservationDao.getUserReservationsByStatus(userId, status)
    }
    
    override suspend fun createReservation(request: ReservationRequest): Result<Reservation> {
        return try {
            val reservation = request.toReservation()
            reservationDao.insertReservation(reservation)
            Result.success(reservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateReservation(reservation: Reservation): Result<Unit> {
        return try {
            val updatedReservation = reservation.copy(updatedAt = Date())
            reservationDao.updateReservation(updatedReservation)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateReservationStatus(reservationId: String, status: ReservationStatus): Result<Unit> {
        return try {
            reservationDao.updateReservationStatus(reservationId, status, Date().time)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteReservation(reservation: Reservation): Result<Unit> {
        return try {
            reservationDao.deleteReservation(reservation)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelReservation(reservationId: String): Result<Unit> {
        return updateReservationStatus(reservationId, ReservationStatus.CANCELLED)
    }
    
    override suspend fun confirmReservation(reservationId: String): Result<Unit> {
        return updateReservationStatus(reservationId, ReservationStatus.CONFIRMED)
    }
}