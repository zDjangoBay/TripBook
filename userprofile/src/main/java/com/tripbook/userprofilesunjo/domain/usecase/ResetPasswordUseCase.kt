package com.tripbook.userprofilesunjo.domain.usecase

import com.tripbook.userprofilesunjo.domain.model.ResetPasswordResponse
import com.tripbook.userprofilesunjo.domain.repository.PasswordRecoveryRepository

class ResetPasswordUseCase(
    private val repository: PasswordRecoveryRepository
) {
    suspend operator fun invoke(
        email: String,
        token: String,
        newPassword: String,
        confirmPassword: String
    ): Result<ResetPasswordResponse> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }

        if (token.isBlank()) {
            return Result.failure(IllegalArgumentException("Token cannot be empty"))
        }

        if (newPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }

        if (newPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
        }

        if (newPassword != confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match"))
        }

        if (!isPasswordValid(newPassword)) {
            return Result.failure(IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"))
        }

        return repository.resetPassword(email, token, newPassword)
    }

    private fun isPasswordValid(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar
    }
}