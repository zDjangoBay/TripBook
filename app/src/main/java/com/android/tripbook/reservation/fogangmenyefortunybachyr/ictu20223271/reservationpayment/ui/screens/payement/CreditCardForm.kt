package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.payement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.util.CryptoUtils

data class CreditCardData(
    val cardNumber: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val cvv: String = "",
    val holderName: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardForm(
    cardData: CreditCardData,
    onCardDataChanged: (CreditCardData) -> Unit,
    modifier: Modifier = Modifier
) {
    val cryptoUtils = remember { CryptoUtils() }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card Holder Name
        OutlinedTextField(
            value = cardData.holderName,
            onValueChange = { onCardDataChanged(cardData.copy(holderName = it)) },
            label = { Text("Cardholder Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Card Number
        OutlinedTextField(
            value = cardData.cardNumber,
            onValueChange = { input ->
                val cleaned = input.replace(" ", "").take(16)
                onCardDataChanged(cardData.copy(cardNumber = cleaned))
            },
            label = { Text("Card Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CardNumberVisualTransformation(),
            singleLine = true,
            isError = cardData.cardNumber.isNotEmpty() && !cryptoUtils.validateCardNumber(cardData.cardNumber)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Expiry Month
            OutlinedTextField(
                value = cardData.expiryMonth,
                onValueChange = { input ->
                    val month = input.take(2).filter { it.isDigit() }
                    onCardDataChanged(cardData.copy(expiryMonth = month))
                },
                label = { Text("MM") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            // Expiry Year
            OutlinedTextField(
                value = cardData.expiryYear,
                onValueChange = { input ->
                    val year = input.take(2).filter { it.isDigit() }
                    onCardDataChanged(cardData.copy(expiryYear = year))
                },
                label = { Text("YY") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            // CVV
            OutlinedTextField(
                value = cardData.cvv,
                onValueChange = { input ->
                    val cvv = input.take(4).filter { it.isDigit() }
                    onCardDataChanged(cardData.copy(cvv = cvv))
                },
                label = { Text("CVV") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }
}

class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }
        
        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString(out),
            androidx.compose.ui.text.input.OffsetMapping.Identity
        )
    }
}