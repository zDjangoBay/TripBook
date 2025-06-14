package com.example.tripbooktest.utils

import org.junit.Assert.*
import org.junit.Test

class StringUtilsTest {

    @Test
    fun testValidEmail() {
        val validEmail = "user@example.com"
        assertTrue(StringUtils.isEmailValid(validEmail))
    }

    @Test
    fun testInvalidEmail() {
        val invalidEmail = "invalid-email"
        assertFalse(StringUtils.isEmailValid(invalidEmail))
    }
}
