package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PlayArrow
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
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Helper function to determine the status of an itinerary item
enum class ItemStatus {
    COMPLETED, CURRENT, UPCOMING
}

fun getItemStatus(item: ItineraryItem, today: LocalDate): ItemStatus {
    return when {
        item.date.isBefore(today) -> ItemStatus.COMPLETED
        item.date.isEqual(today) -> ItemStatus.CURRENT
        else -> ItemStatus.UPCOMING
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripProgressTimelineScreen(
    trip: Trip,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Progress: ${trip.name}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (trip.itinerary.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No itinerary items to display a timeline for yet.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B)
                )
            }
        } else {
            val today = LocalDate.now()
            val sortedItinerary = trip.itinerary.sortedWith(
                compareBy<ItineraryItem> { it.date }
                    .thenBy { it.time }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) } // Top padding

                itemsIndexed(sortedItinerary) { index, item ->
                    val itemStatus = getItemStatus(item, today)
                    val lineColor = when (itemStatus) {
                        ItemStatus.COMPLETED -> Color(0xFF4CAF50) // Green
                        ItemStatus.CURRENT -> Color(0xFFFFC107) // Amber
                        ItemStatus.UPCOMING -> Color(0xFF9E9E9E) // Gray
                    }
                    val dotColor = when (itemStatus) {
                        ItemStatus.COMPLETED -> Color(0xFF4CAF50)
                        ItemStatus.CURRENT -> Color(0xFFFFC107)
                        ItemStatus.UPCOMING -> Color(0xFF9E9E9E)
                    }
                    val icon = when (itemStatus) {
                        ItemStatus.COMPLETED -> Icons.Default.CheckCircle
                        ItemStatus.CURRENT -> Icons.Default.PlayArrow
                        ItemStatus.UPCOMING -> Icons.Default.Event
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp) // Ensure enough height for content
                    ) {
                        // Timeline Line and Dot
                        Column(
                            modifier = Modifier
                                .width(30.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top line segment (if not the first item)
                            if (index > 0) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(4.dp) // Small gap to the dot
                                        .background(lineColor)
                                )
                            }
                            // Dot representing the event
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(dotColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = itemStatus.name,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // Bottom line segment (if not the last item)
                            if (index < sortedItinerary.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .fillMaxHeight()
                                        .background(lineColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Event Details Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(vertical = 4.dp), // Card vertical padding
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = item.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "${item.time} - ${item.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                if (item.notes.isNotEmpty()) {
                                    Text(
                                        text = "Notes: ${item.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) } // Bottom padding
            }
        }
    }
}