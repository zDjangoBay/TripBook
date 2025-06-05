package com.android.tripbook.ui.screens.reservation

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.models.*
import com.android.tripbook.data.providers.DummyActivityProvider

/**
 * Fourth step: Summary and activity selection
 */
@Composable
fun SummaryStep(
    session: ReservationSession?,
    trip: Trip,
    onAddActivity: (ActivityOption) -> Unit,
    onRemoveActivity: (ActivityOption) -> Unit,
    onProceedToPayment: () -> Unit,
    onBack: () -> Unit
) {
    val activities = remember { DummyActivityProvider.getActivities() }
    var showActivityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Booking Summary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trip Info
            item {
                SummaryCard(
                    title = "Trip Details",
                    icon = Icons.Default.Flight
                ) {
                    Column {
                        SummaryRow("Destination", "${trip.fromLocation} → ${trip.toLocation}")
                        SummaryRow("Duration", trip.duration)
                        SummaryRow("Dates", "${trip.departureDate} - ${trip.returnDate}")
                    }
                }
            }

            // Transport Info
            session?.selectedTransport?.let { transport ->
                item {
                    SummaryCard(
                        title = "Transport",
                        icon = getTransportIcon(transport.type)
                    ) {
                        Column {
                            SummaryRow("Service", transport.name)
                            SummaryRow("Type", transport.type.name)
                            SummaryRow("Price", "$${String.format("%.0f", transport.price)}")
                        }
                    }
                }
            }

            // Hotel Info
            session?.selectedHotel?.let { hotel ->
                item {
                    SummaryCard(
                        title = "Accommodation",
                        icon = Icons.Default.Hotel
                    ) {
                        Column {
                            SummaryRow("Hotel", hotel.name)
                            SummaryRow("Room", hotel.roomType)
                            SummaryRow("Nights", "${session.hotelNights}")
                            SummaryRow("Total", "$${String.format("%.0f", hotel.pricePerNight * session.hotelNights)}")
                        }
                    }
                }
            }

            // Activities
            item {
                SummaryCard(
                    title = "Activities",
                    icon = Icons.Default.LocalActivity,
                    action = {
                        TextButton(onClick = { showActivityDialog = true }) {
                            Text("Add Activities")
                        }
                    }
                ) {
                    if (session?.selectedActivities?.isEmpty() == true) {
                        Text(
                            text = "No activities selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column {
                            session?.selectedActivities?.forEach { activity ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = activity.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "$${String.format("%.0f", activity.price)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { onRemoveActivity(activity) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Remove activity"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Total Cost
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Cost",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.2f", session?.totalCost ?: 0.0)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = onProceedToPayment,
                modifier = Modifier.weight(2f),
                enabled = session?.selectedTransport != null
            ) {
                Text("Proceed to Payment")
            }
        }
    }

    // Activity selection dialog
    if (showActivityDialog) {
        ActivitySelectionDialog(
            activities = activities,
            selectedActivities = session?.selectedActivities ?: emptyList(),
            onActivityToggle = { activity, isSelected ->
                if (isSelected) {
                    onAddActivity(activity)
                } else {
                    onRemoveActivity(activity)
                }
            },
            onDismiss = { showActivityDialog = false }
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                action?.invoke()
            }

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActivitySelectionDialog(
    activities: List<ActivityOption>,
    selectedActivities: List<ActivityOption>,
    onActivityToggle: (ActivityOption, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Activities",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activities) { activity ->
                        val isSelected = selectedActivities.contains(activity)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        onActivityToggle(activity, checked)
                                    }
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activity.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = activity.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                    Text(
                                        text = "${activity.duration} • $${String.format("%.0f", activity.price)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
            }
        }
    }
}

fun getTransportIcon(type: TransportType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        TransportType.PLANE -> Icons.Default.Flight
        TransportType.CAR -> Icons.Default.DirectionsCar
        TransportType.SHIP -> Icons.Default.DirectionsBoat
    }
}
