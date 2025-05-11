package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationStatus
import com.android.tripbook.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Dialog for editing a reservation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReservationDialog(
    reservation: Reservation,
    onDismiss: () -> Unit,
    onSave: (Reservation) -> Unit
) {
    var title by remember { mutableStateOf(reservation.title) }
    var destination by remember { mutableStateOf(reservation.destination) }
    var accommodationName by remember { mutableStateOf(reservation.accommodationName ?: "") }
    var accommodationAddress by remember { mutableStateOf(reservation.accommodationAddress ?: "") }
    var transportInfo by remember { mutableStateOf(reservation.transportInfo ?: "") }
    var price by remember { mutableStateOf(reservation.price.toString()) }
    var bookingReference by remember { mutableStateOf(reservation.bookingReference) }
    var notes by remember { mutableStateOf(reservation.notes ?: "") }
    var selectedStatus by remember { mutableStateOf(reservation.status) }

    // Format dates for display
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var startDateText by remember { mutableStateOf(reservation.startDate.format(dateFormatter)) }
    var endDateText by remember { mutableStateOf(reservation.endDate.format(dateFormatter)) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Reservation",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Destination
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startDateText,
                            onValueChange = { startDateText = it },
                            label = { Text("Start Date (yyyy-MM-dd HH:mm)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = endDateText,
                            onValueChange = { endDateText = it },
                            label = { Text("End Date (yyyy-MM-dd HH:mm)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReservationStatus.values().forEach { status ->
                            FilterChip(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status },
                                label = {
                                    Text(status.name.lowercase().replaceFirstChar { it.uppercase() })
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (status) {
                                        ReservationStatus.CONFIRMED -> StatusConfirmed.copy(alpha = 0.2f)
                                        ReservationStatus.PENDING -> StatusPending.copy(alpha = 0.2f)
                                        ReservationStatus.CANCELLED -> StatusCancelled.copy(alpha = 0.2f)
                                        ReservationStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.2f)
                                    },
                                    selectedLabelColor = when (status) {
                                        ReservationStatus.CONFIRMED -> StatusConfirmed
                                        ReservationStatus.PENDING -> StatusPending
                                        ReservationStatus.CANCELLED -> StatusCancelled
                                        ReservationStatus.COMPLETED -> StatusCompleted
                                    }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price and booking reference
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = bookingReference,
                            onValueChange = { bookingReference = it },
                            label = { Text("Booking Reference") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Accommodation
                    OutlinedTextField(
                        value = accommodationName,
                        onValueChange = { accommodationName = it },
                        label = { Text("Accommodation Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = accommodationAddress,
                        onValueChange = { accommodationAddress = it },
                        label = { Text("Accommodation Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Transport
                    OutlinedTextField(
                        value = transportInfo,
                        onValueChange = { transportInfo = it },
                        label = { Text("Transport Information") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            try {
                                // Parse dates
                                val startDate = LocalDateTime.parse(startDateText, dateFormatter)
                                val endDate = LocalDateTime.parse(endDateText, dateFormatter)

                                // Create updated reservation
                                val updatedReservation = reservation.copy(
                                    title = title,
                                    destination = destination,
                                    startDate = startDate,
                                    endDate = endDate,
                                    status = selectedStatus,
                                    price = price.toDoubleOrNull() ?: reservation.price,
                                    bookingReference = bookingReference,
                                    accommodationName = accommodationName.ifBlank { null },
                                    accommodationAddress = accommodationAddress.ifBlank { null },
                                    transportInfo = transportInfo.ifBlank { null },
                                    notes = notes.ifBlank { null }
                                )

                                onSave(updatedReservation)
                            } catch (e: Exception) {
                                // Handle date parsing errors
                                // In a real app, show an error message
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TripBookPrimary
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
