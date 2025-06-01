package com.tripbook.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tripbook.reservation.viewmodel.Reservation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(
    reservation: Reservation,
    onViewReservationClick: () -> Unit,
    onBackToHomeClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Confirmed") },
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
                text = "Your booking has been confirmed!",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            BookingDetailsCard(reservation = reservation)

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBackToHomeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back to Home")
                }

                Button(
                    onClick = onViewReservationClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Reservation")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsCard(reservation: Reservation) {
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
                text = "Booking Details",
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

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 