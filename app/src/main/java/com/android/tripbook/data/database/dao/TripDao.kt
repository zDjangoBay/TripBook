package com.android.tripbook.data.database.dao

import androidx.room.*
import com.android.tripbook.data.database.entities.TripEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for Trip operations
 * 
 * This DAO provides all database operations for trips including
 * CRUD operations, search functionality, and filtering capabilities.
 * It supports the DashboardScreen and trip selection features.
 * 
 * Key Features:
 * - Basic CRUD operations
 * - Search by title, location, description
 * - Filter by category, price range, dates
 * - Sort by price, date, popularity
 * - Reactive data with Flow
 * 
 * Used by:
 * - TripManager for business logic
 * - DashboardScreen for displaying trips
 * - Search and filter functionality
 */
@Dao
interface TripDao {
    
    /**
     * Get all active trips
     * Returns a Flow for reactive UI updates
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 ORDER BY created_at DESC")
    fun getAllTrips(): Flow<List<TripEntity>>
    
    /**
     * Get a specific trip by ID
     */
    @Query("SELECT * FROM trips WHERE id = :tripId AND is_active = 1")
    suspend fun getTripById(tripId: String): TripEntity?
    
    /**
     * Search trips by title, location, or description
     * Case-insensitive search across multiple fields
     */
    @Query("""
        SELECT * FROM trips 
        WHERE is_active = 1 
        AND (
            title LIKE '%' || :query || '%' 
            OR from_location LIKE '%' || :query || '%' 
            OR to_location LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%'
        )
        ORDER BY 
            CASE WHEN title LIKE '%' || :query || '%' THEN 1 ELSE 2 END,
            base_price ASC
    """)
    fun searchTrips(query: String): Flow<List<TripEntity>>
    
    /**
     * Filter trips by category
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 AND category = :category ORDER BY base_price ASC")
    fun getTripsByCategory(category: String): Flow<List<TripEntity>>
    
    /**
     * Filter trips by price range
     */
    @Query("""
        SELECT * FROM trips 
        WHERE is_active = 1 
        AND base_price BETWEEN :minPrice AND :maxPrice 
        ORDER BY base_price ASC
    """)
    fun getTripsByPriceRange(minPrice: Double, maxPrice: Double): Flow<List<TripEntity>>
    
    /**
     * Filter trips by departure location
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 AND from_location LIKE '%' || :location || '%' ORDER BY departure_date ASC")
    fun getTripsByDepartureLocation(location: String): Flow<List<TripEntity>>
    
    /**
     * Filter trips by destination
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 AND to_location LIKE '%' || :destination || '%' ORDER BY departure_date ASC")
    fun getTripsByDestination(destination: String): Flow<List<TripEntity>>
    
    /**
     * Get trips departing within a date range
     */
    @Query("""
        SELECT * FROM trips 
        WHERE is_active = 1 
        AND departure_date BETWEEN :startDate AND :endDate 
        ORDER BY departure_date ASC
    """)
    fun getTripsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TripEntity>>
    
    /**
     * Get upcoming trips (departing in the future)
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 AND departure_date >= :currentDate ORDER BY departure_date ASC")
    fun getUpcomingTrips(currentDate: LocalDate = LocalDate.now()): Flow<List<TripEntity>>
    
    /**
     * Get featured/popular trips (can be based on booking count or manual curation)
     * For now, returns trips sorted by lowest price
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 ORDER BY base_price ASC LIMIT :limit")
    fun getFeaturedTrips(limit: Int = 10): Flow<List<TripEntity>>
    
    /**
     * Get trips sorted by price (ascending)
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 ORDER BY base_price ASC")
    fun getTripsSortedByPrice(): Flow<List<TripEntity>>
    
    /**
     * Get trips sorted by departure date
     */
    @Query("SELECT * FROM trips WHERE is_active = 1 ORDER BY departure_date ASC")
    fun getTripsSortedByDate(): Flow<List<TripEntity>>
    
    /**
     * Get all unique categories
     */
    @Query("SELECT DISTINCT category FROM trips WHERE is_active = 1 ORDER BY category")
    suspend fun getAllCategories(): List<String>
    
    /**
     * Get all unique departure locations
     */
    @Query("SELECT DISTINCT from_location FROM trips WHERE is_active = 1 ORDER BY from_location")
    suspend fun getAllDepartureLocations(): List<String>
    
    /**
     * Get all unique destinations
     */
    @Query("SELECT DISTINCT to_location FROM trips WHERE is_active = 1 ORDER BY to_location")
    suspend fun getAllDestinations(): List<String>
    
    /**
     * Insert a new trip
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)
    
    /**
     * Insert multiple trips
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)
    
    /**
     * Update an existing trip
     */
    @Update
    suspend fun updateTrip(trip: TripEntity)
    
    /**
     * Soft delete a trip (mark as inactive)
     */
    @Query("UPDATE trips SET is_active = 0, updated_at = :timestamp WHERE id = :tripId")
    suspend fun softDeleteTrip(tripId: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Hard delete a trip (permanent removal)
     */
    @Delete
    suspend fun deleteTrip(trip: TripEntity)
    
    /**
     * Get trip count for statistics
     */
    @Query("SELECT COUNT(*) FROM trips WHERE is_active = 1")
    suspend fun getTripCount(): Int
    
    /**
     * Get price range for filtering UI
     */
    @Query("SELECT MIN(base_price) as min_price, MAX(base_price) as max_price FROM trips WHERE is_active = 1")
    suspend fun getPriceRange(): PriceRange?
}

/**
 * Data class for price range results
 */
data class PriceRange(
    @ColumnInfo(name = "min_price") val minPrice: Double,
    @ColumnInfo(name = "max_price") val maxPrice: Double
)
