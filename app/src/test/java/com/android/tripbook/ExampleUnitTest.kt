package com.android.tripbook

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        // Test simple addition
        assertEquals(4, 2 + 2)
    }

    @Test
    fun subtraction_isCorrect() {
        // Test simple subtraction
        assertEquals(0, 2 - 2)
    }

    @Test
    fun multiplication_isCorrect() {
        // Test multiplication
        assertEquals(6, 2 * 3)
    }

    @Test
    fun division_isCorrect() {
        // Test division
        assertEquals(2, 6 / 3)
    }

    @Test
    fun stringConcatenation_isCorrect() {
        // Test string concatenation
        val result = "Hello, " + "World!"
        assertEquals("Hello, World!", result)
    }

    @Test
    fun listOperations() {
        // Test list operations
        val list = mutableListOf(1, 2, 3)
        list.add(4)
        assertEquals(4, list.size)
        assertTrue(list.contains(2))
        list.remove(2)
        assertFalse(list.contains(2))
    }

    @Test
    fun validateOTP() {
        // Test OTP validation
        val generatedOTP = "123456"
        val inputOTP = "123456"
        assertTrue(validateOTP(inputOTP, generatedOTP))
        
        val wrongOTP = "654321"
        assertFalse(validateOTP(wrongOTP, generatedOTP))
    }

    private fun validateOTP(input: String, generated: String): Boolean {
        // Simple OTP validation logic
        return input == generated
    }
}
