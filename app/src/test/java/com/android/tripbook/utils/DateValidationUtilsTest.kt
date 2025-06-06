package com.android.tripbook.utils

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

/**
 * Unit tests for DateValidationUtils to verify smart date selection logic
 */
class DateValidationUtilsTest {

    @Test
    fun `handleStartDateSelection with no end date should switch to end date selection`() {
        val startDate = LocalDate.of(2024, 6, 15)
        val result = DateValidationUtils.handleStartDateSelection(startDate, null)
        
        assertEquals(startDate, result.startDate)
        assertNull(result.endDate)
        assertTrue(result.shouldSwitchToEndDate)
        assertEquals("Now select your end date", result.message)
        assertTrue(result.isValid)
    }

    @Test
    fun `handleStartDateSelection after end date should reset end date`() {
        val startDate = LocalDate.of(2024, 6, 20)
        val currentEndDate = LocalDate.of(2024, 6, 15) // End date is before new start date
        
        val result = DateValidationUtils.handleStartDateSelection(startDate, currentEndDate)
        
        assertEquals(startDate, result.startDate)
        assertNull(result.endDate)
        assertTrue(result.shouldSwitchToEndDate)
        assertEquals("End date reset because start date was moved later. Please select a new end date.", result.message)
        assertTrue(result.isValid)
    }

    @Test
    fun `handleStartDateSelection equal to end date should suggest extending trip`() {
        val startDate = LocalDate.of(2024, 6, 15)
        val currentEndDate = LocalDate.of(2024, 6, 15) // Same date
        
        val result = DateValidationUtils.handleStartDateSelection(startDate, currentEndDate)
        
        assertEquals(startDate, result.startDate)
        assertEquals(currentEndDate, result.endDate)
        assertTrue(result.shouldSwitchToEndDate)
        assertEquals("Your trip is only one day. Consider selecting a later end date.", result.message)
        assertTrue(result.isValid)
    }

    @Test
    fun `handleStartDateSelection before end date should keep end date`() {
        val startDate = LocalDate.of(2024, 6, 10)
        val currentEndDate = LocalDate.of(2024, 6, 15)
        
        val result = DateValidationUtils.handleStartDateSelection(startDate, currentEndDate)
        
        assertEquals(startDate, result.startDate)
        assertEquals(currentEndDate, result.endDate)
        assertFalse(result.shouldSwitchToEndDate)
        assertNull(result.message)
        assertTrue(result.isValid)
    }

    @Test
    fun `handleEndDateSelection with no start date should be invalid`() {
        val endDate = LocalDate.of(2024, 6, 15)
        val result = DateValidationUtils.handleEndDateSelection(endDate, null)
        
        assertNull(result.startDate)
        assertNull(result.endDate)
        assertFalse(result.isValid)
        assertEquals("Please select a start date first", result.message)
    }

    @Test
    fun `handleEndDateSelection before start date should be invalid`() {
        val startDate = LocalDate.of(2024, 6, 15)
        val endDate = LocalDate.of(2024, 6, 10) // Before start date
        
        val result = DateValidationUtils.handleEndDateSelection(endDate, startDate)
        
        assertEquals(startDate, result.startDate)
        assertNull(result.endDate)
        assertFalse(result.isValid)
        assertTrue(result.message!!.contains("End date cannot be before start date"))
    }

    @Test
    fun `handleEndDateSelection with valid date should succeed`() {
        val startDate = LocalDate.of(2024, 6, 10)
        val endDate = LocalDate.of(2024, 6, 15)
        
        val result = DateValidationUtils.handleEndDateSelection(endDate, startDate)
        
        assertEquals(startDate, result.startDate)
        assertEquals(endDate, result.endDate)
        assertTrue(result.isValid)
        assertTrue(result.message!!.contains("6 days"))
    }

    @Test
    fun `handleEndDateSelection with maximum duration exceeded should be invalid`() {
        val startDate = LocalDate.of(2024, 6, 1)
        val endDate = LocalDate.of(2024, 6, 20) // 20 days
        val maxDuration = 10L
        
        val result = DateValidationUtils.handleEndDateSelection(endDate, startDate, maxDuration)
        
        assertEquals(startDate, result.startDate)
        assertNull(result.endDate)
        assertFalse(result.isValid)
        assertEquals("Trip duration cannot exceed 10 days", result.message)
    }

