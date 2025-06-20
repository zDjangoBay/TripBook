package com.android.tripbook.data.dao

import androidx.room.*
import com.android.tripbook.data.models.Reservation
import com.android.tripbook.data.models.ReservationStatus
import com.android.tripbook.data.models.ReservationType
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    
    @Query("SELECT * FROM reservations ORDER BY createdAt DESC")
    fun getAllReservations(): Flow<List<Reservation>>
    
    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY createdAt DESC")
    fun getReservationsByUser(userId: String): Flow<List<Reservation>>
    
    @Query("SELECT * FROM reservations WHERE id = :reservationId")
    suspend fun getReservationById(reservationId: String): Reservation?
    
    @Query("SELECT * FROM reservations WHERE status = :status ORDER BY createdAt DESC")
    fun getReservationsByStatus(status: ReservationStatus): Flow<List<Reservation>>
    
    @Query("SELECT * FROM reservations WHERE type = :type ORDER BY createdAt DESC")
    fun getReservationsByType(type: ReservationType): Flow<List<Reservation>>
    
    @Query("SELECT * FROM reservations WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    fun getUserReservationsByStatus(userId: String, status: ReservationStatus): Flow<List<Reservation>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation): Long
    
    @Update
    suspend fun updateReservation(reservation: Reservation)
    
    @Delete
    suspend fun deleteReservation(reservation: Reservation)
    
    @Query("UPDATE reservations SET status = :status, updatedAt = :updatedAt WHERE id = :reservationId")
    suspend fun updateReservationStatus(reservationId: String, status: ReservationStatus, updatedAt: Long)
    
    @Query("DELETE FROM reservations WHERE userId = :userId")
    suspend fun deleteUserReservations(userId: String)
}