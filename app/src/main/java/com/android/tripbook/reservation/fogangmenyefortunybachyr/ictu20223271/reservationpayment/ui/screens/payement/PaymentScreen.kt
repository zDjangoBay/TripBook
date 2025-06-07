package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.screens.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

// Mock payment status enum
enum class PaymentStatus {
    PENDING, PROCESSING, SUCCEEDED, FAILED
}

// Mock payment response
data class PaymentResponse(
    val id: String,
    val amount: BigDecimal,
    val status: PaymentStatus,
    val message: String? = null
)

// Mock credit card data
data class CreditCardData(
    val cardNumber: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val cvv: String = "",
    val holderName: String = ""
)

// Mock payment UI state
data class PaymentUiState(
    val selectedPaymentMethod: String = "credit_card",
    val cardData: CreditCardData = CreditCardData(),
    val isProcessing: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val paymentResult: PaymentResponse? = null
)

// Mock payment viewmodel
class PaymentViewModel : androidx.lifecycle.ViewModel() {
    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(PaymentUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<PaymentUiState> = _uiState.asStateFlow()
    
    fun selectPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }
    
    fun updateCardData(cardData: CreditCardData) {
        _uiState.value = _uiState.value.copy(cardData = cardData)
    }
    
    fun processPayment(reservationId: String, amount: BigDecimal) {
        // Mock payment processing
    }
    
    fun processPayPalPayment() {
        // Mock PayPal processing
    }
    
    fun processGooglePayPayment(token: String) {
        // Mock Google Pay processing
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    reservationId: String,
    amount: BigDecimal,
    onPaymentSuccess: () -> Unit,
    onPaymentFailure: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PaymentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(key1 = uiState.paymentResult) {
        uiState.paymentResult?.let { result ->
            when (result.status) {
                PaymentStatus.SUCCEEDED -> onPaymentSuccess()
                PaymentStatus.FAILED -> onPaymentFailure(result.message ?: "Payment failed")
                else -> {}
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Payment Summary
            PaymentSummaryCard(amount = amount)
            
            // Payment Method Selection
            PaymentMethodSection(
                selectedMethod = uiState.selectedPaymentMethod,
                onMethodSelected = { viewModel.selectPaymentMethod(it) }
            )
            
            // Payment Form based on selected method
            when (uiState.selectedPaymentMethod) {
                "credit_card" -> {
                    CreditCardForm(
                        cardData = uiState.cardData,
                        onCardDataChanged = { viewModel.updateCardData(it) }
                    )
                }
                "paypal" -> {
                    PayPalForm(
                        onPayPalLogin = { viewModel.processPayPalPayment() }
                    )
                }
                "google_pay" -> {
                    GooglePayButton(
                        onGooglePayClicked = { viewModel.processGooglePayPayment("mock_token") }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Process Payment Button
            LoadingButton(
                text = "Pay $${amount}",
                isLoading = uiState.isProcessing,
                onClick = { viewModel.processPayment(reservationId, amount) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isFormValid
            )
            
            // Error Display
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSummaryCard(amount: BigDecimal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Payment Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$$amount",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodSection(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Select Payment Method",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaymentMethodCard(
                title = "Credit Card",
                subtitle = "Visa, Mastercard, Amex",
                icon = Icons.Default.CreditCard,
                isSelected = selectedMethod == "credit_card",
                onClick = { onMethodSelected("credit_card") }
            )
            
            PaymentMethodCard(
                title = "PayPal",
                subtitle = "Pay with your PayPal account",
                icon = Icons.Default.Payment,
                isSelected = selectedMethod == "paypal",
                onClick = { onMethodSelected("paypal") }
            )
            
            PaymentMethodCard(
                title = "Google Pay",
                subtitle = "Quick and secure",
                icon = Icons.Default.AccountBalance,
                isSelected = selectedMethod == "google_pay",
                onClick = { onMethodSelected("google_pay") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) 
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CreditCardForm(
    cardData: CreditCardData,
    onCardDataChanged: (CreditCardData) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Credit Card Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = cardData.cardNumber,
            onValueChange = { onCardDataChanged(cardData.copy(cardNumber = it)) },
            label = { Text("Card Number") },
            placeholder = { Text("1234 5678 9012 3456") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = cardData.expiryMonth,
                onValueChange = { onCardDataChanged(cardData.copy(expiryMonth = it)) },
                label = { Text("MM") },
                placeholder = { Text("12") },
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = cardData.expiryYear,
                onValueChange = { onCardDataChanged(cardData.copy(expiryYear = it)) },
                label = { Text("YY") },
                placeholder = { Text("25") },
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = cardData.cvv,
                onValueChange = { onCardDataChanged(cardData.copy(cvv = it)) },
                label = { Text("CVV") },
                placeholder = { Text("123") },
                modifier = Modifier.weight(1f)
            )
        }
        
        OutlinedTextField(
            value = cardData.holderName,
            onValueChange = { onCardDataChanged(cardData.copy(holderName = it)) },
            label = { Text("Cardholder Name") },
            placeholder = { Text("John Doe") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PayPalForm(onPayPalLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PayPal Payment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onPayPalLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login to PayPal")
        }
    }
}

@Composable
fun GooglePayButton(onGooglePayClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Google Pay",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onGooglePayClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay with Google Pay")
        }
    }
}

@Composable
fun LoadingButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text)
    }
}