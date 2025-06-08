package com.tripbook.userprofilesunjo.domain.repository

import com.tripbook.userprofilesunjo.domain.model.*

interface PasswordRecoveryRepository {
    suspend fun requestPasswordReset(email: String): Result<PasswordResetResponse>
    suspend fun verifyOtp(email: String, otp: String): Result<OtpVerificationResponse>
    suspend fun resetPassword(
        email: String,
        token: String,
        newPassword: String
    ): Result<ResetPasswordResponse>
}