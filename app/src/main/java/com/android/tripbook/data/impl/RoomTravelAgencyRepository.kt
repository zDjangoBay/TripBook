package com.android.tripbook.data.impl

import com.android.tripbook.data.TravelAgencyRepository
import com.android.tripbook.data.local.dao.TravelAgencyDao
import com.android.tripbook.data.local.mapper.TravelAgencyMapper
import com.android.tripbook.data.model.TravelAgency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.NoSuchElementException

/**
 * Room-based implementation of TravelAgencyRepository
 */
class RoomTravelAgencyRepository(
    private val agencyDao: TravelAgencyDao
) : TravelAgencyRepository {

    override fun getAllAgencies(): Flow<List<TravelAgency>> {
        return agencyDao.getAllAgencies().map { agencies ->
            TravelAgencyMapper.fromEntities(agencies)
        }
    }

    override suspend fun getAgencyById(agencyId: String): TravelAgency {
        val agencyEntity = agencyDao.getAgencyById(agencyId) 
            ?: throw NoSuchElementException("Travel agency not found with id: $agencyId")
        
        return TravelAgencyMapper.fromEntity(agencyEntity)
    }

    override fun searchAgencies(query: String): Flow<List<TravelAgency>> {
        // In a real implementation, you'd use Full Text Search or a more sophisticated query
        // For now, we'll just use the getAllAgencies flow and filter in memory
        return agencyDao.getAllAgencies().map { agencies ->
            agencies
                .filter { 
                    it.name.contains(query, ignoreCase = true) || 
                    it.description.contains(query, ignoreCase = true) 
                }
                .let { TravelAgencyMapper.fromEntities(it) }
        }
    }
}
