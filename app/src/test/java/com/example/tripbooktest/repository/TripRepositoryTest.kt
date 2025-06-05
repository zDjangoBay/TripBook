package com.example.tripbooktest.data.repository

import com.example.tripbooktest.data.ApiService
import com.example.tripbooktest.data.Trip
import com.example.tripbooktest.data.TripRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TripRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var tripRepository: TripRepository

    @Before
    fun setup() {
        apiService = mock()
        tripRepository = TripRepository(apiService)
    }

    @Test
    fun `fetchTrips returns list of trips`() = runTest {
        val mockTrips = listOf(
            Trip(id = "1", destination = "Paris"),
            Trip(id = "2", destination = "Rome")
        )
        `when`(apiService.getTrips()).thenReturn(mockTrips)

        val result = tripRepository.fetchTrips()

        assertEquals(mockTrips, result)
        verify(apiService).getTrips()
    }
}
