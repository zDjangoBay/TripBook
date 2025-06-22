package com.android.tripbook.database.dao

import androidx.room.*
import com.android.tripbook.database.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    
    @Query("SELECT * FROM places ORDER BY lastUpdated DESC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>
    
    @Query("SELECT * FROM places ORDER BY lastUpdated DESC")
    suspend fun getAllPlacesOnce(): List<PlaceEntity>
    
    @Query("SELECT * FROM places WHERE isFromFirebase = 1 ORDER BY lastUpdated DESC")
    fun getFirebasePlaces(): Flow<List<PlaceEntity>>
    
    @Query("SELECT * FROM places WHERE isFromFirebase = 0 ORDER BY lastUpdated DESC")
    fun getUserPlaces(): Flow<List<PlaceEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<PlaceEntity>)
    
    @Update
    suspend fun updatePlace(place: PlaceEntity)
    
    @Delete
    suspend fun deletePlace(place: PlaceEntity)
    
    @Query("DELETE FROM places WHERE isFromFirebase = 1")
    suspend fun clearFirebasePlaces()
    
    @Query("DELETE FROM places")
    suspend fun clearAllPlaces()
}
