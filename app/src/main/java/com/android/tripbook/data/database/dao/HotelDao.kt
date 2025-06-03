package com.android.tripbook.data.database.dao

import androidx.room.*
import com.android.tripbook.data.database.entities.HotelEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Hotel operations
 * 
 * This DAO handles all database operations for hotels including
 * CRUD operations, filtering by location, rating, and price.
 * It supports the HotelSelectionStep in the reservation flow.
 * 
 * Key Features:
 * - Location-based hotel queries
 * - Filter by rating, price, amenities
 * - Availability management
 * - Search functionality
 * - Room capacity tracking
 * 
 * Used by:
 * - HotelManager for business logic
 * - HotelSelectionStep for showing available hotels
 * - ReservationFlow for hotel booking
 */
@Dao
interface HotelDao {
    
    /**
     * Get all available hotels
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 ORDER BY rating DESC, price_per_night ASC")
    fun getAllHotels(): Flow<List<HotelEntity>>
    
    /**
     * Get a specific hotel by ID
     */
    @Query("SELECT * FROM hotels WHERE id = :hotelId")
    suspend fun getHotelById(hotelId: String): HotelEntity?
    
    /**
     * Get hotels by location
     * Used for destination-specific hotel searches
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 AND location LIKE '%' || :location || '%' ORDER BY rating DESC")
    fun getHotelsByLocation(location: String): Flow<List<HotelEntity>>
    
    /**
     * Get hotels by minimum rating
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 AND rating >= :minRating ORDER BY rating DESC, price_per_night ASC")
    fun getHotelsByMinRating(minRating: Int): Flow<List<HotelEntity>>
    
    /**
     * Get hotels within a price range
     */
    @Query("""
        SELECT * FROM hotels 
        WHERE is_available = 1 
        AND price_per_night BETWEEN :minPrice AND :maxPrice 
        ORDER BY price_per_night ASC
    """)
    fun getHotelsByPriceRange(minPrice: Double, maxPrice: Double): Flow<List<HotelEntity>>
    
    /**
     * Get hotels with available rooms
     */
    @Query("""
        SELECT * FROM hotels 
        WHERE is_available = 1 
        AND (available_rooms IS NULL OR available_rooms > 0)
        ORDER BY rating DESC
    """)
    fun getHotelsWithAvailableRooms(): Flow<List<HotelEntity>>
    
    /**
     * Search hotels by name or description
     */
    @Query("""
        SELECT * FROM hotels 
        WHERE is_available = 1 
        AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY 
            CASE WHEN name LIKE '%' || :query || '%' THEN 1 ELSE 2 END,
            rating DESC
    """)
    fun searchHotels(query: String): Flow<List<HotelEntity>>
    
    /**
     * Get hotels with specific amenities
     * Searches for amenities in the comma-separated amenities field
     */
    @Query("""
        SELECT * FROM hotels 
        WHERE is_available = 1 
        AND amenities LIKE '%' || :amenity || '%'
        ORDER BY rating DESC
    """)
    fun getHotelsByAmenity(amenity: String): Flow<List<HotelEntity>>
    
    /**
     * Get hotels by room type
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 AND room_type LIKE '%' || :roomType || '%' ORDER BY rating DESC")
    fun getHotelsByRoomType(roomType: String): Flow<List<HotelEntity>>
    
    /**
     * Get cheapest hotels
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 ORDER BY price_per_night ASC LIMIT :limit")
    fun getCheapestHotels(limit: Int = 10): Flow<List<HotelEntity>>
    
    /**
     * Get highest rated hotels
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 ORDER BY rating DESC LIMIT :limit")
    fun getTopRatedHotels(limit: Int = 10): Flow<List<HotelEntity>>
    
    /**
     * Get hotels sorted by price (ascending)
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 ORDER BY price_per_night ASC")
    fun getHotelsSortedByPrice(): Flow<List<HotelEntity>>
    
    /**
     * Get hotels sorted by rating (descending)
     */
    @Query("SELECT * FROM hotels WHERE is_available = 1 ORDER BY rating DESC, price_per_night ASC")
    fun getHotelsSortedByRating(): Flow<List<HotelEntity>>
    
    /**
     * Get all unique locations
     */
    @Query("SELECT DISTINCT location FROM hotels WHERE is_available = 1 ORDER BY location")
    suspend fun getAllLocations(): List<String>
    
    /**
     * Get all unique room types
     */
    @Query("SELECT DISTINCT room_type FROM hotels WHERE is_available = 1 ORDER BY room_type")
    suspend fun getAllRoomTypes(): List<String>
    
    /**
     * Get all unique amenities (this will need processing to split comma-separated values)
     */
    @Query("SELECT DISTINCT amenities FROM hotels WHERE is_available = 1")
    suspend fun getAllAmenitiesRaw(): List<String>
    
    /**
     * Insert a new hotel
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotel(hotel: HotelEntity)
    
    /**
     * Insert multiple hotels
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotels(hotels: List<HotelEntity>)
    
    /**
     * Update an existing hotel
     */
    @Update
    suspend fun updateHotel(hotel: HotelEntity)
    
    /**
     * Update hotel availability
     */
    @Query("""
        UPDATE hotels 
        SET is_available = :isAvailable, updated_at = :timestamp 
        WHERE id = :hotelId
    """)
    suspend fun updateAvailability(
        hotelId: String, 
        isAvailable: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update available rooms count
     */
    @Query("""
        UPDATE hotels 
        SET available_rooms = :availableRooms, updated_at = :timestamp 
        WHERE id = :hotelId
    """)
    suspend fun updateAvailableRooms(
        hotelId: String, 
        availableRooms: Int,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Decrement available rooms (when a booking is made)
     */
    @Query("""
        UPDATE hotels 
        SET available_rooms = CASE 
            WHEN available_rooms IS NULL THEN NULL 
            ELSE MAX(0, available_rooms - :decrement) 
        END,
        updated_at = :timestamp 
        WHERE id = :hotelId
    """)
    suspend fun decrementAvailableRooms(
        hotelId: String, 
        decrement: Int = 1,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Increment available rooms (when a booking is cancelled)
     */
    @Query("""
        UPDATE hotels 
        SET available_rooms = CASE 
            WHEN available_rooms IS NULL THEN NULL 
            ELSE available_rooms + :increment 
        END,
        updated_at = :timestamp 
        WHERE id = :hotelId
    """)
    suspend fun incrementAvailableRooms(
        hotelId: String, 
        increment: Int = 1,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete a hotel
     */
    @Delete
    suspend fun deleteHotel(hotel: HotelEntity)
    
    /**
     * Get hotel count
     */
    @Query("SELECT COUNT(*) FROM hotels WHERE is_available = 1")
    suspend fun getHotelCount(): Int
    
    /**
     * Get price range for hotels
     */
    @Query("SELECT MIN(price_per_night) as min_price, MAX(price_per_night) as max_price FROM hotels WHERE is_available = 1")
    suspend fun getHotelPriceRange(): HotelPriceRange?
    
    /**
     * Check if hotel has available rooms
     */
    @Query("""
        SELECT CASE 
            WHEN available_rooms IS NULL THEN 1 
            WHEN available_rooms > 0 THEN 1 
            ELSE 0 
        END 
        FROM hotels 
        WHERE id = :hotelId AND is_available = 1
    """)
    suspend fun hasAvailableRooms(hotelId: String): Boolean
}

/**
 * Data class for hotel price range results
 */
data class HotelPriceRange(
    @ColumnInfo(name = "min_price") val minPrice: Double,
    @ColumnInfo(name = "max_price") val maxPrice: Double
)
