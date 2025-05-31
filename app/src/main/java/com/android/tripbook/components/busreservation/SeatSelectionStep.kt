package com.android.tripbook.components.busreservation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.components.model.BusReservation
import com.android.tripbook.components.model.Seat
import java.time.format.DateTimeFormatter

@Composable
fun SeatSelectionStep(
    reservation: BusReservation,
    seats: List<Seat>,
    onSeatSelect: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select Your Seat",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Choose your preferred seat for the journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Trip Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${formatCityName(reservation.from)} to ${formatCityName(reservation.to)}",
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${reservation.date?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} • ${reservation.time?.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Driver Section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Driver",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Seat Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(seats) { seat ->
                    SeatItem(
                        seat = seat,
                        isSelected = seat.number == reservation.selectedSeat,
                        onSeatClick = { onSeatSelect(seat.number) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = "Selected"
                )
                LegendItem(
                    color = Color.Transparent,
                    label = "Available",
                    borderColor = MaterialTheme.colorScheme.outline
                )
                LegendItem(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    label = "Booked"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                enabled = reservation.selectedSeat.isNotEmpty()
            ) {
                Text("Continue to Payment")
            }
            
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun SeatItem(
    seat: Seat,
    isSelected: Boolean,
    onSeatClick: () -> Unit
) {
    val backgroundColor = when {
        seat.isBooked -> MaterialTheme.colorScheme.surfaceVariant
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    
    val textColor = when {
        seat.isBooked -> MaterialTheme.colorScheme.onSurfaceVariant
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (!seat.isBooked && !isSelected) MaterialTheme.colorScheme.outline else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !seat.isBooked) { onSeatClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seat.number,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    borderColor: Color? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .then(
                    if (borderColor != null) {
                        Modifier.border(1.dp, borderColor, RoundedCornerShape(4.dp))
                    } else Modifier
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatCityName(cityKey: String): String {
    return when (cityKey) {
        "new-york" -> "New York"
        "boston" -> "Boston"
        "washington" -> "Washington DC"
        "philadelphia" -> "Philadelphia"
        else -> cityKey
    }
}