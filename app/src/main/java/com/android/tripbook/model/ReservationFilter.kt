package com.android.tripbook.model

import java.time.LocalDateTime

/**
 * Data class representing filters for reservations
 */
data class ReservationFilter(
    val status: Set<ReservationStatus>? = null,
    val destination: String? = null,
    val startDateFrom: LocalDateTime? = null,
    val startDateTo: LocalDateTime? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val searchQuery: String? = null
) {
    /**
     * Apply the filter to a list of reservations
     */
    fun apply(reservations: List<Reservation>): List<Reservation> {
        return reservations.filter { reservation ->
            // Filter by status
            val statusMatch = status == null || status.isEmpty() || status.contains(reservation.status)
            
            // Filter by destination
            val destinationMatch = destination.isNullOrBlank() || 
                reservation.destination.contains(destination, ignoreCase = true)
            
            // Filter by start date range
            val startDateFromMatch = startDateFrom == null || 
                !reservation.startDate.isBefore(startDateFrom)
            val startDateToMatch = startDateTo == null || 
                !reservation.startDate.isAfter(startDateTo)
            
            // Filter by price range
            val priceMinMatch = priceMin == null || reservation.price >= priceMin
            val priceMaxMatch = priceMax == null || reservation.price <= priceMax
            
            // Filter by search query
            val searchMatch = searchQuery.isNullOrBlank() || 
                reservation.title.contains(searchQuery, ignoreCase = true) ||
                reservation.destination.contains(searchQuery, ignoreCase = true) ||
                reservation.accommodationName?.contains(searchQuery, ignoreCase = true) == true ||
                reservation.notes?.contains(searchQuery, ignoreCase = true) == true
            
            // All conditions must match
            statusMatch && destinationMatch && startDateFromMatch && 
                startDateToMatch && priceMinMatch && priceMaxMatch && searchMatch
        }
    }
    
    companion object {
        /**
         * Create an empty filter that doesn't filter anything
         */
        fun empty() = ReservationFilter()
        
        /**
         * Create a filter for upcoming reservations
         */
        fun upcoming() = ReservationFilter(
            startDateFrom = LocalDateTime.now()
        )
        
        /**
         * Create a filter for past reservations
         */
        fun past() = ReservationFilter(
            startDateTo = LocalDateTime.now()
        )
    }
}
