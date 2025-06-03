package com.android.tripbook.data.repositories

import com.android.tripbook.data.database.TripBookDatabase
import com.android.tripbook.data.database.entities.ReservationEntity
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * Repository for managing reservations data
 */
class ReservationRepository(
    private val database: TripBookDatabase
) {
    
    /**
     * Get all reservations for a specific user
     */
    fun getUserReservations(userId: String): Flow<List<Reservation>> {
        return database.reservationDao().getUserReservations(userId).map { entities ->
            entities.map { entity -> entity.toReservation() }
        }
    }

    /**
     * Get reservations by status for a specific user
     */
    fun getUserReservationsByStatus(userId: String, status: String): Flow<List<Reservation>> {
        return database.reservationDao().getReservationsByStatus(userId, status).map { entities ->
            entities.map { entity -> entity.toReservation() }
        }
    }
    
    /**
     * Get upcoming reservations for a user
     */
    fun getUpcomingReservations(userId: String): Flow<List<Reservation>> {
        return getUserReservationsByStatus(userId, "PENDING").map { reservations ->
            reservations.filter { it.startDate.isAfter(LocalDateTime.now()) }
        }
    }
    
    /**
     * Get completed reservations for a user
     */
    fun getCompletedReservations(userId: String): Flow<List<Reservation>> {
        return getUserReservationsByStatus(userId, "COMPLETED")
    }
    
    /**
     * Insert a new reservation
     */
    suspend fun insertReservation(reservation: ReservationEntity) {
        database.reservationDao().insertReservation(reservation)
    }
    
    /**
     * Update reservation status
     */
    suspend fun updateReservationStatus(reservationId: String, status: String) {
        database.reservationDao().updateReservationStatus(reservationId, status)
    }
    
    /**
     * Delete a reservation
     */
    suspend fun deleteReservation(reservationId: String) {
        val reservation = database.reservationDao().getReservationById(reservationId)
        reservation?.let {
            database.reservationDao().deleteReservation(it)
        }
    }
}

/**
 * Extension function to convert ReservationEntity to Reservation
 */
private fun ReservationEntity.toReservation(): Reservation {
    return Reservation(
        id = this.id,
        title = "Trip Reservation", // You might want to get this from trip data
        destination = "Destination", // You might want to get this from trip data
        startDate = this.bookingDate,
        endDate = this.bookingDate.plusDays(this.hotelNights.toLong()),
        status = when (this.status) {
            "PENDING" -> ReservationStatus.PENDING
            "CONFIRMED" -> ReservationStatus.CONFIRMED
            "CANCELLED" -> ReservationStatus.CANCELLED
            "COMPLETED" -> ReservationStatus.COMPLETED
            else -> ReservationStatus.PENDING
        },
        imageUrl = null,
        price = this.totalCost,
        currency = "USD",
        bookingReference = this.id.takeLast(6).uppercase(),
        notes = this.notes,
        accommodationName = null, // You might want to get this from hotel data
        accommodationAddress = null,
        transportInfo = null // You might want to get this from transport data
    )
}
