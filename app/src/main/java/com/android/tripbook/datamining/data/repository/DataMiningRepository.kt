package com.android.tripbook.datamining.data.repository

import com.android.tripbook.datamining.data.database.dao.DestinationDao
import com.android.tripbook.datamining.data.database.dao.TravelPatternDao
import com.android.tripbook.datamining.data.database.dao.UserPreferenceDao
import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.TravelPattern
import com.android.tripbook.datamining.data.database.entities.UserPreference
import com.android.tripbook.datamining.data.model.DestinationInsight
import com.android.tripbook.datamining.data.model.TravelInsight
import com.android.tripbook.datamining.data.model.UserInsight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

/**
 * Repository for data mining operations, providing access to insights derived from travel data
 */
class DataMiningRepository(
    private val destinationDao: DestinationDao,
    private val travelPatternDao: TravelPatternDao,
    private val userPreferenceDao: UserPreferenceDao
) {
    // Destination insights
    fun getTrendingDestinations(limit: Int = 10): Flow<List<DestinationInsight>> {
        return destinationDao.getTopDestinations(limit).map { destinations ->
            destinations.map { it.toDestinationInsight() }
        }
    }

    fun getDestinationsByRegion(region: String): Flow<List<DestinationInsight>> {
        return destinationDao.getDestinationsByRegion(region).map { destinations ->
            destinations.map { it.toDestinationInsight() }
        }
    }

    // Travel pattern insights
    fun getUserTravelPatterns(userId: String): Flow<List<TravelInsight>> {
        return travelPatternDao.getPatternsByUserId(userId).map { patterns ->
            patterns.map { it.toTravelInsight() }
        }
    }

    fun getGlobalTravelPatterns(): Flow<List<TravelInsight>> {
        return travelPatternDao.getPatternsByUserId("global").map { patterns ->
            patterns.map { it.toTravelInsight() }
        }
    }

    fun getSeasonalTravelPatterns(): Flow<List<TravelInsight>> {
        return travelPatternDao.getPatternsByType("seasonal").map { patterns ->
            patterns.map { it.toTravelInsight() }
        }
    }

    // User preference insights
    fun getUserPreferenceInsights(userId: String): Flow<List<UserInsight>> {
        return userPreferenceDao.getUserPreferences(userId).map { preferences ->
            preferences.map { it.toUserInsight() }
        }
    }

    // Data population methods for demonstration
    suspend fun populateSampleData() {
        // Clear existing data
        destinationDao.deleteAll()
        travelPatternDao.deleteAll()
        userPreferenceDao.deleteAll()

        // Insert sample destinations
        destinationDao.insertAll(getSampleDestinations())

        // Insert sample travel patterns
        travelPatternDao.insertAll(getSampleTravelPatterns())

        // Insert sample user preferences
        userPreferenceDao.insertAll(getSampleUserPreferences())
    }

    // Import sample data generators
    private fun getSampleDestinations() = com.android.tripbook.datamining.data.repository.getSampleDestinations()
    private fun getSampleTravelPatterns() = com.android.tripbook.datamining.data.repository.getSampleTravelPatterns()
    private fun getSampleUserPreferences() = com.android.tripbook.datamining.data.repository.getSampleUserPreferences()

    // Helper extension functions to convert entities to insight models
    private fun Destination.toDestinationInsight(): DestinationInsight {
        return DestinationInsight(
            id = this.id,
            name = this.name,
            country = this.country,
            region = this.region,
            popularity = this.popularity,
            rating = this.averageRating,
            imageUrl = this.imageUrl,
            description = this.description ?: "",
            tags = this.tags?.split(",")?.map { it.trim() } ?: emptyList()
        )
    }

    private fun TravelPattern.toTravelInsight(): TravelInsight {
        return TravelInsight(
            id = this.id,
            name = this.patternName,
            type = this.patternType,
            value = this.patternValue,
            confidence = this.confidence,
            startDate = this.startDate,
            endDate = this.endDate,
            data = this.patternData
        )
    }

    private fun UserPreference.toUserInsight(): UserInsight {
        return UserInsight(
            id = this.id,
            type = this.preferenceType,
            value = this.preferenceValue,
            strength = this.preferenceStrength,
            source = this.source
        )
    }
}
