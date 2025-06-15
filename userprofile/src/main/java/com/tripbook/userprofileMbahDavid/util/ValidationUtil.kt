package com.tripbook.userprofileMbahDavid.util

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

object ValidationUtil {
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Name cannot be empty")
            name.length < 2 -> ValidationResult(false, "Name is too short")
            else -> ValidationResult(true)
        }
    }

    fun validateEmail(email: String): ValidationResult {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return when {
            email.isBlank() -> ValidationResult(false, "Email cannot be empty")
            !email.matches(emailRegex) -> ValidationResult(false, "Invalid email format")
            else -> ValidationResult(true)
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult(false, "Phone number cannot be empty")
            phone.length < 10 -> ValidationResult(false, "Phone number is too short")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.length < 8 -> ValidationResult(false, "Password must be at least 8 characters")
            !password.any { it.isDigit() } -> ValidationResult(false, "Password must contain at least one number")
            !password.any { it.isLetter() } -> ValidationResult(false, "Password must contain at least one letter")
            else -> ValidationResult(true)
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword != password -> ValidationResult(false, "Passwords do not match")
            else -> ValidationResult(true)
        }
    }

    fun validateBio(bio: String): ValidationResult {
        return when {
            bio.length > 500 -> ValidationResult(false, "Bio must be less than 500 characters")
            else -> ValidationResult(true)
        }
    }
}