package com.android.tripbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme

// Simple data classes for demo
data class SimplePaymentMethod(
    val id: String,
    val name: String,
    val type: String
)

data class SimpleTransaction(
    val id: String,
    val amount: Double,
    val currency: String,
    val method: SimplePaymentMethod,
    val status: String,
    val referenceNumber: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePaymentDemo() {
    var selectedMethod by remember { mutableStateOf<SimplePaymentMethod?>(null) }
    var amount by remember { mutableStateOf("100.0") }
    var cardNumber by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var transaction by remember { mutableStateOf<SimpleTransaction?>(null) }
    
    val paymentMethods = remember {
        listOf(
            SimplePaymentMethod("card_1", "Visa Credit Card", "CREDIT_CARD"),
            SimplePaymentMethod("card_2", "Mastercard Debit", "DEBIT_CARD"),
            SimplePaymentMethod("orange_1", "Orange Money", "ORANGE_MONEY"),
            SimplePaymentMethod("mtn_1", "MTN Mobile Money", "MTN_MOBILE_MONEY"),
            SimplePaymentMethod("bank_1", "Bank Transfer", "BANK_TRANSFER")
        )
    }

    TripBookTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Payment Demo - TripBook") },
                    navigationIcon = {
                        IconButton(onClick = { /* Back action */ }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showSuccess && transaction != null) {
                    PaymentSuccessDemo(
                        transaction = transaction!!,
                        onBack = { showSuccess = false }
                    )
                } else {
                    PaymentFormDemo(
                        paymentMethods = paymentMethods,
                        selectedMethod = selectedMethod,
                        amount = amount,
                        cardNumber = cardNumber,
                        mobileNumber = mobileNumber,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onMethodSelected = { method -> selectedMethod = method },
                        onAmountChanged = { newAmount -> amount = newAmount },
                        onCardNumberChanged = { number -> cardNumber = number },
                        onMobileNumberChanged = { number -> mobileNumber = number },
                        onPay = {
                            if (selectedMethod != null && amount.toDoubleOrNull() != null) {
                                isLoading = true
                                errorMessage = null
                                
                                // Simulate payment processing delay
                                transaction = SimpleTransaction(
                                    id = "TXN_${System.currentTimeMillis()}",
                                    amount = amount.toDouble(),
                                    currency = "XOF",
                                    method = selectedMethod!!,
                                    status = "SUCCESS",
                                    referenceNumber = "REF_${System.currentTimeMillis()}"
                                )
                                
                                isLoading = false
                                showSuccess = true
                            } else {
                                errorMessage = "Please select a payment method and enter a valid amount"
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentFormDemo(
    paymentMethods: List<SimplePaymentMethod>,
    selectedMethod: SimplePaymentMethod?,
    amount: String,
    cardNumber: String,
    mobileNumber: String,
    isLoading: Boolean,
    errorMessage: String?,
    onMethodSelected: (SimplePaymentMethod) -> Unit,
    onAmountChanged: (String) -> Unit,
    onCardNumberChanged: (String) -> Unit,
    onMobileNumberChanged: (String) -> Unit,
    onPay: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Select Payment Method",
            style = MaterialTheme.typography.headlineSmall
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(paymentMethods) { method ->
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .clickable { onMethodSelected(method) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMethod?.id == method.id) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = method.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = method.type,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChanged,
            label = { Text("Amount (XOF)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        selectedMethod?.let { method ->
            when (method.type) {
                "CREDIT_CARD", "DEBIT_CARD" -> {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = onCardNumberChanged,
                        label = { Text("Card Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                "ORANGE_MONEY", "MTN_MOBILE_MONEY" -> {
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = onMobileNumberChanged,
                        label = { Text("Mobile Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Button(
            onClick = onPay,
            enabled = !isLoading && selectedMethod != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "Processing..." else "Pay Now")
        }
    }
}

@Composable
fun PaymentSuccessDemo(
    transaction: SimpleTransaction,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        
        Text(
            text = "Payment Successful!",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Transaction Details", style = MaterialTheme.typography.titleMedium)
                Text("ID: ${transaction.id}")
                Text("Amount: ${transaction.amount} ${transaction.currency}")
                Text("Method: ${transaction.method.name}")
                Text("Status: ${transaction.status}")
                Text("Reference: ${transaction.referenceNumber}")
            }
        }
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Make Another Payment")
        }
    }
}
