package com.android.tripbook.data.database.dao

import androidx.room.*
import com.android.tripbook.data.database.entities.UserFavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User Favorites operations
 * 
 * This DAO handles all database operations for user favorites including
 * CRUD operations, filtering by type, and priority management.
 * It supports personalized recommendations and quick access features.
 * 
 * Key Features:
 * - User-specific favorite queries
 * - Filter by favorite type (TRIP, HOTEL, ACTIVITY)
 * - Priority-based sorting
 * - Notes management
 * - Reactive data with Flow
 * 
 * Used by:
 * - UserManager for business logic
 * - Dashboard for showing favorite trips
 * - Profile screen for managing favorites
 * - Recommendation engine
 */
@Dao
interface UserFavoriteDao {
    
    /**
     * Get all favorites for a user
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId ORDER BY priority DESC, created_at DESC")
    fun getUserFavorites(userId: String): Flow<List<UserFavoriteEntity>>
    
    /**
     * Get favorites by type for a user
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId AND favorite_type = :favoriteType ORDER BY priority DESC, created_at DESC")
    fun getFavoritesByType(userId: String, favoriteType: String): Flow<List<UserFavoriteEntity>>
    
    /**
     * Get favorite trips for a user
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId AND favorite_type = 'TRIP' ORDER BY priority DESC, created_at DESC")
    fun getFavoriteTrips(userId: String): Flow<List<UserFavoriteEntity>>
    
    /**
     * Get favorite hotels for a user
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId AND favorite_type = 'HOTEL' ORDER BY priority DESC, created_at DESC")
    fun getFavoriteHotels(userId: String): Flow<List<UserFavoriteEntity>>
    
    /**
     * Get favorite activities for a user
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId AND favorite_type = 'ACTIVITY' ORDER BY priority DESC, created_at DESC")
    fun getFavoriteActivities(userId: String): Flow<List<UserFavoriteEntity>>
    
    /**
     * Get high priority favorites
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId AND priority > 0 ORDER BY priority DESC, created_at DESC")
    fun getHighPriorityFavorites(userId: String): Flow<List<UserFavoriteEntity>>
    
    /**
     * Get favorites with notes
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId AND notes IS NOT NULL AND notes != '' ORDER BY priority DESC, created_at DESC")
    fun getFavoritesWithNotes(userId: String): Flow<List<UserFavoriteEntity>>
    
    /**
     * Get a specific favorite by user and item
     */
    @Query("SELECT * FROM user_favorites WHERE user_id = :userId AND favorite_id = :favoriteId AND favorite_type = :favoriteType")
    suspend fun getFavorite(userId: String, favoriteId: String, favoriteType: String): UserFavoriteEntity?
    
    /**
     * Check if an item is favorited by user
     */
    @Query("SELECT COUNT(*) > 0 FROM user_favorites WHERE user_id = :userId AND favorite_id = :favoriteId AND favorite_type = :favoriteType")
    suspend fun isFavorited(userId: String, favoriteId: String, favoriteType: String): Boolean
    
    /**
     * Get favorite IDs by type (for filtering other queries)
     */
    @Query("SELECT favorite_id FROM user_favorites WHERE user_id = :userId AND favorite_type = :favoriteType")
    suspend fun getFavoriteIds(userId: String, favoriteType: String): List<String>
    
    /**
     * Get favorite trip IDs
     */
    @Query("SELECT favorite_id FROM user_favorites WHERE user_id = :userId AND favorite_type = 'TRIP'")
    suspend fun getFavoriteTripIds(userId: String): List<String>
    
    /**
     * Get favorite hotel IDs
     */
    @Query("SELECT favorite_id FROM user_favorites WHERE user_id = :userId AND favorite_type = 'HOTEL'")
    suspend fun getFavoriteHotelIds(userId: String): List<String>
    
    /**
     * Get favorite activity IDs
     */
    @Query("SELECT favorite_id FROM user_favorites WHERE user_id = :userId AND favorite_type = 'ACTIVITY'")
    suspend fun getFavoriteActivityIds(userId: String): List<String>
    
    /**
     * Get favorite count by type
     */
    @Query("SELECT COUNT(*) FROM user_favorites WHERE user_id = :userId AND favorite_type = :favoriteType")
    suspend fun getFavoriteCountByType(userId: String, favoriteType: String): Int
    
    /**
     * Get total favorite count for user
     */
    @Query("SELECT COUNT(*) FROM user_favorites WHERE user_id = :userId")
    suspend fun getTotalFavoriteCount(userId: String): Int
    
    /**
     * Insert a new favorite
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: UserFavoriteEntity): Long
    
    /**
     * Insert multiple favorites
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<UserFavoriteEntity>)
    
    /**
     * Update a favorite
     */
    @Update
    suspend fun updateFavorite(favorite: UserFavoriteEntity)
    
