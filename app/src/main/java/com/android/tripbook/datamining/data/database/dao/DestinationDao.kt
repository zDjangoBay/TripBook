package com.android.tripbook.datamining.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.datamining.data.database.entities.Destination
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(destination: Destination): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(destinations: List<Destination>)

    @Update
    suspend fun update(destination: Destination)

    @Delete
    suspend fun delete(destination: Destination)

    @Query("DELETE FROM destinations")
    suspend fun deleteAll()

    @Query("SELECT * FROM destinations ORDER BY popularity DESC")
    fun getAllDestinations(): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE id = :id")
    fun getDestinationById(id: Long): Flow<Destination>

    @Query("SELECT * FROM destinations ORDER BY popularity DESC LIMIT :limit")
    fun getTopDestinations(limit: Int): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE region = :region ORDER BY popularity DESC")
    fun getDestinationsByRegion(region: String): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE country = :country ORDER BY popularity DESC")
    fun getDestinationsByCountry(country: String): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE tags LIKE '%' || :tag || '%' ORDER BY popularity DESC")
    fun getDestinationsByTag(tag: String): Flow<List<Destination>>
}
