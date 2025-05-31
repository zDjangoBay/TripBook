package com.tripbook.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tripbook.reservation.viewmodel.Reservation
import com.tripbook.reservation.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancellationScreen(
    reservation: Reservation,
    onCancellationComplete: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ReservationViewModel
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var cancellationReason by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cancel Reservation") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Are you sure you want to cancel this reservation?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            ReservationDetailsCard(reservation = reservation)

            OutlinedTextField(
                value = cancellationReason,
                onValueChange = { cancellationReason = it },
                label = { Text("Reason for Cancellation") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text(
                text = "Cancellation Policy:\n" +
                    "• Cancellations made 48 hours before the trip are fully refundable\n" +
                    "• Cancellations made 24-48 hours before the trip are 50% refundable\n" +
                    "• Cancellations made less than 24 hours before the trip are non-refundable",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = cancellationReason.isNotBlank() && !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Cancel Reservation")
                    }
                }
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Cancellation") },
            text = { Text("Are you sure you want to cancel this reservation? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        isProcessing = true
                        viewModel.cancelReservation(reservation.id)
                        onCancellationComplete()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReservationDetailsCard(reservation: Reservation) {
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
                text = "Reservation Details",
                style = MaterialTheme.typography.titleMedium
            )

            DetailRow("Booking ID", reservation.id)
            DetailRow("Trip ID", reservation.tripId)
            DetailRow("Status", reservation.status.name)
            DetailRow("Total Amount", "$${reservation.totalAmount}")
            DetailRow("Payment Status", reservation.paymentStatus.name)
            DetailRow("Seats", reservation.seats.joinToString(", ") { it.seatNumber })
        }
    }
} 