    /**
     * Update favorite notes
     */
    @Query("""
        UPDATE user_favorites 
        SET notes = :notes, updated_at = :timestamp 
        WHERE user_id = :userId AND favorite_id = :favoriteId AND favorite_type = :favoriteType
    """)
    suspend fun updateNotes(
        userId: String,
        favoriteId: String,
        favoriteType: String,
        notes: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update favorite priority
     */
    @Query("""
        UPDATE user_favorites 
        SET priority = :priority, updated_at = :timestamp 
        WHERE user_id = :userId AND favorite_id = :favoriteId AND favorite_type = :favoriteType
    """)
    suspend fun updatePriority(
        userId: String,
        favoriteId: String,
        favoriteType: String,
        priority: Int,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete a favorite
     */
    @Delete
    suspend fun deleteFavorite(favorite: UserFavoriteEntity)
    
    /**
     * Delete favorite by user and item
     */
    @Query("DELETE FROM user_favorites WHERE user_id = :userId AND favorite_id = :favoriteId AND favorite_type = :favoriteType")
    suspend fun deleteFavorite(userId: String, favoriteId: String, favoriteType: String)
    
    /**
     * Delete all favorites of a type for user
     */
    @Query("DELETE FROM user_favorites WHERE user_id = :userId AND favorite_type = :favoriteType")
    suspend fun deleteFavoritesByType(userId: String, favoriteType: String)
    
    /**
     * Delete all favorites for user
     */
    @Query("DELETE FROM user_favorites WHERE user_id = :userId")
    suspend fun deleteAllUserFavorites(userId: String)
    
    /**
     * Get favorite statistics for a user
     */
    @Query("""
        SELECT 
            COUNT(*) as total_favorites,
            SUM(CASE WHEN favorite_type = 'TRIP' THEN 1 ELSE 0 END) as trip_favorites,
            SUM(CASE WHEN favorite_type = 'HOTEL' THEN 1 ELSE 0 END) as hotel_favorites,
            SUM(CASE WHEN favorite_type = 'ACTIVITY' THEN 1 ELSE 0 END) as activity_favorites,
            SUM(CASE WHEN priority > 0 THEN 1 ELSE 0 END) as high_priority_favorites,
            SUM(CASE WHEN notes IS NOT NULL AND notes != '' THEN 1 ELSE 0 END) as favorites_with_notes
        FROM user_favorites 
        WHERE user_id = :userId
    """)
    suspend fun getFavoriteStats(userId: String): FavoriteStats?
}

/**
 * Data class for favorite statistics
 */
data class FavoriteStats(
    @ColumnInfo(name = "total_favorites") val totalFavorites: Int,
    @ColumnInfo(name = "trip_favorites") val tripFavorites: Int,
    @ColumnInfo(name = "hotel_favorites") val hotelFavorites: Int,
    @ColumnInfo(name = "activity_favorites") val activityFavorites: Int,
    @ColumnInfo(name = "high_priority_favorites") val highPriorityFavorites: Int,
    @ColumnInfo(name = "favorites_with_notes") val favoritesWithNotes: Int
)

class PreferenceHelper(context: Context) {

    private val prefs: SharedPreferences =
            context.getSharedPreferences("tripbook_preferences", Context.MODE_PRIVATE)

    fun setLangue(langue: String) {
        prefs.edit().putString("langue", langue).apply()
    }

    fun getLangue(): String = prefs.getString("langue", "fr") ?: "fr"

    fun setMoyenTransportPrefere(mode: String) {
        prefs.edit().putString("transport", mode).apply()
    }

    fun getMoyenTransportPrefere(): String = prefs.getString("transport", "Avion") ?: "Avion"

    fun setThemeSombre(active: Boolean) {
        prefs.edit().putBoolean("theme_sombre", active).apply()
    }

    fun isThemeSombre(): Boolean = prefs.getBoolean("theme_sombre", false)

    fun setNotificationsActives(active: Boolean) {
        prefs.edit().putBoolean("notifications", active).apply()
    }

    fun isNotificationsActives(): Boolean = prefs.getBoolean("notifications", true)
}

//The user can set up favorite places and see their preferences
class PreferencesActivity : AppCompatActivity() {
    private lateinit var prefHelper: DestinationPrefHelper
    private val userId = "user_123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        prefHelper = DestinationPrefHelper(this)

        // Exemple : enregistrer des destinations préférées
        val lieuxChoisis = listOf("Tokyo", "Nairobi", "Rio")
        prefHelper.saveDestinationsPref(userId, lieuxChoisis)

        // Récupérer et afficher
        val prefs = prefHelper.getDestinationsPref(userId)
        findViewById<TextView>(R.id.txtPrefs).text = "Destinations préférées : ${prefs.joinToString()}"
    }
}