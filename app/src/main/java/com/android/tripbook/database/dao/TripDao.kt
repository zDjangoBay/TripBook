package com.android.tripbook.database.dao

import androidx.room.*
import com.android.tripbook.database.entity.TripEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Trip operations
 * Provides database operations for trips
 */
@Dao
interface TripDao {
    
    /**
     * Get all trips as Flow for reactive updates
     */
    @Query("SELECT * FROM trips ORDER BY id ASC")
    fun getAllTrips(): Flow<List<TripEntity>>
    
    /**
     * Get all trips as list (for one-time queries)
     */
    @Query("SELECT * FROM trips ORDER BY id ASC")
    suspend fun getAllTripsOnce(): List<TripEntity>
    
    /**
     * Get trip by ID
     */
    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Int): TripEntity?
    
    /**
     * Get trip by ID as Flow
     */
    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripByIdFlow(tripId: Int): Flow<TripEntity?>
    
    /**
     * Insert single trip
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)
    
    /**
     * Insert multiple trips
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)
    
    /**
     * Update trip
     */
    @Update
    suspend fun updateTrip(trip: TripEntity)
    
    /**
     * Delete trip
     */
    @Delete
    suspend fun deleteTrip(trip: TripEntity)
    
    /**
     * Delete trip by ID
     */
    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Int)
    
    /**
     * Delete all trips
     */
    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()
    
    /**
     * Get trips count
     */
    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripsCount(): Int
    
    /**
     * Search trips by title or caption
     */
    @Query("SELECT * FROM trips WHERE title LIKE '%' || :query || '%' OR caption LIKE '%' || :query || '%'")
    fun searchTrips(query: String): Flow<List<TripEntity>>
}
