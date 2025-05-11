package com.android.tripbook.datamining

import com.android.tripbook.datamining.data.database.dao.DestinationDao
import com.android.tripbook.datamining.data.database.dao.TravelPatternDao
import com.android.tripbook.datamining.data.database.dao.UserPreferenceDao
import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.TravelPattern
import com.android.tripbook.datamining.data.database.entities.UserPreference
import com.android.tripbook.datamining.data.repository.DataMiningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Date

/**
 * Unit tests for the DataMiningRepository
 */
class DataMiningRepositoryTest {
    
    // Mock DAOs
    private lateinit var mockDestinationDao: DestinationDao
    private lateinit var mockTravelPatternDao: TravelPatternDao
    private lateinit var mockUserPreferenceDao: UserPreferenceDao
    
    // Repository to test
    private lateinit var repository: DataMiningRepository
    
    // Sample data
    private val sampleDestinations = listOf(
        Destination(
            id = 1,
            name = "Test Destination",
            country = "Test Country",
            region = "Test Region",
            popularity = 85.0f,
            averageRating = 4.5f,
            visitCount = 1000,
            lastUpdated = Date(),
            latitude = 0.0,
            longitude = 0.0
        )
    )
    
    private val sampleTravelPatterns = listOf(
        TravelPattern(
            id = 1,
            userId = "user123",
            patternType = "seasonal",
            patternName = "Test Pattern",
            patternValue = 0.8f,
            patternData = "{}",
            startDate = Date(),
            endDate = Date(),
            confidence = 0.9f,
            sampleSize = 100
        )
    )
    
    private val sampleUserPreferences = listOf(
        UserPreference(
            id = 1,
            userId = "user123",
            preferenceType = "destination_type",
            preferenceValue = "beach",
            preferenceStrength = 0.8f,
            lastUpdated = Date(),
            source = "explicit",
            confidence = 0.9f
        )
    )
    
    @Before
    fun setup() {
        // Initialize mocks
        mockDestinationDao = mock(DestinationDao::class.java)
        mockTravelPatternDao = mock(TravelPatternDao::class.java)
        mockUserPreferenceDao = mock(UserPreferenceDao::class.java)
        
        // Set up mock behavior
        `when`(mockDestinationDao.getTopDestinations(10)).thenReturn(flowOf(sampleDestinations))
        `when`(mockTravelPatternDao.getPatternsByUserId("user123")).thenReturn(flowOf(sampleTravelPatterns))
        `when`(mockUserPreferenceDao.getUserPreferences("user123")).thenReturn(flowOf(sampleUserPreferences))
        
        // Initialize repository with mocks
        repository = DataMiningRepository(
            destinationDao = mockDestinationDao,
            travelPatternDao = mockTravelPatternDao,
            userPreferenceDao = mockUserPreferenceDao
        )
    }
    
    @Test
    fun `getTrendingDestinations returns mapped destination insights`() = runBlocking {
        // Act
        val result = repository.getTrendingDestinations(10)
        
        // Assert
        verify(mockDestinationDao).getTopDestinations(10)
        
        // Collect and verify the result
        result.collect { insights ->
            assert(insights.size == 1)
            assert(insights[0].name == "Test Destination")
            assert(insights[0].country == "Test Country")
            assert(insights[0].popularity == 85.0f)
        }
    }
    
    @Test
    fun `getUserTravelPatterns returns mapped travel insights`() = runBlocking {
        // Act
        val result = repository.getUserTravelPatterns("user123")
        
        // Assert
        verify(mockTravelPatternDao).getPatternsByUserId("user123")
        
        // Collect and verify the result
        result.collect { insights ->
            assert(insights.size == 1)
            assert(insights[0].name == "Test Pattern")
            assert(insights[0].type == "seasonal")
            assert(insights[0].confidence == 0.9f)
        }
    }
    
    @Test
    fun `getUserPreferenceInsights returns mapped user insights`() = runBlocking {
        // Act
        val result = repository.getUserPreferenceInsights("user123")
        
        // Assert
        verify(mockUserPreferenceDao).getUserPreferences("user123")
        
        // Collect and verify the result
        result.collect { insights ->
            assert(insights.size == 1)
            assert(insights[0].type == "destination_type")
            assert(insights[0].value == "beach")
            assert(insights[0].strength == 0.8f)
        }
    }
}
