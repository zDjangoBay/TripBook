package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.entities.ActivityEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Activity operations
 * 
 * This DAO handles all database operations for activities including
 * CRUD operations, filtering by category, location, and price.
 * It supports the SummaryStep and future ActivitySelectionFragment.
 * 
 * Key Features:
 * - Location-based activity queries
 * - Filter by category, price, difficulty
 * - Availability management
 * - Search functionality
 * - Participant requirements
 * 
 * Used by:
 * - ActivityManager for business logic
 * - SummaryStep for activity selection
 * - ActivitySelectionFragment (future implementation)
 * - ReservationFlow for activity booking
 */
@Dao
interface ActivityDao {
    
    /**
     * Get all available activities
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 ORDER BY category, price ASC")
    fun getAllActivities(): Flow<List<ActivityEntity>>
    
    /**
     * Get a specific activity by ID
     */
    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: String): ActivityEntity?
    
    /**
     * Get activities by location
     * Used for destination-specific activity searches
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 AND location LIKE '%' || :location || '%' ORDER BY category, price ASC")
    fun getActivitiesByLocation(location: String): Flow<List<ActivityEntity>>
    
    /**
     * Get activities by category
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 AND category = :category ORDER BY price ASC")
    fun getActivitiesByCategory(category: String): Flow<List<ActivityEntity>>
    
    /**
     * Get activities within a price range
     */
    @Query("""
        SELECT * FROM activities 
        WHERE is_available = 1 
        AND price BETWEEN :minPrice AND :maxPrice 
        ORDER BY price ASC
    """)
    fun getActivitiesByPriceRange(minPrice: Double, maxPrice: Double): Flow<List<ActivityEntity>>
    
    /**
     * Get activities by difficulty level
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 AND difficulty_level = :difficultyLevel ORDER BY price ASC")
    fun getActivitiesByDifficulty(difficultyLevel: String): Flow<List<ActivityEntity>>
    
    /**
     * Search activities by name or description
     */
    @Query("""
        SELECT * FROM activities 
        WHERE is_available = 1 
        AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY 
            CASE WHEN name LIKE '%' || :query || '%' THEN 1 ELSE 2 END,
            price ASC
    """)
    fun searchActivities(query: String): Flow<List<ActivityEntity>>
    
    /**
     * Get activities that require booking
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 AND requires_booking = 1 ORDER BY category, price ASC")
    fun getBookableActivities(): Flow<List<ActivityEntity>>
    
    /**
     * Get free activities (price = 0)
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 AND price = 0 ORDER BY category")
    fun getFreeActivities(): Flow<List<ActivityEntity>>
    
    /**
     * Get activities suitable for specific participant count
     */
    @Query("""
        SELECT * FROM activities 
        WHERE is_available = 1 
        AND min_participants <= :participantCount 
        AND (max_participants IS NULL OR max_participants >= :participantCount)
        ORDER BY price ASC
    """)
    fun getActivitiesForParticipantCount(participantCount: Int): Flow<List<ActivityEntity>>
    
    /**
     * Get cheapest activities
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 ORDER BY price ASC LIMIT :limit")
    fun getCheapestActivities(limit: Int = 10): Flow<List<ActivityEntity>>
    
    /**
     * Get activities sorted by price (ascending)
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 ORDER BY price ASC")
    fun getActivitiesSortedByPrice(): Flow<List<ActivityEntity>>
    
    /**
     * Get activities sorted by category
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 ORDER BY category, name ASC")
    fun getActivitiesSortedByCategory(): Flow<List<ActivityEntity>>
    
    /**
     * Get popular activities (can be based on booking count or manual curation)
     * For now, returns activities sorted by lowest price
     */
    @Query("SELECT * FROM activities WHERE is_available = 1 ORDER BY price ASC LIMIT :limit")
    fun getPopularActivities(limit: Int = 10): Flow<List<ActivityEntity>>
    
    /**
     * Get all unique categories
     */
    @Query("SELECT DISTINCT category FROM activities WHERE is_available = 1 ORDER BY category")
    suspend fun getAllCategories(): List<String>
    
    /**
     * Get all unique locations
     */
    @Query("SELECT DISTINCT location FROM activities WHERE is_available = 1 ORDER BY location")
    suspend fun getAllLocations(): List<String>
    
    /**
     * Get all unique difficulty levels
     */
    @Query("SELECT DISTINCT difficulty_level FROM activities WHERE is_available = 1 AND difficulty_level IS NOT NULL ORDER BY difficulty_level")
    suspend fun getAllDifficultyLevels(): List<String>
    
    /**
     * Insert a new activity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)
    
    /**
     * Insert multiple activities
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)
    
    /**
     * Update an existing activity
     */
    @Update
    suspend fun updateActivity(activity: ActivityEntity)
    
    /**
     * Update activity availability
     */
    @Query("""
        UPDATE activities 
        SET is_available = :isAvailable, updated_at = :timestamp 
        WHERE id = :activityId
    """)
    suspend fun updateAvailability(
        activityId: String, 
        isAvailable: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update activity price
     */
    @Query("""
        UPDATE activities 
        SET price = :price, updated_at = :timestamp 
        WHERE id = :activityId
    """)
    suspend fun updatePrice(
        activityId: String, 
        price: Double,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete an activity
     */
    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)
    
    /**
     * Get activity count
     */
    @Query("SELECT COUNT(*) FROM activities WHERE is_available = 1")
    suspend fun getActivityCount(): Int
    
    /**
     * Get activity count by category
     */
    @Query("SELECT COUNT(*) FROM activities WHERE is_available = 1 AND category = :category")
    suspend fun getActivityCountByCategory(category: String): Int
    
    /**
     * Get price range for activities
     */
    @Query("SELECT MIN(price) as min_price, MAX(price) as max_price FROM activities WHERE is_available = 1")
    suspend fun getActivityPriceRange(): ActivityPriceRange?
    
    /**
     * Get price range for activities by location
     */
    @Query("""
        SELECT MIN(price) as min_price, MAX(price) as max_price 
        FROM activities 
        WHERE is_available = 1 AND location LIKE '%' || :location || '%'
    """)
    suspend fun getActivityPriceRangeByLocation(location: String): ActivityPriceRange?
}

/**
 * Data class for activity price range results
 */
data class ActivityPriceRange(
    @ColumnInfo(name = "min_price") val minPrice: Double,
    @ColumnInfo(name = "max_price") val maxPrice: Double
)
