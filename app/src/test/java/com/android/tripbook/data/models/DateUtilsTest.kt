package com.android.tripbook.data.models

import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.ItineraryType
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Unit tests for DateUtils to verify safe date parsing functionality
 */
class DateUtilsTest {

    @Test
    fun `parseLocalDateSafely should parse valid ISO date`() {
        val dateString = "2024-03-15"
        val expected = LocalDate.of(2024, 3, 15)
        val result = DateUtils.parseLocalDateSafely(dateString)
        assertEquals(expected, result)
    }

    @Test
    fun `parseLocalDateSafely should handle null input with fallback`() {
        val fallback = LocalDate.of(2024, 1, 1)
        val result = DateUtils.parseLocalDateSafely(null, fallback)
        assertEquals(fallback, result)
    }

    @Test
    fun `parseLocalDateSafely should handle empty string with fallback`() {
        val fallback = LocalDate.of(2024, 1, 1)
        val result = DateUtils.parseLocalDateSafely("", fallback)
        assertEquals(fallback, result)
    }

    @Test
    fun `parseLocalDateSafely should handle invalid format with fallback`() {
        val fallback = LocalDate.of(2024, 1, 1)
        val result = DateUtils.parseLocalDateSafely("invalid-date", fallback)
        assertEquals(fallback, result)
    }

    @Test
    fun `parseLocalDateSafely should parse alternative date formats`() {
        val testCases = mapOf(
            "03/15/2024" to LocalDate.of(2024, 3, 15),
            "15/03/2024" to LocalDate.of(2024, 3, 15),
            "2024/03/15" to LocalDate.of(2024, 3, 15),
            "15-03-2024" to LocalDate.of(2024, 3, 15),
            "03-15-2024" to LocalDate.of(2024, 3, 15)
        )

        testCases.forEach { (input, expected) ->
            val result = DateUtils.parseLocalDateSafely(input)
            assertEquals("Failed to parse $input", expected, result)
        }
    }

    @Test
    fun `parseLocalDateTimeSafely should parse valid ISO datetime`() {
        val dateTimeString = "2024-03-15T14:30:00"
        val expected = LocalDateTime.of(2024, 3, 15, 14, 30, 0)
        val result = DateUtils.parseLocalDateTimeSafely(dateTimeString)
        assertEquals(expected, result)
    }

    @Test
    fun `parseLocalDateTimeSafely should handle space separator`() {
        val dateTimeString = "2024-03-15 14:30:00"
        val expected = LocalDateTime.of(2024, 3, 15, 14, 30, 0)
        val result = DateUtils.parseLocalDateTimeSafely(dateTimeString)
        assertEquals(expected, result)
    }

    @Test
    fun `parseLocalDateTimeSafely should handle null input with fallback`() {
        val fallback = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
        val result = DateUtils.parseLocalDateTimeSafely(null, fallback)
        assertEquals(fallback, result)
    }

    @Test
    fun `parseLocalDateTimeSafely should handle invalid format with fallback`() {
        val fallback = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
        val result = DateUtils.parseLocalDateTimeSafely("invalid-datetime", fallback)
        assertEquals(fallback, result)
    }

    @Test
    fun `parseLocalDateTimeSafely should parse alternative datetime formats`() {
        val testCases = mapOf(
            "2024-03-15 14:30:00" to LocalDateTime.of(2024, 3, 15, 14, 30, 0),
            "2024-03-15 14:30" to LocalDateTime.of(2024, 3, 15, 14, 30, 0),
            "03/15/2024 14:30:00" to LocalDateTime.of(2024, 3, 15, 14, 30, 0),
            "15/03/2024 14:30:00" to LocalDateTime.of(2024, 3, 15, 14, 30, 0)
        )

        testCases.forEach { (input, expected) ->
            val result = DateUtils.parseLocalDateTimeSafely(input)
            assertEquals("Failed to parse $input", expected, result)
        }
    }

    @Test
    fun `formatLocalDateSafely should format valid date`() {
        val date = LocalDate.of(2024, 3, 15)
        val expected = "2024-03-15"
        val result = DateUtils.formatLocalDateSafely(date)
        assertEquals(expected, result)
    }

