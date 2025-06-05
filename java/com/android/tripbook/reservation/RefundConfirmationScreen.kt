package com.tripbook.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tripbook.reservation.viewmodel.PaymentStatus
import com.tripbook.reservation.viewmodel.Reservation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefundConfirmationScreen(
    reservation: Reservation,
    refundAmount: Double,
    onBackToHomeClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Refund Confirmation") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Refund Processed Successfully!",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            RefundDetailsCard(
                reservation = reservation,
                refundAmount = refundAmount
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackToHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Back to Home")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefundDetailsCard(
    reservation: Reservation,
    refundAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Refund Details",
                style = MaterialTheme.typography.titleMedium
            )

            DetailRow("Original Amount", "$${reservation.totalAmount}")
            DetailRow("Refund Amount", "$$refundAmount")
            DetailRow("Payment Method", reservation.paymentStatus.name)
            DetailRow("Refund Status", "Processed")
            DetailRow("Estimated Time", "3-5 business days")
            
            Text(
                text = "The refund will be processed to your original payment method. Please allow 3-5 business days for the amount to reflect in your account.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
} 