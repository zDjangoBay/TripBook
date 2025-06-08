package com.tripbook.userprofilesunjo.domain.usecase

import com.tripbook.userprofilesunjo.domain.model.OtpVerificationResponse
import com.tripbook.userprofilesunjo.domain.repository.PasswordRecoveryRepository

class VerifyOtpUseCase(
    private val repository: PasswordRecoveryRepository
) {
    suspend operator fun invoke(email: String, otp: String): Result<OtpVerificationResponse> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }

        if (otp.isBlank()) {
            return Result.failure(IllegalArgumentException("OTP cannot be empty"))
        }

        if (otp.length != 6) {
            return Result.failure(IllegalArgumentException("OTP must be 6 digits"))
        }

        return repository.verifyOtp(email, otp)
    }
}