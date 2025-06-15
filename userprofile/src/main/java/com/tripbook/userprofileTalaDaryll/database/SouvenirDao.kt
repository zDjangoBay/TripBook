package com.tripbook.userprofileTalaDaryll.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.tripbook.userprofileTalaDaryll.models.Souvenir

@Dao
interface SouvenirDao {
    @Insert
    suspend fun insert(souvenir: Souvenir)

    @Delete
    suspend fun delete(souvenir: Souvenir)

    @Query("SELECT * FROM souvenirs ORDER BY timestamp DESC")
    fun getAllSouvenirs(): Flow<List<Souvenir>>
}