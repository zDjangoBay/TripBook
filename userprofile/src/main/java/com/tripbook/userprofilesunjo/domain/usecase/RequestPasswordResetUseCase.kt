package com.tripbook.userprofilesunjo.domain.usecase

import com.tripbook.userprofilesunjo.domain.model.PasswordResetResponse
import com.tripbook.userprofilesunjo.domain.repository.PasswordRecoveryRepository

class RequestPasswordResetUseCase(
    private val repository: PasswordRecoveryRepository
) {
    suspend operator fun invoke(email: String): Result<PasswordResetResponse> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        return repository.requestPasswordReset(email)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
