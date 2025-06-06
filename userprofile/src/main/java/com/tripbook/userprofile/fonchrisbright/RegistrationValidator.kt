package com.tripbook.userprofile.fonchrisbright

object RegistrationValidator {
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 6
}

