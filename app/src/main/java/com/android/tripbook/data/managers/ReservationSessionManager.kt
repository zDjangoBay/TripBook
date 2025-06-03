package com.android.tripbook.data.managers

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.android.tripbook.data.models.*
import com.android.tripbook.data.providers.DummyTripDataProvider
import com.android.tripbook.data.database.TripBookDatabase
import com.android.tripbook.data.database.entities.ReservationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context

/**
 * Manages the current reservation session state
 */
class ReservationSessionManager private constructor(
    private val context: Context,
    private val database: TripBookDatabase,
    private val userSessionManager: UserSessionManager
) {

    private val _currentSession = MutableStateFlow<ReservationSession?>(null)
    val currentSession: StateFlow<ReservationSession?> = _currentSession.asStateFlow()

    private val _reservations = MutableStateFlow<List<TripReservation>>(emptyList())
    val reservations: StateFlow<List<TripReservation>> = _reservations.asStateFlow()

    fun startNewSession(tripId: String) {
        _currentSession.value = ReservationSession(tripId = tripId)
    }

    fun selectTransport(transport: TransportOption) {
        _currentSession.value?.let { session ->
            _currentSession.value = session.copy(
                selectedTransport = transport,
                totalCost = calculateTotalCost(session.copy(selectedTransport = transport))
            )
        }
    }

    fun selectHotel(hotel: HotelOption, nights: Int) {
        _currentSession.value?.let { session ->
            _currentSession.value = session.copy(
                selectedHotel = hotel,
                hotelNights = nights,
                totalCost = calculateTotalCost(session.copy(selectedHotel = hotel, hotelNights = nights))
            )
        }
    }

    fun addActivity(activity: ActivityOption) {
        _currentSession.value?.let { session ->
            val updatedActivities = session.selectedActivities + activity
            _currentSession.value = session.copy(
                selectedActivities = updatedActivities,
                totalCost = calculateTotalCost(session.copy(selectedActivities = updatedActivities))
            )
        }
    }

    fun removeActivity(activity: ActivityOption) {
        _currentSession.value?.let { session ->
            val updatedActivities = session.selectedActivities - activity
            _currentSession.value = session.copy(
                selectedActivities = updatedActivities,
                totalCost = calculateTotalCost(session.copy(selectedActivities = updatedActivities))
            )
        }
    }

    fun clearSession() {
        _currentSession.value = null
    }

    private fun calculateTotalCost(session: ReservationSession): Double {
        var total = 0.0

        // Add transport cost
        session.selectedTransport?.let { total += it.price }

        // Add hotel cost
        session.selectedHotel?.let { hotel ->
            total += hotel.pricePerNight * session.hotelNights
        }

        // Add activities cost
        total += session.selectedActivities.sumOf { it.price }

        return total
    }

    fun completeReservation(): TripReservation? {
        val session = _currentSession.value ?: return null
        val transport = session.selectedTransport ?: return null
        val currentUserId = userSessionManager.getCurrentUserId() ?: return null

        val reservation = TripReservation(
            id = "reservation_${System.currentTimeMillis()}",
            trip = DummyTripDataProvider.getTripById(session.tripId) ?: return null,
            transport = transport,
            hotel = session.selectedHotel,
            hotelNights = session.hotelNights,
            activities = session.selectedActivities,
            totalCost = session.totalCost,
            status = ReservationStatus.PENDING,
            bookingDate = java.time.LocalDateTime.now(),
            paymentStatus = PaymentStatus.PENDING
        )

        // Save to database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reservationEntity = ReservationEntity(
                    id = reservation.id,
                    userId = currentUserId,
                    tripId = session.tripId,
                    transportId = transport.id,
                    hotelId = session.selectedHotel?.id,
                    hotelNights = session.hotelNights,
                    totalCost = session.totalCost,
                    status = reservation.status.name,
                    paymentStatus = reservation.paymentStatus.name,
                    bookingDate = reservation.bookingDate,
                    notes = "Reservation created via app"
                )

                database.reservationDao().insertReservation(reservationEntity)

                // Update in-memory list
                _reservations.value = _reservations.value + reservation
            } catch (e: Exception) {
                // Handle error - could show toast or log
                android.util.Log.e("ReservationManager", "Failed to save reservation", e)
            }
        }

        // Clear current session
        clearSession()

        return reservation
    }

    fun updateReservationStatus(reservationId: String, status: ReservationStatus) {
        _reservations.value = _reservations.value.map { reservation ->
            if (reservation.id == reservationId) {
                reservation.copy(status = status)
            } else {
                reservation
            }
        }
    }

    fun updatePaymentStatus(reservationId: String, paymentStatus: PaymentStatus) {
        _reservations.value = _reservations.value.map { reservation ->
            if (reservation.id == reservationId) {
                reservation.copy(paymentStatus = paymentStatus)
            } else {
                reservation
            }
        }
    }

    fun getReservationsByStatus(status: ReservationStatus): List<TripReservation> {
        return _reservations.value.filter { it.status == status }
    }

    companion object {
        @Volatile
        private var INSTANCE: ReservationSessionManager? = null

        fun getInstance(
            context: Context,
            database: TripBookDatabase,
            userSessionManager: UserSessionManager
        ): ReservationSessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReservationSessionManager(
                    context.applicationContext,
                    database,
                    userSessionManager
                ).also { INSTANCE = it }
            }
        }
    }
}
