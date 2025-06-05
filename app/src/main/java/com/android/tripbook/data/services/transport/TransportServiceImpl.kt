package com.android.tripbook.data.services.transport

import com.android.tripbook.data.database.dao.TransportDao
import com.android.tripbook.data.models.TransportOption
import com.android.tripbook.data.models.TransportType
import com.android.tripbook.data.services.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * Implementation of TransportService that uses Room database as the data source
 */
class TransportServiceImpl(private val transportDao: TransportDao) : TransportService {
    
    override suspend fun getTransportOptionsForTrip(tripId: String): ServiceResult<List<TransportOption>> = withContext(Dispatchers.IO) {
        try {
            val transportEntities = transportDao.getTransportOptionsForTrip(tripId).first()
            val transportOptions = transportEntities.map { it.toDomainModel() }
            ServiceResult.Success(transportOptions)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch transport options: ${e.message}")
        }
    }
    
    override suspend fun getTransportOptionsByType(tripId: String, transportType: TransportType): ServiceResult<List<TransportOption>> = withContext(Dispatchers.IO) {
        try {
            val transportEntities = transportDao.getTransportOptionsByType(tripId, transportType.name).first()
            val transportOptions = transportEntities.map { it.toDomainModel() }
            ServiceResult.Success(transportOptions)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch transport options by type: ${e.message}")
        }
    }
    
    override suspend fun getTransportOptionById(transportId: String): ServiceResult<TransportOption> = withContext(Dispatchers.IO) {
        try {
            val transportEntity = transportDao.getTransportOptionById(transportId)
                ?: return@withContext ServiceResult.Error("Transport option not found with ID: $transportId")
            ServiceResult.Success(transportEntity.toDomainModel())
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch transport option: ${e.message}")
        }
    }
    
    override suspend fun getAvailableTransportOptions(tripId: String): ServiceResult<List<TransportOption>> = withContext(Dispatchers.IO) {
        try {
            val transportEntities = transportDao.getAvailableTransportOptions(tripId).first()
            val transportOptions = transportEntities.map { it.toDomainModel() }
            ServiceResult.Success(transportOptions)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch available transport options: ${e.message}")
        }
    }
    
    override suspend fun getTransportOptionsByPriceRange(
        tripId: String, 
        minPrice: Double, 
        maxPrice: Double
    ): ServiceResult<List<TransportOption>> = withContext(Dispatchers.IO) {
        try {
            val transportEntities = transportDao.getTransportOptionsByPriceRange(tripId, minPrice, maxPrice).first()
            val transportOptions = transportEntities.map { it.toDomainModel() }
            ServiceResult.Success(transportOptions)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch transport options by price range: ${e.message}")
        }
    }
    
    override suspend fun getTransportOptionsByTimeRange(
        tripId: String, 
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): ServiceResult<List<TransportOption>> = withContext(Dispatchers.IO) {
        try {
            val transportEntities = transportDao.getTransportOptionsByTimeRange(tripId, startTime, endTime).first()
            val transportOptions = transportEntities.map { it.toDomainModel() }
            ServiceResult.Success(transportOptions)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch transport options by time range: ${e.message}")
        }
    }
    
    override suspend fun getCheapestTransportOption(tripId: String): ServiceResult<TransportOption> = withContext(Dispatchers.IO) {
        try {
            val transportEntity = transportDao.getCheapestTransportOption(tripId)
                ?: return@withContext ServiceResult.Error("No transport options found for trip: $tripId")
            ServiceResult.Success(transportEntity.toDomainModel())
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch cheapest transport option: ${e.message}")
        }
    }
    
    override suspend fun getFastestTransportOption(tripId: String): ServiceResult<TransportOption> = withContext(Dispatchers.IO) {
        try {
            val transportEntity = transportDao.getFastestTransportOption(tripId)
                ?: return@withContext ServiceResult.Error("No transport options found for trip: $tripId")
            ServiceResult.Success(transportEntity.toDomainModel())
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch fastest transport option: ${e.message}")
        }
    }
    
    override suspend fun getAvailableTransportTypes(tripId: String): ServiceResult<List<TransportType>> = withContext(Dispatchers.IO) {
        try {
            val types = transportDao.getAvailableTransportTypes(tripId)
            val transportTypes = types.map { TransportType.valueOf(it) }
            ServiceResult.Success(transportTypes)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to fetch available transport types: ${e.message}")
        }
    }
    
    override suspend fun hasAvailableCapacity(transportId: String): ServiceResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val hasCapacity = transportDao.hasAvailableCapacity(transportId)
            ServiceResult.Success(hasCapacity)
        } catch (e: Exception) {
            ServiceResult.Error("Failed to check transport capacity: ${e.message}")
        }
    }
}