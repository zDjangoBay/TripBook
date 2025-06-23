package com.tripbook.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripbook.reservation.viewmodel.PaymentViewModel
import com.tripbook.reservation.viewmodel.PaymentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInformationScreen(
    amount: Double,
    reservationId: String,
    onPaymentComplete: () -> Unit,
    viewModel: PaymentViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var selectedPaymentType by remember { mutableStateOf(PaymentType.CREDIT_CARD) }

    LaunchedEffect(state.paymentStatus) {
        if (state.paymentStatus == PaymentStatus.PAID) {
            onPaymentComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Information") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Amount to Pay: $$amount",
                style = MaterialTheme.typography.headlineSmall
            )

            PaymentTypeSelector(
                selectedType = selectedPaymentType,
                onTypeSelected = { selectedPaymentType = it }
            )

            when (selectedPaymentType) {
                PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { if (it.length <= 16) cardNumber = it },
                        label = { Text("Card Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { if (it.length <= 5) expiryDate = it },
                            label = { Text("MM/YY") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { if (it.length <= 3) cvv = it },
                            label = { Text("CVV") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = cardHolderName,
                        onValueChange = { cardHolderName = it },
                        label = { Text("Card Holder Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                PaymentType.MOBILE_MONEY -> {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                PaymentType.BANK_TRANSFER -> {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Account Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    val paymentMethod = PaymentMethod(
                        id = "temp_${System.currentTimeMillis()}",
                        type = selectedPaymentType,
                        lastFourDigits = cardNumber.takeLast(4),
                        expiryDate = expiryDate
                    )
                    viewModel.processPayment(amount, paymentMethod, reservationId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !state.isLoading && validateInputs(
                    selectedPaymentType,
                    cardNumber,
                    expiryDate,
                    cvv,
                    cardHolderName
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Pay Now")
                }
            }
        }
    }
}

@Composable
fun PaymentTypeSelector(
    selectedType: PaymentType,
    onTypeSelected: (PaymentType) -> Unit
) {
    Column {
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentType.values().forEach { type ->
                FilterChip(
                    selected = type == selectedType,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.name.replace("_", " ")) }
                )
            }
        }
    }
}

private fun validateInputs(
    paymentType: PaymentType,
    cardNumber: String,
    expiryDate: String,
    cvv: String,
    cardHolderName: String
): Boolean {
    return when (paymentType) {
        PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> {
            cardNumber.length == 16 &&
            expiryDate.matches(Regex("\\d{2}/\\d{2}")) &&
            cvv.length == 3 &&
            cardHolderName.isNotBlank()
        }
        PaymentType.MOBILE_MONEY -> {
            cardNumber.length >= 10
        }
        PaymentType.BANK_TRANSFER -> {
            cardNumber.length >= 8
        }
    }
} 