    @Test
    fun `formatLocalDateSafely should handle null input`() {
        val result = DateUtils.formatLocalDateSafely(null)
        assertNotNull(result)
        assertTrue("Result should be a valid date string", result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `formatLocalDateTimeSafely should format valid datetime`() {
        val dateTime = LocalDateTime.of(2024, 3, 15, 14, 30, 0)
        val expected = "2024-03-15T14:30:00"
        val result = DateUtils.formatLocalDateTimeSafely(dateTime)
        assertEquals(expected, result)
    }

    @Test
    fun `formatLocalDateTimeSafely should handle null input`() {
        val result = DateUtils.formatLocalDateTimeSafely(null)
        assertNotNull(result)
        assertTrue("Result should be a valid datetime string", 
            result.matches(Regex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")))
    }

    @Test
    fun `parseEnumSafely should parse valid enum values`() {
        val tripStatus = DateUtils.parseEnumSafely("PLANNED", TripStatus.COMPLETED, "TripStatus", TripStatus::class.java)
        assertEquals(TripStatus.PLANNED, tripStatus)

        val tripCategory = DateUtils.parseEnumSafely("ADVENTURE", TripCategory.RELAXATION, "TripCategory", TripCategory::class.java)
        assertEquals(TripCategory.ADVENTURE, tripCategory)

        val itineraryType = DateUtils.parseEnumSafely("ACTIVITY", ItineraryType.ACCOMMODATION, "ItineraryType", ItineraryType::class.java)
        assertEquals(ItineraryType.ACTIVITY, itineraryType)
    }

    @Test
    fun `parseEnumSafely should handle invalid enum values with fallback`() {
        val tripStatus = DateUtils.parseEnumSafely("INVALID_STATUS", TripStatus.PLANNED, "TripStatus", TripStatus::class.java)
        assertEquals(TripStatus.PLANNED, tripStatus)

        val tripCategory = DateUtils.parseEnumSafely("INVALID_CATEGORY", TripCategory.RELAXATION, "TripCategory", TripCategory::class.java)
        assertEquals(TripCategory.RELAXATION, tripCategory)

        val itineraryType = DateUtils.parseEnumSafely("INVALID_TYPE", ItineraryType.ACTIVITY, "ItineraryType", ItineraryType::class.java)
        assertEquals(ItineraryType.ACTIVITY, itineraryType)
    }

    @Test
    fun `parseEnumSafely should handle null input with fallback`() {
        val tripStatus = DateUtils.parseEnumSafely(null, TripStatus.PLANNED, "TripStatus", TripStatus::class.java)
        assertEquals(TripStatus.PLANNED, tripStatus)
    }

    @Test
    fun `parseEnumSafely should handle empty string with fallback`() {
        val tripStatus = DateUtils.parseEnumSafely("", TripStatus.PLANNED, "TripStatus", TripStatus::class.java)
        assertEquals(TripStatus.PLANNED, tripStatus)
    }

    @Test
    fun `serialization deserialization should work with malformed backend data`() {
        // Simulate malformed backend data
        val malformedTrip = SupabaseTrip(
            name = "Test Trip",
            start_date = "15/03/2024", // Non-ISO format
            end_date = "invalid-date", // Invalid date
            destination = "Test Destination",
            travelers = 2,
            budget = 1000,
            status = "INVALID_STATUS", // Invalid enum
            category = "ADVENTURE", // Valid enum
            description = "Test description"
        )

        // Should not throw exception and use fallbacks
        val trip = malformedTrip.toTrip()
        
        assertNotNull(trip)
        assertEquals("Test Trip", trip.name)
        assertEquals(LocalDate.of(2024, 3, 15), trip.startDate) // Parsed alternative format
        assertNotNull(trip.endDate) // Should use fallback (current date)
        assertEquals(TripStatus.PLANNED, trip.status) // Should use fallback
        assertEquals(TripCategory.ADVENTURE, trip.category) // Should parse correctly
    }

    @Test
    fun `serialization should work with valid data`() {
        val trip = Trip(
            id = "test-id",
            name = "Test Trip",
            startDate = LocalDate.of(2024, 3, 15),
            endDate = LocalDate.of(2024, 3, 20),
            destination = "Test Destination",
            travelers = 2,
            budget = 1000,
            status = TripStatus.PLANNED,
            category = TripCategory.ADVENTURE,
            description = "Test description"
        )

        val supabaseTrip = SupabaseTrip.fromTrip(trip)
        
        assertEquals("2024-03-15", supabaseTrip.start_date)
        assertEquals("2024-03-20", supabaseTrip.end_date)
        assertEquals("PLANNED", supabaseTrip.status)
        assertEquals("ADVENTURE", supabaseTrip.category)
    }
}
