package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.managers

import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.models.*
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.providers.DummyTripDataProvider
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.reservation.HotelOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the current reservation session state
 */
class ReservationSessionManager {

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

        // Add to reservations list
        _reservations.value = _reservations.value + reservation

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

        fun getInstance(): ReservationSessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReservationSessionManager().also { INSTANCE = it }
            }
        }
    }
}
