package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.model

sealed class PaymentMethod(val type: String) {
    data class CreditCard(
        val cardNumber: String,
        val expiryMonth: String,
        val expiryYear: String,
        val cvv: String,
        val holderName: String
    ) : PaymentMethod("credit_card")
    
    data class PayPal(
        val email: String
    ) : PaymentMethod("paypal")
    
    data class GooglePay(
        val token: String
    ) : PaymentMethod("google_pay")
    
    data class ApplePay(
        val token: String
    ) : PaymentMethod("apple_pay")
}