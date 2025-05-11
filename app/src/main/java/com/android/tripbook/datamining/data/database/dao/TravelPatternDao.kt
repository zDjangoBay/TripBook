package com.android.tripbook.datamining.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.datamining.data.database.entities.TravelPattern
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelPatternDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(travelPattern: TravelPattern): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(travelPatterns: List<TravelPattern>)

    @Update
    suspend fun update(travelPattern: TravelPattern)

    @Delete
    suspend fun delete(travelPattern: TravelPattern)

    @Query("DELETE FROM travel_patterns")
    suspend fun deleteAll()

    @Query("SELECT * FROM travel_patterns ORDER BY confidence DESC")
    fun getAllPatterns(): Flow<List<TravelPattern>>

    @Query("SELECT * FROM travel_patterns WHERE userId = :userId ORDER BY confidence DESC")
    fun getPatternsByUserId(userId: String): Flow<List<TravelPattern>>

    @Query("SELECT * FROM travel_patterns WHERE patternType = :patternType ORDER BY confidence DESC")
    fun getPatternsByType(patternType: String): Flow<List<TravelPattern>>

    @Query("SELECT * FROM travel_patterns WHERE userId = :userId AND patternType = :patternType ORDER BY confidence DESC")
    fun getPatternsByUserAndType(userId: String, patternType: String): Flow<List<TravelPattern>>

    @Query("SELECT * FROM travel_patterns WHERE confidence >= :minConfidence ORDER BY confidence DESC")
    fun getHighConfidencePatterns(minConfidence: Float): Flow<List<TravelPattern>>
}
