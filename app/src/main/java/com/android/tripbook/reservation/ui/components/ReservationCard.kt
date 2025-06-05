package com.android.tripbook.reservation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.reservation.model.Reservation
import com.android.tripbook.reservation.model.ReservationStatus
import com.android.tripbook.reservation.model.ReservationType
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Card component for displaying a reservation in a list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationCard(
    reservation: Reservation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title row with icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon based on reservation type
                Icon(
                    imageVector = getIconForReservationType(reservation.type),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Title
                Text(
                    text = reservation.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Status chip
                StatusChip(status = reservation.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date and location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = formatDateRange(reservation.startDate, reservation.endDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Location
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = reservation.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total: ${reservation.price}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Chip displaying the reservation status
 */
@Composable
fun StatusChip(status: ReservationStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        ReservationStatus.CONFIRMED -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.primary,
            "Confirmed"
        )
        ReservationStatus.PENDING -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.tertiary,
            "Pending"
        )
        ReservationStatus.CANCELLED -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.error,
            "Cancelled"
        )
        ReservationStatus.COMPLETED -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary,
            "Completed"
        )
        ReservationStatus.REFUNDED -> Triple(
            Color(0xFFF3E5F5).copy(alpha = 0.8f),
            Color(0xFF4A148C),
            "Refunded"
        )
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
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

@Preview
@Composable
fun ReservationCardPreview() {
    TripBookTheme {
        ReservationCard(
            reservation = Reservation(
                id = "1",
                userId = "user123",
                title = "Serengeti Safari Lodge",
                type = ReservationType.ACCOMMODATION,
                status = ReservationStatus.CONFIRMED,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(3),
                location = "Serengeti, Tanzania",
                price = "$450",
                description = "Luxury safari lodge with views of the Serengeti plains",
                imageUrl = ""
            ),
            onClick = {}
        )
    }
}
