package com.android.tripbook.reservation.data

import android.util.Log
import com.android.tripbook.reservation.model.*
import com.android.tripbook.utils.DebugHelper
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repository for managing reservation data
 */
class ReservationRepository {
    companion object {
        private const val TAG = "ReservationRepository"
    }

    // Current logged-in user ID (dummy value for now)
    private val currentUserId = "user123"

    init {
        Log.d(TAG, "Initializing ReservationRepository")
    }

    /**
     * Get all reservations for the current user
     */
    fun getUserReservations(): List<Reservation> {
        DebugHelper.enterMethod("ReservationRepository", "getUserReservations")
        try {
            val reservations = dummyReservations.filter { it.userId == currentUserId }
            Log.d(TAG, "Found ${reservations.size} reservations for user $currentUserId")
            return reservations
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user reservations", e)
            throw e
        } finally {
            DebugHelper.exitMethod("ReservationRepository", "getUserReservations")
        }
    }

    /**
     * Get a specific reservation by ID
     */
    fun getReservationById(id: String): Reservation? {
        DebugHelper.enterMethod("ReservationRepository", "getReservationById")
        try {
            val reservation = dummyReservations.find { it.id == id }
            Log.d(TAG, "Reservation lookup for ID $id: ${if (reservation != null) "found" else "not found"}")
            return reservation
        } catch (e: Exception) {
            Log.e(TAG, "Error getting reservation by ID: $id", e)
            throw e
        } finally {
            DebugHelper.exitMethod("ReservationRepository", "getReservationById")
        }
    }

    /**
     * Create a new reservation
     */
    fun createReservation(reservation: Reservation): Reservation {
        DebugHelper.enterMethod("ReservationRepository", "createReservation")
        try {
            // In a real app, this would add to a database
            Log.d(TAG, "Created new reservation with ID: ${reservation.id}")
            return reservation
        } catch (e: Exception) {
            Log.e(TAG, "Error creating reservation", e)
            throw e
        } finally {
            DebugHelper.exitMethod("ReservationRepository", "createReservation")
        }
    }

    /**
     * Update the status of a reservation
     */
    fun updateReservationStatus(id: String, status: ReservationStatus): Reservation? {
        DebugHelper.enterMethod("ReservationRepository", "updateReservationStatus")
        try {
            val reservation = getReservationById(id) ?: return null
            // In a real app, this would update the database
            Log.d(TAG, "Updated reservation $id status to $status")
            return reservation.copy(status = status)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating reservation status", e)
            throw e
        } finally {
            DebugHelper.exitMethod("ReservationRepository", "updateReservationStatus")
        }
    }

    /**
     * Cancel a reservation
     */
    fun cancelReservation(id: String): Reservation? {
        DebugHelper.enterMethod("ReservationRepository", "cancelReservation")
        try {
            val result = updateReservationStatus(id, ReservationStatus.CANCELLED)
            Log.d(TAG, "Cancelled reservation $id: ${result != null}")
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling reservation", e)
            throw e
        } finally {
            DebugHelper.exitMethod("ReservationRepository", "cancelReservation")
        }
    }

    // Dummy data for testing
    private val dummyReservations = listOf(
        // Hotel reservation
        Reservation(
            id = "res1",
            userId = "user123",
            title = "Serengeti Safari Lodge",
            type = ReservationType.ACCOMMODATION,
            status = ReservationStatus.CONFIRMED,
            startDate = LocalDate.now().plusDays(30),
            endDate = LocalDate.now().plusDays(35),
            location = "Serengeti, Tanzania",
            price = "$1,250",
            description = "Luxury safari lodge with views of the Serengeti plains. Includes daily game drives and all meals.",
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070&auto=format&fit=crop"
        ),

        // Tour reservation
        Reservation(
            id = "res2",
            userId = "user123",
            title = "Zanzibar Spice Tour",
            type = ReservationType.TOUR,
            status = ReservationStatus.PENDING,
            startDate = LocalDate.now().plusDays(45),
            endDate = LocalDate.now().plusDays(45),
            location = "Stone Town, Zanzibar",
            price = "$75",
            description = "Half-day tour exploring the spice farms of Zanzibar with a local guide. Learn about the island's rich trading history.",
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=2070&auto=format&fit=crop"
        ),

        // Activity reservation
        Reservation(
            id = "res3",
            userId = "user123",
            title = "Mount Kilimanjaro Climb",
            type = ReservationType.ACTIVITY,
            status = ReservationStatus.COMPLETED,
            startDate = LocalDate.now().minusDays(60),
            endDate = LocalDate.now().minusDays(53),
            location = "Kilimanjaro, Tanzania",
            price = "$2,800",
            description = "7-day trek to the summit of Mount Kilimanjaro via the Machame Route. Includes guides, porters, and all meals.",
            imageUrl = "https://images.unsplash.com/photo-1572252009286-268acec5ca0a?q=80&w=2070&auto=format&fit=crop"
        ),

        // Cancelled reservation
        Reservation(
            id = "res4",
            userId = "user123",
            title = "Victoria Falls Helicopter Tour",
            type = ReservationType.ACTIVITY,
            status = ReservationStatus.CANCELLED,
            startDate = LocalDate.now().minusDays(10),
            endDate = LocalDate.now().minusDays(10),
            location = "Victoria Falls, Zimbabwe",
            price = "$350",
            description = "30-minute helicopter flight over Victoria Falls, offering spectacular aerial views of the falls and surrounding landscape.",
            imageUrl = "https://images.unsplash.com/photo-1577493340887-b7bfff550145?q=80&w=2070&auto=format&fit=crop"
        )
    )
}
