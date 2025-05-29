package com.tripbook.otp

object OtpGenerator {
    fun generateOtp(length: Int = 6): String {
        return (1..length)
            .map { ('0'..'9').random() }
            .joinToString("")
    }
}