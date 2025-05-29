package com.android.tripbook.data

import com.android.tripbook.data.model.TravelAgency
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for travel agency operations
 */
interface TravelAgencyRepository {
    /**
     * Get all available travel agencies
     */
    fun getAllAgencies(): Flow<List<TravelAgency>>
    
    /**
     * Get agency by ID
     */
    suspend fun getAgencyById(agencyId: String): TravelAgency
    
    /**
     * Search for agencies by name
     */
    fun searchAgencies(query: String): Flow<List<TravelAgency>>
}
