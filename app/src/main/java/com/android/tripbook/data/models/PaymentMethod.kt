package com.android.tripbook.data.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.android.tripbook.ui.theme.AppIcons

data class PaymentMethod(
    val id: String,
    val name: String,
    val type: PaymentType,
    val lastFourDigits: String? = null,
    val icon: ImageVector,
    val cardBrand: CardBrand? = null,
    val phoneCountryCode: String? = null,
    val phoneNumber: String? = null
) {
    enum class PaymentType {
        CREDIT_CARD,
        DEBIT_CARD,
        ORANGE_MONEY,
        MTN_MOBILE_MONEY,
        BANK_TRANSFER
    }

    enum class CardBrand {
        VISA,
        MASTERCARD,
        AMERICAN_EXPRESS,
        DISCOVER
    }

    companion object {
        fun createDefaultMethods(): List<PaymentMethod> = listOf(
            PaymentMethod(
                id = "card_1",
                name = "Visa Credit Card",
                type = PaymentType.CREDIT_CARD,
                cardBrand = CardBrand.VISA,
                lastFourDigits = "4532",
                icon = AppIcons.CreditCard
            ),
            PaymentMethod(
                id = "card_2",
                name = "Mastercard Debit",
                type = PaymentType.DEBIT_CARD,
                cardBrand = CardBrand.MASTERCARD,
                lastFourDigits = "8901",
                icon = AppIcons.CreditCard
            ),
            PaymentMethod(
                id = "orange_1",
                name = "Orange Money",
                type = PaymentType.ORANGE_MONEY,
                phoneCountryCode = "+225",
                icon = AppIcons.Phone
            ),
            PaymentMethod(
                id = "mtn_1",
                name = "MTN Mobile Money",
                type = PaymentType.MTN_MOBILE_MONEY,
                phoneCountryCode = "+225",
                icon = AppIcons.Phone
            ),
            PaymentMethod(
                id = "bank_1",
                name = "Bank Transfer",
                type = PaymentType.BANK_TRANSFER,
                icon = AppIcons.AccountBalance
            )
        )

        fun validateCardNumber(number: String): CardBrand? {
            val cleanNumber = number.replace(" ", "").replace("-", "")
            return when {
                cleanNumber.startsWith("4") -> CardBrand.VISA
                cleanNumber.startsWith("5") -> CardBrand.MASTERCARD
                cleanNumber.startsWith("34") || cleanNumber.startsWith("37") -> CardBrand.AMERICAN_EXPRESS
                cleanNumber.startsWith("6") -> CardBrand.DISCOVER
                else -> null
            }
        }

        fun validateMobileNumber(number: String, countryCode: String): Boolean {
            // Remove non-digit characters
            val cleanNumber = number.replace(" ", "").replace("-", "")
            // Check if number starts with country code
            return cleanNumber.startsWith(countryCode) && cleanNumber.length >= 12
        }
    }
}
