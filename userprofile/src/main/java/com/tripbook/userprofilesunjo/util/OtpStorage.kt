package com.tripbook.userprofilesunjo.util

object OtpStorage {
    private var currentOtp: String = ""
    private var otpEmail: String = ""
    private var otpTimestamp: Long = 0L

    fun storeOtp(email: String, otp: String) {
        otpEmail = email
        currentOtp = otp
        otpTimestamp = System.currentTimeMillis()
    }

    fun verifyOtp(email: String, otp: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val otpAge = currentTime - otpTimestamp
        val tenMinutesInMillis = 10 * 60 * 1000L // 10 minutes

        return email == otpEmail &&
                otp == currentOtp &&
                otpAge <= tenMinutesInMillis
    }

    fun clearOtp() {
        currentOtp = ""
        otpEmail = ""
        otpTimestamp = 0L
    }
}