package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.util

import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoUtils @Inject constructor() {
    
    fun generateSecureToken(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
    
    fun hashCardNumber(cardNumber: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(cardNumber.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
    
    fun maskCardNumber(cardNumber: String): String {
        if (cardNumber.length < 4) return cardNumber
        val lastFour = cardNumber.takeLast(4)
        return "**** **** **** $lastFour"
    }
    
    fun validateCardNumber(cardNumber: String): Boolean {
        // Algorithme de Luhn simplifié
        val digits = cardNumber.replace(" ", "").map { it.digitToIntOrNull() ?: return false }
        if (digits.size < 13 || digits.size > 19) return false
        
        var sum = 0
        var isEven = false
        
        for (i in digits.size - 1 downTo 0) {
            var digit = digits[i]
            if (isEven) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            sum += digit
            isEven = !isEven
        }
        
        return sum % 10 == 0
    }
}