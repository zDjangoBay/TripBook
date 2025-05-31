package com.tripbook.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    tripId: String,
    onBookNowClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedSeats by remember { mutableStateOf(1) }
    val pricePerSeat = 150.0 // This would come from your data source
    val totalPrice = pricePerSeat * selectedSeats

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Trip Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // TODO: Add trip image
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Trip Image")
                    }
                }
            }

            // Trip Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Safari Adventure in Serengeti",
                    style = MaterialTheme.typography.headlineSmall
                )

                TripInfoRow(
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    content = "Serengeti National Park, Tanzania"
                )

                TripInfoRow(
                    icon = Icons.Default.DateRange,
                    title = "Duration",
                    content = "3 Days, 2 Nights"
                )

                TripInfoRow(
                    icon = Icons.Default.People,
                    title = "Group Size",
                    content = "Max 12 people"
                )

                Divider()

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Experience the breathtaking beauty of the Serengeti National Park. " +
                        "Witness the great migration, spot the Big Five, and immerse yourself " +
                        "in the stunning African wilderness. This safari adventure includes " +
                        "luxury accommodations, expert guides, and unforgettable wildlife encounters.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Divider()

                // Booking Options
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.titleMedium
                )

                // Date Selection
                DateSelector(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Seat Selection
                Text(
                    text = "Number of Seats",
                    style = MaterialTheme.typography.titleMedium
                )

                SeatSelector(
                    selectedSeats = selectedSeats,
                    onSeatsChanged = { selectedSeats = it }
                )

                // Price Summary
                PriceSummary(
                    pricePerSeat = pricePerSeat,
                    selectedSeats = selectedSeats
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Book Now Button
                Button(
                    onClick = onBookNowClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = selectedDate != null
                ) {
                    Text("Book Now")
                }
            }
        }
    }
}

@Composable
fun TripInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DateSelector(
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    // This is a simplified version. In a real app, you'd want to use a proper date picker
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("2024-03-15", "2024-03-22", "2024-03-29").forEach { date ->
            FilterChip(
                selected = date == selectedDate,
                onClick = { onDateSelected(date) },
                label = { Text(date) }
            )
        }
    }
}

@Composable
fun SeatSelector(
    selectedSeats: Int,
    onSeatsChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (selectedSeats > 1) onSeatsChanged(selectedSeats - 1) }
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease seats")
        }

        Text(
            text = selectedSeats.toString(),
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = { if (selectedSeats < 12) onSeatsChanged(selectedSeats + 1) }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase seats")
        }
    }
}

@Composable
fun PriceSummary(
    pricePerSeat: Double,
    selectedSeats: Int
) {
    val totalPrice = pricePerSeat * selectedSeats
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Price Summary",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Price per seat")
                Text(formatter.format(pricePerSeat))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Number of seats")
                Text(selectedSeats.toString())
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatter.format(totalPrice),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
} 