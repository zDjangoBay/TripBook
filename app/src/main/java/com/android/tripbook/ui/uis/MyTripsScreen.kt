package com.android.tripbook.ui.uis

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MyTripsScreen(
    trips: List<Trip>,
    onPlanNewTripClick: () -> Unit,
    onTripClick: (Trip) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "My Trips",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Plan your African adventure",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(25.dp))
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Search trips...",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Planned", "Active", "Completed").forEach { tab ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedTab == tab) Color.White else Color.Transparent,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedTab == tab) Color.White else Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == tab) Color(0xFF667EEA) else Color.White
                        )
                    }
                }
            }

            // Trip List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trips.filter { trip ->
                    (selectedTab == "All" ||
                            (selectedTab == "Planned" && trip.status == TripStatus.PLANNED) ||
                            (selectedTab == "Active" && trip.status == TripStatus.ACTIVE) ||
                            (selectedTab == "Completed" && trip.status == TripStatus.COMPLETED)) &&
                            (searchText.isEmpty() ||
                                    trip.name.contains(searchText, ignoreCase = true) ||
                                    trip.destination.contains(searchText, ignoreCase = true))
                }) { trip ->
                    TripCard(trip = trip, onClick = { onTripClick(trip) })
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onPlanNewTripClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color.White,
            contentColor = Color(0xFF667EEA),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Trip",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit
) {
    val statusColor = when (trip.status) {
        TripStatus.PLANNED -> Color(0xFF0066CC)
        TripStatus.ACTIVE -> Color(0xFF00CC66)
        TripStatus.COMPLETED -> Color(0xFF666666)
    }
    val statusBgColor = when (trip.status) {
        TripStatus.PLANNED -> Color(0xFFE6F3FF)
        TripStatus.ACTIVE -> Color(0xFFE6FFE6)
        TripStatus.COMPLETED -> Color(0xFFF0F0F0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                    Text(
                        text = "${trip.startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${
                            trip.endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        }",
                        fontSize = 14.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = trip.status.name.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📍",
                        fontSize = 14.sp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trip.destination,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Travelers
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "👥",
                        fontSize = 14.sp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${trip.travelers} travelers",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Budget
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💰",
                        fontSize = 14.sp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$${trip.budget}",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
