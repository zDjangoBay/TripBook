package com.android.tripbook.util

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtil {

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "Name cannot be empty"
            )
            name.length < 2 -> ValidationResult(
                isValid = false,
                errorMessage = "Name is too short"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "Email cannot be empty"
            )
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(
                isValid = false,
                errorMessage = "Please enter a valid email address"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        // Password pattern: at least 8 chars, 1 letter, 1 number
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$"
        )

        return when {
            password.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "Password cannot be empty"
            )
            password.length < 8 -> ValidationResult(
                isValid = false,
                errorMessage = "Password must be at least 8 characters"
            )
            !passwordPattern.matcher(password).matches() -> ValidationResult(
                isValid = false,
                errorMessage = "Password must contain letters and at least one number"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    fun validateBio(bio: String, maxLength: Int = 150): ValidationResult {
        return when {
            bio.length > maxLength -> ValidationResult(
                isValid = false,
                errorMessage = "Bio cannot exceed $maxLength characters"
            )
            else -> ValidationResult(isValid = true)
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)
