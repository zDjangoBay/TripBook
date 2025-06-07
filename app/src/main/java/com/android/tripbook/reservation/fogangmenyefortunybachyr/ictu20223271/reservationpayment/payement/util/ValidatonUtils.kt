package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.util

import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.exception.PaymentException
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.PaymentMethod
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model.PaymentRequest
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidationUtils @Inject constructor() {
    
    fun validatePaymentRequest(request: PaymentRequest) {
        if (request.amount <= BigDecimal.ZERO) {
            throw PaymentException.ValidationException("Amount must be positive")
        }
        
        if (request.currency.length != 3) {
            throw PaymentException.ValidationException("Invalid currency code")
        }
        
        if (request.reservationId.isBlank()) {
            throw PaymentException.ValidationException("Reservation ID is required")
        }
        
        if (request.customerId.isBlank()) {
            throw PaymentException.ValidationException("Customer ID is required")
        }
    }
    
    fun validateCreditCard(card: PaymentMethod.CreditCard): Boolean {
        // Luhn algorithm for card number validation
        if (!isValidCardNumber(card.cardNumber)) return false
        
        // CVV validation
        if (card.cvv.length !in 3..4 || !card.cvv.all { it.isDigit() }) return false
        
        // Expiry validation
        val currentYear = java.time.LocalDate.now().year % 100
        val currentMonth = java.time.LocalDate.now().monthValue
        
        val expiryYear = card.expiryYear.toIntOrNull() ?: return false
        val expiryMonth = card.expiryMonth.toIntOrNull() ?: return false
        
        if (expiryMonth !in 1..12) return false
        if (expiryYear < currentYear || (expiryYear == currentYear && expiryMonth < currentMonth)) return false
        
        return true
    }
    
    private fun isValidCardNumber(cardNumber: String): Boolean {
        val digits = cardNumber.replace("\\s".toRegex(), "").map { it.toString().toInt() }
        
        if (digits.size < 13 || digits.size > 19) return false
        
        var sum = 0
        var alternate = false
        
        for (i in digits.size - 1 downTo 0) {
            var n = digits[i]
            
            if (alternate) {
                n *= 2
                if (n > 9) n = (n % 10) + 1
            }
            
            sum += n
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }
}