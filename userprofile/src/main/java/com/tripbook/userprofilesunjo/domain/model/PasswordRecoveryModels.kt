package com.tripbook.userprofilesunjo.domain.model

data class PasswordResetRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

data class PasswordResetResponse(
    val message: String,
    val otpSent: Boolean = false
)

data class OtpVerificationResponse(
    val message: String,
    val token: String? = null,
    val isValid: Boolean = false
)

data class ResetPasswordResponse(
    val message: String,
    val isReset: Boolean = false
)