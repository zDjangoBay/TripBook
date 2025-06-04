package com.android.tripbook.data.services.trip

import com.android.tripbook.data.database.dao.TripDao
import com.android.tripbook.data.models.Trip
import com.android.tripbook.data.models.TripCategory
import com.android.tripbook.data.services.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Implementation of TripService that uses Room database as the data source
 */
class TripServiceImpl(private val tripDao: TripDao) : TripService {
    
    override suspend fun getAllTrips(): ServiceResult<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            val tripEntities = tripDao.getAllTrips().first()
            val trips = tripEntities.map { it.toDomainModel() }
            ServiceResult.Success(trips)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch trips: ${e.message}")
        }
    }
    
    override suspend fun getTripById(tripId: String): ServiceResult<Trip> = withContext(Dispatchers.IO) {
        try {
            val tripEntity = tripDao.getTripById(tripId) 
                ?: return@withContext ServiceResult.Error("Trip not found with ID: $tripId")
            ServiceResult.Success(tripEntity.toDomainModel())
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch trip: ${e.message}")
        }
    }
    
    override suspend fun searchTrips(query: String): ServiceResult<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            val tripEntities = tripDao.searchTrips(query).first()
            val trips = tripEntities.map { it.toDomainModel() }
            ServiceResult.Success(trips)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to search trips: ${e.message}")
        }
    }
    
    override suspend fun getTripsByCategory(category: TripCategory): ServiceResult<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            val tripEntities = tripDao.getTripsByCategory(category.name).first()
            val trips = tripEntities.map { it.toDomainModel() }
            ServiceResult.Success(trips)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch trips by category: ${e.message}")
        }
    }
    
    override suspend fun getTripsByPriceRange(minPrice: Double, maxPrice: Double): ServiceResult<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            val tripEntities = tripDao.getTripsByPriceRange(minPrice, maxPrice).first()
            val trips = tripEntities.map { it.toDomainModel() }
            ServiceResult.Success(trips)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch trips by price range: ${e.message}")
        }
    }
    
    override suspend fun getTripsByDateRange(startDate: LocalDate, endDate: LocalDate): ServiceResult<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            val tripEntities = tripDao.getTripsByDateRange(startDate, endDate).first()
            val trips = tripEntities.map { it.toDomainModel() }
            ServiceResult.Success(trips)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch trips by date range: ${e.message}")
        }
    }
    
    override suspend fun getFeaturedTrips(limit: Int): ServiceResult<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            val tripEntities = tripDao.getFeaturedTrips(limit).first()
            val trips = tripEntities.map { it.toDomainModel() }
            ServiceResult.Success(trips)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch featured trips: ${e.message}")
        }
    }
    
    override suspend fun getAllCategories(): ServiceResult<List<TripCategory>> = withContext(Dispatchers.IO) {
        try {
            val categories = tripDao.getAllCategories()
            val tripCategories = categories.map { TripCategory.valueOf(it) }
            ServiceResult.Success(tripCategories)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch categories: ${e.message}")
        }
    }
    
    override suspend fun getAllDepartureLocations(): ServiceResult<List<String>> = withContext(Dispatchers.IO) {
        try {
            val locations = tripDao.getAllDepartureLocations()
            ServiceResult.Success(locations)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch departure locations: ${e.message}")
        }
    }
    
    override suspend fun getAllDestinations(): ServiceResult<List<String>> = withContext(Dispatchers.IO) {
        try {
            val destinations = tripDao.getAllDestinations()
            ServiceResult.Success(destinations)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch destinations: ${e.message}")
        }
    }
}