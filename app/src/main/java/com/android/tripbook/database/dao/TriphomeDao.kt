package com.android.tripbook.database.dao

import androidx.room.*
import com.android.tripbook.database.entity.TriphomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TriphomeDao {
    
    @Query("SELECT * FROM triphome ORDER BY lastUpdated DESC")
    fun getAllTriphomes(): Flow<List<TriphomeEntity>>
    
    @Query("SELECT * FROM triphome ORDER BY lastUpdated DESC")
    suspend fun getAllTriphomesOnce(): List<TriphomeEntity>
    
    @Query("SELECT * FROM triphome WHERE isFromFirebase = 1 ORDER BY lastUpdated DESC")
    fun getFirebaseTriphomes(): Flow<List<TriphomeEntity>>
    
    @Query("SELECT * FROM triphome WHERE isFromFirebase = 0 ORDER BY lastUpdated DESC")
    fun getUserTriphomes(): Flow<List<TriphomeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriphome(triphome: TriphomeEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriphomes(triphomes: List<TriphomeEntity>)
    
    @Update
    suspend fun updateTriphome(triphome: TriphomeEntity)
    
    @Delete
    suspend fun deleteTriphome(triphome: TriphomeEntity)
    
    @Query("DELETE FROM triphome WHERE isFromFirebase = 1")
    suspend fun clearFirebaseTriphomes()
    
    @Query("DELETE FROM triphome")
    suspend fun clearAllTriphomes()
}
