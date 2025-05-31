package com.tripbook.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tripbook.reservation.viewmodel.Seat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    availableSeats: List<Seat>,
    selectedSeats: List<Seat>,
    onSeatSelected: (Seat) -> Unit,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Seats") },
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
                .padding(16.dp)
        ) {
            // Seat Map Legend
            SeatLegend()

            Spacer(modifier = Modifier.height(16.dp))

            // Seat Map
            SeatMap(
                availableSeats = availableSeats,
                selectedSeats = selectedSeats,
                onSeatSelected = onSeatSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Seats Summary
            SelectedSeatsSummary(selectedSeats = selectedSeats)

            Spacer(modifier = Modifier.weight(1f))

            // Confirm Button
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = selectedSeats.isNotEmpty()
            ) {
                Text("Confirm Selection")
            }
        }
    }
}

@Composable
fun SeatLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(
            color = MaterialTheme.colorScheme.primary,
            label = "Available"
        )
        LegendItem(
            color = MaterialTheme.colorScheme.error,
            label = "Occupied"
        )
        LegendItem(
            color = MaterialTheme.colorScheme.secondary,
            label = "Selected"
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SeatMap(
    availableSeats: List<Seat>,
    selectedSeats: List<Seat>,
    onSeatSelected: (Seat) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(availableSeats) { seat ->
            SeatItem(
                seat = seat,
                isSelected = selectedSeats.contains(seat),
                onSeatSelected = onSeatSelected
            )
        }
    }
}

@Composable
fun SeatItem(
    seat: Seat,
    isSelected: Boolean,
    onSeatSelected: (Seat) -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.secondary
        seat.isAvailable -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = seat.isAvailable) { onSeatSelected(seat) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seat.seatNumber,
            color = if (isSelected || seat.isAvailable) 
                MaterialTheme.colorScheme.onPrimary 
            else 
                MaterialTheme.colorScheme.onError
        )
    }
}

@Composable
fun SelectedSeatsSummary(selectedSeats: List<Seat>) {
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
                text = "Selected Seats",
                style = MaterialTheme.typography.titleMedium
            )

            if (selectedSeats.isEmpty()) {
                Text(
                    text = "No seats selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = selectedSeats.joinToString(", ") { it.seatNumber },
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Total: $${selectedSeats.sumOf { it.price }}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
} 