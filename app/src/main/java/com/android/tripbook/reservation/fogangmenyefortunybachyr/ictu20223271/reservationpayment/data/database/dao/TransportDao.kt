package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.dao

import androidx.room.*
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.entities.TransportOptionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for Transport operations
 * 
 * This DAO handles all database operations for transport options
 * including CRUD operations, filtering by type, and availability checks.
 * It supports the TransportSelectionStep and TransportOptionsStep.
 * 
 * Key Features:
 * - Trip-specific transport options
 * - Filter by transport type (PLANE, CAR, SHIP)
 * - Availability and capacity management
 * - Price-based sorting
 * - Schedule-based queries
 * 
 * Used by:
 * - TransportManager for business logic
 * - TransportSelectionStep for showing transport types
 * - TransportOptionsStep for specific options
 * - ReservationFlow for transport booking
 */
@Dao
interface TransportDao {
    
    /**
     * Get all transport options for a specific trip
     */
    @Query("SELECT * FROM transport_options WHERE trip_id = :tripId AND is_available = 1 ORDER BY price ASC")
    fun getTransportOptionsForTrip(tripId: String): Flow<List<TransportOptionEntity>>
    
    /**
     * Get transport options by type for a trip
     * Used by TransportSelectionStep and TransportOptionsStep
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND type = :transportType 
        AND is_available = 1 
        ORDER BY price ASC
    """)
    fun getTransportOptionsByType(tripId: String, transportType: String): Flow<List<TransportOptionEntity>>
    
    /**
     * Get a specific transport option by ID
     */
    @Query("SELECT * FROM transport_options WHERE id = :transportId")
    suspend fun getTransportOptionById(transportId: String): TransportOptionEntity?
    
    /**
     * Get available transport options (with capacity)
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND is_available = 1 
        AND (capacity IS NULL OR booked_seats < capacity)
        ORDER BY departure_time ASC
    """)
    fun getAvailableTransportOptions(tripId: String): Flow<List<TransportOptionEntity>>
    
    /**
     * Get transport options within a price range
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND is_available = 1 
        AND price BETWEEN :minPrice AND :maxPrice 
        ORDER BY price ASC
    """)
    fun getTransportOptionsByPriceRange(
        tripId: String, 
        minPrice: Double, 
        maxPrice: Double
    ): Flow<List<TransportOptionEntity>>
    
    /**
     * Get transport options departing within a time range
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND is_available = 1 
        AND departure_time BETWEEN :startTime AND :endTime 
        ORDER BY departure_time ASC
    """)
    fun getTransportOptionsByTimeRange(
        tripId: String, 
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): Flow<List<TransportOptionEntity>>
    
    /**
     * Get cheapest transport option for a trip
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND is_available = 1 
        ORDER BY price ASC 
        LIMIT 1
    """)
    suspend fun getCheapestTransportOption(tripId: String): TransportOptionEntity?
    
    /**
     * Get fastest transport option (shortest travel time)
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND is_available = 1 
        ORDER BY (julianday(arrival_time) - julianday(departure_time)) ASC 
        LIMIT 1
    """)
    suspend fun getFastestTransportOption(tripId: String): TransportOptionEntity?
    
    /**
     * Get all unique transport types for a trip
     */
    @Query("SELECT DISTINCT type FROM transport_options WHERE trip_id = :tripId AND is_available = 1")
    suspend fun getAvailableTransportTypes(tripId: String): List<String>
    
    /**
     * Get transport options sorted by departure time
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND is_available = 1 
        ORDER BY departure_time ASC
    """)
    fun getTransportOptionsByDepartureTime(tripId: String): Flow<List<TransportOptionEntity>>
    
    /**
     * Search transport options by name or description
     */
    @Query("""
        SELECT * FROM transport_options 
        WHERE trip_id = :tripId 
        AND is_available = 1 
        AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY price ASC
    """)
    fun searchTransportOptions(tripId: String, query: String): Flow<List<TransportOptionEntity>>
    
    /**
     * Insert a new transport option
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransportOption(transportOption: TransportOptionEntity)
    
    /**
     * Insert multiple transport options
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransportOptions(transportOptions: List<TransportOptionEntity>)
    
    /**
     * Update an existing transport option
     */
    @Update
    suspend fun updateTransportOption(transportOption: TransportOptionEntity)
    
    /**
     * Update availability status
     */
    @Query("""
        UPDATE transport_options 
        SET is_available = :isAvailable, updated_at = :timestamp 
        WHERE id = :transportId
    """)
    suspend fun updateAvailability(
        transportId: String, 
        isAvailable: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update booked seats count
     */
    @Query("""
        UPDATE transport_options 
        SET booked_seats = :bookedSeats, updated_at = :timestamp 
        WHERE id = :transportId
    """)
    suspend fun updateBookedSeats(
        transportId: String, 
        bookedSeats: Int,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Increment booked seats (when a booking is made)
     */
    @Query("""
        UPDATE transport_options 
        SET booked_seats = booked_seats + :increment, updated_at = :timestamp 
        WHERE id = :transportId
    """)
    suspend fun incrementBookedSeats(
        transportId: String, 
        increment: Int = 1,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Decrement booked seats (when a booking is cancelled)
     */
    @Query("""
        UPDATE transport_options 
        SET booked_seats = MAX(0, booked_seats - :decrement), updated_at = :timestamp 
        WHERE id = :transportId
    """)
    suspend fun decrementBookedSeats(
        transportId: String, 
        decrement: Int = 1,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete a transport option
     */
    @Delete
    suspend fun deleteTransportOption(transportOption: TransportOptionEntity)
    
    /**
     * Delete all transport options for a trip
     */
    @Query("DELETE FROM transport_options WHERE trip_id = :tripId")
    suspend fun deleteTransportOptionsForTrip(tripId: String)
    
    /**
     * Get transport option count for a trip
     */
    @Query("SELECT COUNT(*) FROM transport_options WHERE trip_id = :tripId AND is_available = 1")
    suspend fun getTransportOptionCount(tripId: String): Int
    
    /**
     * Get price range for transport options of a trip
     */
    @Query("""
        SELECT MIN(price) as min_price, MAX(price) as max_price 
        FROM transport_options 
        WHERE trip_id = :tripId AND is_available = 1
    """)
    suspend fun getTransportPriceRange(tripId: String): TransportPriceRange?
    
    /**
     * Check if transport option has available capacity
     */
    @Query("""
        SELECT CASE 
            WHEN capacity IS NULL THEN 1 
            WHEN booked_seats < capacity THEN 1 
            ELSE 0 
        END 
        FROM transport_options 
        WHERE id = :transportId AND is_available = 1
    """)
    suspend fun hasAvailableCapacity(transportId: String): Boolean
}

/**
 * Data class for transport price range results
 */
data class TransportPriceRange(
    @ColumnInfo(name = "min_price") val minPrice: Double,
    @ColumnInfo(name = "max_price") val maxPrice: Double
)
