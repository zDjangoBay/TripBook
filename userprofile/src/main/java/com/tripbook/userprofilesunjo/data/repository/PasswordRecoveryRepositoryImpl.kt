package com.tripbook.userprofilesunjo.data.repository

import com.tripbook.userprofilesunjo.domain.model.*
import com.tripbook.userprofilesunjo.domain.repository.PasswordRecoveryRepository
import com.tripbook.userprofilesunjo.util.OtpStorage
import kotlinx.coroutines.delay

class PasswordRecoveryRepositoryImpl : PasswordRecoveryRepository {

    override suspend fun requestPasswordReset(email: String): Result<PasswordResetResponse> {
        return try {
            // Simulate network call delay
            delay(1000)

            // In real implementation, this would be handled by the ForgotPasswordScreen
            // which sends the actual email. Here we just return success.
            Result.success(
                PasswordResetResponse(
                    message = "Password reset email sent successfully",
                    otpSent = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): Result<OtpVerificationResponse> {
        return try {
            // Simulate network call delay
            delay(1000)

            // Use the real OTP verification
            if (OtpStorage.verifyOtp(email, otp)) {
                // Generate a reset token
                val resetToken = "reset_token_${System.currentTimeMillis()}_${email.hashCode()}"

                Result.success(
                    OtpVerificationResponse(
                        message = "OTP verified successfully",
                        token = resetToken,
                        isValid = true
                    )
                )
            } else {
                Result.failure(Exception("Invalid OTP or OTP has expired"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(
        email: String,
        token: String,
        newPassword: String
    ): Result<ResetPasswordResponse> {
        return try {
            // Simulate network call delay
            delay(1500)

            // In real implementation, validate the token and update password
            // For demo, we just clear the OTP and return success
            OtpStorage.clearOtp()

            Result.success(
                ResetPasswordResponse(
                    message = "Password reset successfully! You can now login with your new password.",
                    isReset = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