    @Test
    fun `validateDateRange with valid dates should succeed`() {
        val startDate = LocalDate.of(2024, 6, 10)
        val endDate = LocalDate.of(2024, 6, 15)
        
        val result = DateValidationUtils.validateDateRange(startDate, endDate)
        
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
        assertEquals(6L, result.duration)
    }

    @Test
    fun `validateDateRange with null start date should fail`() {
        val endDate = LocalDate.of(2024, 6, 15)
        
        val result = DateValidationUtils.validateDateRange(null, endDate)
        
        assertFalse(result.isValid)
        assertEquals("Start date is required", result.errorMessage)
    }

    @Test
    fun `validateDateRange with null end date should fail`() {
        val startDate = LocalDate.of(2024, 6, 10)
        
        val result = DateValidationUtils.validateDateRange(startDate, null)
        
        assertFalse(result.isValid)
        assertEquals("End date is required", result.errorMessage)
    }

    @Test
    fun `validateDateRange with start after end should fail`() {
        val startDate = LocalDate.of(2024, 6, 15)
        val endDate = LocalDate.of(2024, 6, 10)
        
        val result = DateValidationUtils.validateDateRange(startDate, endDate)
        
        assertFalse(result.isValid)
        assertEquals("Start date must be before or equal to end date", result.errorMessage)
    }

    @Test
    fun `validateDateRange with single day when not allowed should fail`() {
        val date = LocalDate.of(2024, 6, 15)
        
        val result = DateValidationUtils.validateDateRange(date, date, allowSingleDay = false)
        
        assertFalse(result.isValid)
        assertEquals("Trip must be more than one day", result.errorMessage)
    }

    @Test
    fun `validateDateRange with past start date should fail`() {
        val startDate = LocalDate.now().minusDays(1)
        val endDate = LocalDate.now().plusDays(1)
        
        val result = DateValidationUtils.validateDateRange(startDate, endDate)
        
        assertFalse(result.isValid)
        assertEquals("Start date cannot be in the past", result.errorMessage)
    }

    @Test
    fun `suggestEndDate should provide appropriate suggestions`() {
        val startDate = LocalDate.of(2024, 6, 10)
        
        // Test different trip types
        assertEquals(LocalDate.of(2024, 6, 12), DateValidationUtils.suggestEndDate(startDate, "business"))
        assertEquals(LocalDate.of(2024, 6, 12), DateValidationUtils.suggestEndDate(startDate, "weekend"))
        assertEquals(LocalDate.of(2024, 6, 17), DateValidationUtils.suggestEndDate(startDate, "week"))
        assertEquals(LocalDate.of(2024, 6, 17), DateValidationUtils.suggestEndDate(startDate, "vacation"))
        assertEquals(LocalDate.of(2024, 6, 24), DateValidationUtils.suggestEndDate(startDate, "extended"))
        assertEquals(LocalDate.of(2024, 6, 13), DateValidationUtils.suggestEndDate(startDate, null)) // default 3 days
    }

    @Test
    fun `suggestEndDate with custom default duration should work`() {
        val startDate = LocalDate.of(2024, 6, 10)
        val customDuration = 5L
        
        val result = DateValidationUtils.suggestEndDate(startDate, null, customDuration)
        
        assertEquals(LocalDate.of(2024, 6, 15), result)
    }

    @Test
    fun `date selection flow simulation should work correctly`() {
        // Simulate user selecting start date first
        val startDate = LocalDate.of(2024, 6, 10)
        val startResult = DateValidationUtils.handleStartDateSelection(startDate, null)
        
        assertTrue(startResult.shouldSwitchToEndDate)
        assertEquals("Now select your end date", startResult.message)
        
        // Then selecting a valid end date
        val endDate = LocalDate.of(2024, 6, 15)
        val endResult = DateValidationUtils.handleEndDateSelection(endDate, startResult.startDate)
        
        assertTrue(endResult.isValid)
        assertEquals(startDate, endResult.startDate)
        assertEquals(endDate, endResult.endDate)
        assertTrue(endResult.message!!.contains("6 days"))
    }

    @Test
    fun `date selection with conflict resolution should work`() {
        // User has existing end date
        val existingEndDate = LocalDate.of(2024, 6, 15)
        
        // User selects start date after existing end date
        val newStartDate = LocalDate.of(2024, 6, 20)
        val result = DateValidationUtils.handleStartDateSelection(newStartDate, existingEndDate)
        
        // End date should be reset
        assertEquals(newStartDate, result.startDate)
        assertNull(result.endDate)
        assertTrue(result.shouldSwitchToEndDate)
        assertTrue(result.message!!.contains("End date reset"))
    }
}
