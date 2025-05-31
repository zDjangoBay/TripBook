package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import com.android.tripbook.data.managers.ReservationSessionManager
import com.android.tripbook.data.managers.UserSessionManager
import com.android.tripbook.data.services.FakeNotificationDispatcher
import com.android.tripbook.data.services.MockPaymentProcessor
import com.android.tripbook.TripBookApplication
import kotlinx.coroutines.delay

/**
 * Payment screen for processing reservation payments
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    reservationId: String,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val paymentProcessor = remember { MockPaymentProcessor.getInstance() }
    val notificationDispatcher = remember { FakeNotificationDispatcher.getInstance() }
    val context = LocalContext.current
    val application = context.applicationContext as TripBookApplication
    val userSessionManager = remember {
        UserSessionManager.getInstance(context, application.database)
    }
    val sessionManager = remember {
        ReservationSessionManager.getInstance(context, application.database, userSessionManager)
    }

    val paymentMethods = remember { paymentProcessor.getAvailablePaymentMethods() }
    var selectedPaymentMethod by remember { mutableStateOf<MockPaymentProcessor.PaymentMethod?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var shouldProcessPayment by remember { mutableStateOf(false) }

    val reservations by sessionManager.reservations.collectAsState()
    val reservation = reservations.find { it.id == reservationId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payment",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Payment summary
            reservation?.let { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = res.trip.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "${res.trip.fromLocation} → ${res.trip.toLocation}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format("%.2f", res.totalCost)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment methods
            Text(
                text = "Select Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(paymentMethods) { method ->
                    PaymentMethodCard(
                        paymentMethod = method,
                        isSelected = selectedPaymentMethod == method,
                        onSelect = { selectedPaymentMethod = method }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pay button
            Button(
                onClick = {
                    selectedPaymentMethod?.let { method ->
                        reservation?.let { res ->
                            isProcessing = true
                            shouldProcessPayment = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedPaymentMethod != null && !isProcessing,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isProcessing) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...")
                    }
                } else {
                    Text(
                        text = "Pay $${String.format("%.2f", reservation?.totalCost ?: 0.0)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Payment processing effect
    LaunchedEffect(shouldProcessPayment) {
        if (shouldProcessPayment) {
            val paymentMethod = selectedPaymentMethod
            val currentReservation = reservation
            if (paymentMethod != null && currentReservation != null) {
                try {
                    val result = paymentProcessor.processPayment(
                        amount = currentReservation.totalCost,
                        paymentMethod = paymentMethod,
                        reservationId = reservationId
                    )

                isProcessing = false
                shouldProcessPayment = false

                if (result.success) {
                    // Update reservation status
                    sessionManager.updatePaymentStatus(
                        reservationId,
                        com.android.tripbook.data.models.PaymentStatus.COMPLETED
                    )
                    sessionManager.updateReservationStatus(
                        reservationId,
                        com.android.tripbook.data.models.ReservationStatus.UPCOMING
                    )

                    // Send notification
                    notificationDispatcher.sendPaymentSuccessNotification(
                        reservationId,
                        currentReservation.totalCost
                    )
                    notificationDispatcher.sendBookingConfirmedNotification(
                        currentReservation.trip.title
                    )

                    showSuccessDialog = true
                } else {
                    errorMessage = result.errorMessage ?: "Payment failed"
                    notificationDispatcher.sendPaymentFailedNotification(reservationId)
                    showErrorDialog = true
                }
                } catch (e: Exception) {
                    isProcessing = false
                    shouldProcessPayment = false
                    errorMessage = "An error occurred during payment"
                    showErrorDialog = true
                }
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        PaymentResultDialog(
            isSuccess = true,
            message = "Payment successful! Your booking has been confirmed.",
            onDismiss = {
                showSuccessDialog = false
                onPaymentSuccess()
            }
        )
    }

    // Error dialog
    if (showErrorDialog) {
        PaymentResultDialog(
            isSuccess = false,
            message = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodCard(
    paymentMethod: MockPaymentProcessor.PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getPaymentIcon(paymentMethod.type),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethod.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                paymentMethod.lastFourDigits?.let { digits ->
                    Text(
                        text = "•••• $digits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
fun PaymentResultDialog(
    isSuccess: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isSuccess) "Success!" else "Payment Failed",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSuccess) "Continue" else "Try Again")
                }
            }
        }
    }
}

fun getPaymentIcon(type: MockPaymentProcessor.PaymentType): ImageVector {
    return when (type) {
        MockPaymentProcessor.PaymentType.CREDIT_CARD -> Icons.Default.CreditCard
        MockPaymentProcessor.PaymentType.DEBIT_CARD -> Icons.Default.CreditCard
        MockPaymentProcessor.PaymentType.MOBILE_MONEY -> Icons.Default.Phone
        MockPaymentProcessor.PaymentType.BANK_TRANSFER -> Icons.Default.AccountBalance
    }
}
