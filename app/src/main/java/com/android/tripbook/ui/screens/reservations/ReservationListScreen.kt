package com.android.tripbook.ui.screens.reservations

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.data.managers.ReservationSessionManager
import java.time.format.DateTimeFormatter

/**
 * Screen showing user's reservations with tabs for different statuses
 */
@Composable
fun ReservationListScreen(
    onReservationClick: (String) -> Unit
) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pending", "Upcoming", "Completed")
    val statuses = listOf(
        ReservationStatus.PENDING,
        ReservationStatus.COMPLETED
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Text(
            text = "My Reservations",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Reservations list
        val filteredReservations = reservations.filter { it.status == statuses[selectedTab] }

        if (filteredReservations.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No ${tabs[selectedTab].lowercase()} reservations",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredReservations) { reservation ->
                    ReservationCard(
                        reservation = reservation,
                        onClick = { /* Handle click */ },
                        onCancel = if (reservation.status == ReservationStatus.PENDING) {
                            { /* Handle cancel */ }
                        } else null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationCard(
    reservation: TripReservation,
    onClick: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1488646953014-85cb44e25828",
                contentDescription = "Trip image",
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = reservation.tripTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    StatusBadge(status = reservation.status)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${reservation.fromLocation} → ${reservation.toLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Departure: ${reservation.departureDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", reservation.totalPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    onCancel?.let {
                        TextButton(
                            onClick = it,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cancel", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: ReservationStatus
) {
    val (backgroundColor, textColor, text) = when (status) {
        ReservationStatus.PENDING -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.tertiary,
            "Pending"
        )
        ReservationStatus.CONFIRMED -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.primary,
            "Confirmed"
        )
        ReservationStatus.COMPLETED -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.secondary,
            "Completed"
        )
        ReservationStatus.CANCELLED -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.error,
            "Cancelled"
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}


