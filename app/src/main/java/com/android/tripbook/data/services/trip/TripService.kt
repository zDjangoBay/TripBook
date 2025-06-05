package com.android.tripbook.data.services.trip

import com.android.tripbook.data.models.Trip
import com.android.tripbook.data.models.TripCategory
import com.android.tripbook.data.services.ServiceResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Service interface for trip-related operations
 * Provides methods to fetch and search trips
 */
interface TripService {
    /**
     * Get all available trips
     */
    suspend fun getAllTrips(): ServiceResult<List<Trip>>
    
    /**
     * Get trip details by ID
     */
    suspend fun getTripById(tripId: String): ServiceResult<Trip>
    
    /**
     * Search trips by query string (matches title, location, description)
     */
    suspend fun searchTrips(query: String): ServiceResult<List<Trip>>
    
    /**
     * Get trips by category
     */
    suspend fun getTripsByCategory(category: TripCategory): ServiceResult<List<Trip>>
    
    /**
     * Get trips within a price range
     */
    suspend fun getTripsByPriceRange(minPrice: Double, maxPrice: Double): ServiceResult<List<Trip>>
    
    /**
     * Get trips departing within a date range
     */
    suspend fun getTripsByDateRange(startDate: LocalDate, endDate: LocalDate): ServiceResult<List<Trip>>
    
    /**
     * Get featured/popular trips
     */
    suspend fun getFeaturedTrips(limit: Int = 10): ServiceResult<List<Trip>>
    
    /**
     * Get all available trip categories
     */
    suspend fun getAllCategories(): ServiceResult<List<TripCategory>>
    
    /**
     * Get all available departure locations
     */
    suspend fun getAllDepartureLocations(): ServiceResult<List<String>>
    
    /**
     * Get all available destinations
     */
    suspend fun getAllDestinations(): ServiceResult<List<String>>
}