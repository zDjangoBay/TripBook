package com.example.tripbooktest.utils

object StringUtils {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }
}
