package com.android.tripbook.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.data.local.entity.TravelAgencyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for TravelAgency entity operations
 */
@Dao
interface TravelAgencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelAgency(agency: TravelAgencyEntity): Long

    @Update
    suspend fun updateTravelAgency(agency: TravelAgencyEntity): Int

    @Delete
    suspend fun deleteTravelAgency(agency: TravelAgencyEntity): Int

    @Query("SELECT * FROM travel_agencies WHERE id = :agencyId")
    suspend fun getAgencyById(agencyId: String): TravelAgencyEntity?

    @Query("SELECT * FROM travel_agencies ORDER BY name")
    fun getAllAgencies(): Flow<List<TravelAgencyEntity>>
}
