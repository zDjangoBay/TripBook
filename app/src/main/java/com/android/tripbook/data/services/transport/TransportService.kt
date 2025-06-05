package com.android.tripbook.data.services.transport

import com.android.tripbook.data.models.TransportOption
import com.android.tripbook.data.models.TransportType
import com.android.tripbook.data.services.ServiceResult
import java.time.LocalDateTime

/**
 * Service interface for transport-related operations
 * Provides methods to fetch and filter transport options
 */
interface TransportService {
    /**
     * Get all transport options for a specific trip
     */
    suspend fun getTransportOptionsForTrip(tripId: String): ServiceResult<List<TransportOption>>
    
    /**
     * Get transport options by type for a trip
     */
    suspend fun getTransportOptionsByType(tripId: String, transportType: TransportType): ServiceResult<List<TransportOption>>
    
    /**
     * Get a specific transport option by ID
     */
    suspend fun getTransportOptionById(transportId: String): ServiceResult<TransportOption>
    
    /**
     * Get available transport options (with capacity)
     */
    suspend fun getAvailableTransportOptions(tripId: String): ServiceResult<List<TransportOption>>
    
    /**
     * Get transport options within a price range
     */
    suspend fun getTransportOptionsByPriceRange(
        tripId: String, 
        minPrice: Double, 
        maxPrice: Double
    ): ServiceResult<List<TransportOption>>
    
    /**
     * Get transport options departing within a time range
     */
    suspend fun getTransportOptionsByTimeRange(
        tripId: String, 
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): ServiceResult<List<TransportOption>>
    
    /**
     * Get cheapest transport option for a trip
     */
    suspend fun getCheapestTransportOption(tripId: String): ServiceResult<TransportOption>
    
    /**
     * Get fastest transport option (shortest travel time)
     */
    suspend fun getFastestTransportOption(tripId: String): ServiceResult<TransportOption>
    
    /**
     * Get all unique transport types for a trip
     */
    suspend fun getAvailableTransportTypes(tripId: String): ServiceResult<List<TransportType>>
    
    /**
     * Check if transport option has available capacity
     */
    suspend fun hasAvailableCapacity(transportId: String): ServiceResult<Boolean>
}