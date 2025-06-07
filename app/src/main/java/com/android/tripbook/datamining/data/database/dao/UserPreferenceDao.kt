package com.android.tripbook.datamining.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.datamining.data.database.entities.UserPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPreference: UserPreference): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userPreferences: List<UserPreference>)

    @Update
    suspend fun update(userPreference: UserPreference)

    @Delete
    suspend fun delete(userPreference: UserPreference)

    @Query("DELETE FROM user_preferences")
    suspend fun deleteAll()

    @Query("SELECT * FROM user_preferences WHERE userId = :userId ORDER BY preferenceStrength DESC")
    fun getUserPreferences(userId: String): Flow<List<UserPreference>>

    @Query("SELECT * FROM user_preferences WHERE userId = :userId AND preferenceType = :preferenceType ORDER BY preferenceStrength DESC")
    fun getUserPreferencesByType(userId: String, preferenceType: String): Flow<List<UserPreference>>

    @Query("SELECT * FROM user_preferences WHERE userId = :userId AND preferenceStrength >= :minStrength ORDER BY preferenceStrength DESC")
    fun getStrongUserPreferences(userId: String, minStrength: Float): Flow<List<UserPreference>>

    @Query("SELECT * FROM user_preferences WHERE userId = :userId AND source = :source ORDER BY preferenceStrength DESC")
    fun getUserPreferencesBySource(userId: String, source: String): Flow<List<UserPreference>>
}
