package com.android.tripbook.reservation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tripbook.reservation.data.ReservationRepository
import com.android.tripbook.reservation.model.ReservationType
import com.android.tripbook.reservation.ui.components.ReservationCard

/**
 * Screen displaying a list of user's reservations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListScreen(
    onReservationClick: (String) -> Unit,
    onNewBookingClick: (String) -> Unit
) {
    val repository = remember { ReservationRepository() }
    // Wrap in try-catch to prevent crashes
    val reservations = remember {
        try {
            repository.getUserReservations()
        } catch (e: Exception) {
            // If there's an error, return an empty list instead of crashing
            emptyList()
        }
    }

    var showBookingDialog by remember { mutableStateOf(false) }

    @Composable
    fun BookingTypeButton(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        text: String,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBookingDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Booking"
                )
            }
        }
    ) { paddingValues ->
        if (reservations.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Luggage, // Changed from NoLuggage which might not be available
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Reservations Yet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Book your first trip to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showBookingDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Book Now")
                    }
                }
            }
        } else {
            // Reservation list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reservations) { reservation ->
                    ReservationCard(
                        reservation = reservation,
                        onClick = { onReservationClick(reservation.id) }
                    )
                }

                // Add some space at the bottom for the FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Booking type selection dialog
        if (showBookingDialog) {
            Dialog(
                onDismissRequest = { showBookingDialog = false }
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
                        Text(
                            text = "What would you like to book?",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Choose the type of reservation you want to make.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        BookingTypeButton(
                            icon = Icons.Default.Hotel,
                            text = "Accommodation",
                            onClick = {
                                showBookingDialog = false
                                onNewBookingClick(ReservationType.ACCOMMODATION.name)
                            }
                        )

                        BookingTypeButton(
                            icon = Icons.Default.Tour,
                            text = "Tour",
                            onClick = {
                                showBookingDialog = false
                                onNewBookingClick(ReservationType.TOUR.name)
                            }
                        )

                        BookingTypeButton(
                            icon = Icons.Default.LocalActivity,
                            text = "Activity",
                            onClick = {
                                showBookingDialog = false
                                onNewBookingClick(ReservationType.ACTIVITY.name)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { showBookingDialog = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}
