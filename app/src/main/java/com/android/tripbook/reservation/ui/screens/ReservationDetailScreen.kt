package com.android.tripbook.reservation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.reservation.data.ReservationRepository
import com.android.tripbook.reservation.model.*
import com.android.tripbook.reservation.ui.components.DetailItem
import com.android.tripbook.reservation.ui.components.SectionTitle
import com.android.tripbook.reservation.ui.components.StatusBadge
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Screen displaying detailed information about a reservation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservationId: String,
    onBackClick: () -> Unit,
    onCancelReservation: () -> Unit
) {
    val repository = remember { ReservationRepository() }
    // Wrap in try-catch to prevent crashes
    val reservation = remember {
        try {
            repository.getReservationById(reservationId)
        } catch (e: Exception) {
            // If there's an error, return null instead of crashing
            null
        }
    }

    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservation Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
        if (reservation == null) {
            // Reservation not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Reservation not found")
            }
        } else {
            // Reservation details
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = reservation.imageUrl,
                        contentDescription = "Reservation image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Status badge
                    StatusBadge(
                        status = reservation.status,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }

                // Content
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Title
                    Text(
                        text = reservation.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reservation ID
                    Text(
                        text = "Reservation ID: ${reservation.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reservation details
                    SectionTitle(title = "Reservation Details")

                    DetailItem(
                        icon = {
                            Icon(
                                imageVector = getIconForReservationType(reservation.type),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        label = "Type",
                        value = when(reservation.type) {
                            ReservationType.ACCOMMODATION -> "Accommodation"
                            ReservationType.TOUR -> "Tour"
                            ReservationType.ACTIVITY -> "Activity"
                        }
                    )

                    DetailItem(
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = "Location",
                        value = reservation.location
                    )

                    DetailItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = "Dates",
                        value = formatDateRange(reservation.startDate, reservation.endDate)
                    )

                    DetailItem(
                        icon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = "Price",
                        value = reservation.price
                    )

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    // Description
                    SectionTitle(title = "Description")

                    Text(
                        text = reservation.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Cancel button (only for pending or confirmed reservations)
                    if (reservation.status == ReservationStatus.PENDING ||
                        reservation.status == ReservationStatus.CONFIRMED) {
                        Button(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancel Reservation")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Cancel confirmation dialog
            if (showCancelDialog) {
                Dialog(
                    onDismissRequest = { showCancelDialog = false }
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Cancel Reservation",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Are you sure you want to cancel this reservation? This action cannot be undone.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showCancelDialog = false }
                                ) {
                                    Text("No, Keep It")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        showCancelDialog = false
                                        repository.cancelReservation(reservationId)
                                        onCancelReservation()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Yes, Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Get the appropriate icon for a reservation type
 */
@Composable
fun getIconForReservationType(type: ReservationType): ImageVector {
    return when (type) {
        ReservationType.ACCOMMODATION -> Icons.Default.Hotel
        ReservationType.TOUR -> Icons.Default.Map
        ReservationType.ACTIVITY -> Icons.Default.LocalActivity
    }
}

/**
 * Format a date range as a string
 */
fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d")
    return "${startDate.format(formatter)} - ${endDate.format(formatter)}, ${endDate.year}"
}
