// android/app/src/test/java/com/tripbook/TransportationRepositoryTest.kt
package com.tripbook

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*

class TransportationRepositoryTest {
    private lateinit var mockService: TransportationService
    private lateinit var repository: TransportationRepository

    @Before
    fun setUp() {
        mockService = mock(TransportationService::class.java)
        repository = TransportationRepository(mockService)
    }

    @Test
    fun `fetchBusSchedules returns non-empty list when service has data`() {
        // Arrange
        val mockSchedules = listOf(
            BusSchedule(
                id = "1",
                operator = "Test Bus",
                departureTime = "10:00 AM",
                arrivalTime = "2:00 PM",
                price = "XAF 4,000",
                duration = "4h"
            )
        )
        `when`(mockService.getBusSchedules()).thenReturn(mockSchedules)

        // Act
        val result = repository.fetchBusSchedules()

        // Assert
        assertTrue(result.isNotEmpty())
        assertEquals(mockSchedules.size, result.size)
        assertEquals("Test Bus", result[0].operator)
    }

    @Test
    fun `fetchBusSchedules returns empty list when service has no data`() {
        // Arrange
        `when`(mockService.getBusSchedules()).thenReturn(emptyList())

        // Act
        val result = repository.fetchBusSchedules()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `bookBus returns true when service succeeds`() {
        // Arrange
        val testBusId = "1"
        `when`(mockService.bookTicket(testBusId)).thenReturn(true)

        // Act
        val result = repository.bookBus(testBusId)

        // Assert
        assertTrue(result)
    }

    @Test(expected = IllegalStateException::class)
    fun `bookBus throws when service fails`() {
        // Arrange
        val testBusId = "invalid_id"
        `when`(mockService.bookTicket(testBusId))
            .thenThrow(IllegalStateException("Booking failed"))

        // Act & Assert
        repository.bookBus(testBusId)
    }
}