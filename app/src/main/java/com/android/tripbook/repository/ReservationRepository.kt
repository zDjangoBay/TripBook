package com.android.tripbook.repository

import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationFilter
import com.android.tripbook.model.ReservationStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repository class that provides access to reservation data
 */
class ReservationRepository {

    // Sample data for testing
    private var sampleReservations = listOf(
        Reservation(
            title = "Beach Getaway",
            destination = "Zanzibar, Tanzania",
            startDate = LocalDateTime.now().plusDays(15),
            endDate = LocalDateTime.now().plusDays(22),
            status = ReservationStatus.CONFIRMED,
            price = 1200.0,
            bookingReference = "ZNZ12345",
            accommodationName = "Paradise Beach Resort",
            accommodationAddress = "Nungwi Beach, Zanzibar",
            transportInfo = "Flight TZ789 from Dar es Salaam"
        ),
        Reservation(
            title = "Safari Adventure",
            destination = "Serengeti National Park, Tanzania",
            startDate = LocalDateTime.now().plusDays(45),
            endDate = LocalDateTime.now().plusDays(52),
            status = ReservationStatus.PENDING,
            price = 2500.0,
            bookingReference = "SER98765",
            accommodationName = "Serengeti Luxury Camp",
            transportInfo = "Jeep transfer from Arusha"
        ),
        Reservation(
            title = "City Break",
            destination = "Cape Town, South Africa",
            startDate = LocalDateTime.now().minusDays(60),
            endDate = LocalDateTime.now().minusDays(55),
            status = ReservationStatus.COMPLETED,
            price = 950.0,
            bookingReference = "CPT45678",
            accommodationName = "Waterfront Hotel",
            accommodationAddress = "V&A Waterfront, Cape Town"
        ),
        Reservation(
            title = "Mountain Retreat",
            destination = "Atlas Mountains, Morocco",
            startDate = LocalDateTime.now().minusDays(10),
            endDate = LocalDateTime.now().plusDays(2),
            status = ReservationStatus.CONFIRMED,
            price = 1100.0,
            bookingReference = "ATL34567",
            accommodationName = "Mountain View Lodge",
            accommodationAddress = "Imlil Valley, Atlas Mountains"
        ),
        Reservation(
            title = "Desert Experience",
            destination = "Sahara Desert, Morocco",
            startDate = LocalDateTime.now().plusDays(5),
            endDate = LocalDateTime.now().plusDays(8),
            status = ReservationStatus.CONFIRMED,
            price = 800.0,
            bookingReference = "SAH23456",
            accommodationName = "Desert Luxury Camp",
            transportInfo = "4x4 transfer from Marrakech"
        ),
        Reservation(
            title = "Island Hopping",
            destination = "Seychelles",
            startDate = LocalDateTime.now().minusDays(120),
            endDate = LocalDateTime.now().minusDays(110),
            status = ReservationStatus.COMPLETED,
            price = 3200.0,
            bookingReference = "SEY87654",
            accommodationName = "Various Island Resorts",
            transportInfo = "Inter-island ferries"
        ),
        Reservation(
            title = "Cultural Tour",
            destination = "Cairo, Egypt",
            startDate = LocalDateTime.now().minusDays(30),
            endDate = LocalDateTime.now().minusDays(25),
            status = ReservationStatus.CANCELLED,
            price = 1500.0,
            bookingReference = "CAI65432",
            accommodationName = "Nile View Hotel",
            accommodationAddress = "Downtown Cairo",
            notes = "Cancelled due to scheduling conflict"
        )
    )

    // StateFlow to observe reservations
    private val _reservations = MutableStateFlow<List<Reservation>>(sampleReservations)
    val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    // StateFlow to observe the current filter
    private val _currentFilter = MutableStateFlow(ReservationFilter.empty())
    val currentFilter: StateFlow<ReservationFilter> = _currentFilter.asStateFlow()

    /**
     * Get all reservations for the current user
     */
    fun getAllReservations(): List<Reservation> {
        return sampleReservations
    }

    /**
     * Get upcoming reservations (start date is in the future)
     */
    fun getUpcomingReservations(): List<Reservation> {
        return ReservationFilter.upcoming().apply(sampleReservations)
    }

    /**
     * Get past reservations (end date is in the past)
     */
    fun getPastReservations(): List<Reservation> {
        return ReservationFilter.past().apply(sampleReservations)
    }

    /**
     * Get reservations for a specific month
     */
    fun getReservationsForMonth(year: Int, month: Int): List<Reservation> {
        val startOfMonth = LocalDateTime.of(year, month, 1, 0, 0)
        val endOfMonth = startOfMonth.plusMonths(1).minusNanos(1)

        return sampleReservations.filter { reservation ->
            (reservation.startDate.isAfter(startOfMonth) && reservation.startDate.isBefore(endOfMonth)) ||
            (reservation.endDate.isAfter(startOfMonth) && reservation.endDate.isBefore(endOfMonth)) ||
            (reservation.startDate.isBefore(startOfMonth) && reservation.endDate.isAfter(endOfMonth))
        }
    }

    /**
     * Apply a filter to the reservations
     */
    fun applyFilter(filter: ReservationFilter) {
        _currentFilter.update { filter }
        _reservations.update { filter.apply(sampleReservations) }
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _currentFilter.update { ReservationFilter.empty() }
        _reservations.update { sampleReservations }
    }

    /**
     * Add a new reservation
     */
    fun addReservation(reservation: Reservation) {
        val updatedList = sampleReservations.toMutableList().apply {
            add(reservation)
        }
        sampleReservations = updatedList
        _reservations.update { _currentFilter.value.apply(sampleReservations) }
    }

    /**
     * Update an existing reservation
     */
    fun updateReservation(reservation: Reservation) {
        val updatedList = sampleReservations.toMutableList().apply {
            val index = indexOfFirst { it.id == reservation.id }
            if (index != -1) {
                set(index, reservation)
            }
        }
        sampleReservations = updatedList
        _reservations.update { _currentFilter.value.apply(sampleReservations) }
    }

    /**
     * Delete a reservation
     */
    fun deleteReservation(reservationId: String) {
        val updatedList = sampleReservations.toMutableList().apply {
            removeIf { it.id == reservationId }
        }
        sampleReservations = updatedList
        _reservations.update { _currentFilter.value.apply(sampleReservations) }
    }


}
