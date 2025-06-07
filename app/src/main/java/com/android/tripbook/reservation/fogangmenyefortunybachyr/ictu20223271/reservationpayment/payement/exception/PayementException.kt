package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.exception

sealed class PaymentException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    class InvalidPaymentMethodException(message: String) : PaymentException(message)
    
    class InsufficientFundsException(message: String) : PaymentException(message)
    
    class PaymentDeclinedException(message: String) : PaymentException(message)
    
    class NetworkException(message: String, cause: Throwable? = null) : PaymentException(message, cause)
    
    class ValidationException(message: String) : PaymentException(message)
    
    class ProcessingException(message: String, cause: Throwable? = null) : PaymentException(message, cause)
    
    class RefundException(message: String) : PaymentException(message)
}