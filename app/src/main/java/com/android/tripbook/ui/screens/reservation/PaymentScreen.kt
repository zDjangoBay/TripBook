package com.android.tripbook.ui.screens.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.data.models.PaymentMethod
import com.android.tripbook.data.models.PaymentStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    reservationId: String,
    onBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TripBookTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Payment") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
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
                if (uiState.showSuccess) {
                    PaymentSuccessScreen(
                        transaction = uiState.transaction!!,
                        onBack = onBack
                    )
                } else {
                    PaymentForm(
                        paymentMethods = uiState.paymentMethods,
                        selectedMethod = uiState.selectedMethod,
                        amount = uiState.amount,
                        cardNumber = uiState.cardNumber,
                        mobileNumber = uiState.mobileNumber,
                        isLoading = uiState.isLoading,
                        errorMessage = uiState.errorMessage,
                        onMethodSelected = { method ->
                            viewModel._uiState.update { it.copy(selectedMethod = method) }
                        },
                        onAmountChanged = { amount ->
                            viewModel._uiState.update { it.copy(amount = amount) }
                        },
                        onCardNumberChanged = { number ->
                            viewModel._uiState.update { it.copy(cardNumber = number) }
                        },
                        onMobileNumberChanged = { number ->
                            viewModel._uiState.update { it.copy(mobileNumber = number) }
                        },
                        onPay = {
                            viewModel.processPayment(
                                amount = uiState.amount,
                                selectedMethod = uiState.selectedMethod!!,
                                reservationId = reservationId,
                                cardNumber = uiState.cardNumber,
                                mobileNumber = uiState.mobileNumber
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentForm(
    paymentMethods: List<PaymentMethod>,
    selectedMethod: PaymentMethod?,
    amount: Double,
    cardNumber: String,
    mobileNumber: String,
    isLoading: Boolean,
    errorMessage: String?,
    onMethodSelected: (PaymentMethod) -> Unit,
    onAmountChanged: (Double) -> Unit,
    onCardNumberChanged: (String) -> Unit,
    onMobileNumberChanged: (String) -> Unit,
    onPay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Enter Payment Details",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = amount.toString(),
            onValueChange = { 
                try {
                    onAmountChanged(it.toDouble())
                } catch (e: Exception) {
                    // Handle invalid input
                }
            },
            label = { Text("Amount (XOF)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Select Payment Method",
            style = MaterialTheme.typography.titleMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(paymentMethods) { method ->
                PaymentMethodCard(
                    method = method,
                    isSelected = method == selectedMethod,
                    onClick = { onMethodSelected(method) }
                )
            }
        }

        when (selectedMethod?.type) {
            PaymentMethod.PaymentType.CREDIT_CARD,
            PaymentMethod.PaymentType.DEBIT_CARD -> {
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = onCardNumberChanged,
                    label = { Text("Card Number") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            PaymentMethod.PaymentType.ORANGE_MONEY,
            PaymentMethod.PaymentType.MTN_MOBILE_MONEY -> {
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = onMobileNumberChanged,
                    label = { Text("Mobile Number") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {}
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onPay,
            enabled = selectedMethod != null && amount > 0 && 
                (selectedMethod.type == PaymentMethod.PaymentType.BANK_TRANSFER ||
                (selectedMethod.type == PaymentMethod.PaymentType.CREDIT_CARD ||
                selectedMethod.type == PaymentMethod.PaymentType.DEBIT_CARD) && cardNumber.isNotBlank() ||
                (selectedMethod.type == PaymentMethod.PaymentType.ORANGE_MONEY ||
                selectedMethod.type == PaymentMethod.PaymentType.MTN_MOBILE_MONEY) && mobileNumber.isNotBlank()),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Pay Now")
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .size(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = method.icon,
                contentDescription = method.name,
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = method.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PaymentSuccessScreen(
    transaction: PaymentTransaction,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        
        Text(
            "Payment Successful!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        PaymentDetails(
            transaction = transaction,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
private fun PaymentDetails(
    transaction: PaymentTransaction,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Transaction Details",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        PaymentDetailItem(
            label = "Amount",
            value = "${transaction.amount} XOF"
        )
        
        PaymentDetailItem(
            label = "Payment Method",
            value = transaction.paymentMethod.name
        )
        
        PaymentDetailItem(
            label = "Transaction ID",
            value = transaction.id
        )
        
        PaymentDetailItem(
            label = "Reference Number",
            value = transaction.referenceNumber
        )
    }
}

@Composable
private fun PaymentDetailItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
