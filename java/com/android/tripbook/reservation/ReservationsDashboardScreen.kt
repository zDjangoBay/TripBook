package com.tripbook.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripbook.reservation.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsDashboardScreen(
    onReservationClick: (String) -> Unit,
    viewModel: ReservationViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReservations()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Reservations") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            state.reservations.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No reservations found")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(state.reservations) { reservation ->
                        ReservationCard(
                            reservation = reservation,
                            onClick = { onReservationClick(reservation.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationCard(
    reservation: Reservation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Reservation #${reservation.id}",
                    style = MaterialTheme.typography.titleMedium
                )
                StatusChip(status = reservation.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Trip ID: ${reservation.tripId}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Total Amount: $${reservation.totalAmount}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Created: ${formatDate(reservation.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusChip(status: ReservationStatus) {
    val (backgroundColor, textColor) = when (status) {
        ReservationStatus.CONFIRMED -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        ReservationStatus.PENDING -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        ReservationStatus.CANCELLED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        ReservationStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
} 