package com.android.tripbook.ui.uis

import androidx.compose.runtime.Composable
import com.android.tripbook.model.Trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.TripCategory
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.viewmodel.TripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MyTripsScreen(
    tripViewModel: TripViewModel,
    onPlanNewTripClick: () -> Unit
) {
    TripBookTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6B5B95)) // Purple background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "My Trips",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Plan your African adventure",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Search Bar
                BasicTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (true) {
                                Text(
                                    text = "Search trips...",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Tabs
                var selectedTab by remember { mutableStateOf("ALL") }
                val tabs = listOf("ALL", "PLANNED", "ACTIVE", "COMPLETED")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEach { tab ->
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == tab) Color.White else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Trip List
                val trips by tripViewModel.trips.collectAsState()
                val filteredTrips = trips.filter {
                    when (selectedTab) {
                        "ALL" -> true
                        "PLANNED" -> it.status == TripStatus.PLANNED
                        "ACTIVE" -> it.status == TripStatus.ACTIVE
                        "COMPLETED" -> it.status == TripStatus.COMPLETED
                        else -> true
                    }
                }

                if (filteredTrips.isEmpty()) {
                    // Empty State
                    EmptyTripsState(
                        selectedTab = selectedTab,
                        onCreateTripClick = onPlanNewTripClick,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn {
                        items(filteredTrips) { trip ->
                            TripCard(trip)
                        }
                    }
                }
            }

            // Floating Action Button
            FloatingActionButton(
                onClick = onPlanNewTripClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Purple40,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${trip.startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${trip.endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFFFF69B4),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = trip.destination, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Travelers",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${trip.travelers} travelers", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Budget",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$${trip.budget}", fontSize = 14.sp)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (trip.status) {
                            TripStatus.PLANNED -> Color(0xFFADD8E6)
                            TripStatus.ACTIVE -> Color(0xFF90EE90)
                            TripStatus.COMPLETED -> Color(0xFFD3D3D3)
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = trip.status.name,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun EmptyTripsState(
    selectedTab: String,
    onCreateTripClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = when (selectedTab) {
                "PLANNED" -> Icons.Default.DateRange
                "ACTIVE" -> Icons.Default.Flight
                "COMPLETED" -> Icons.Default.Done
                else -> Icons.Default.TravelExplore
            },
            contentDescription = "No trips",
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = when (selectedTab) {
                "PLANNED" -> "No Planned Trips"
                "ACTIVE" -> "No Active Trips"
                "COMPLETED" -> "No Completed Trips"
                else -> "No Trips Yet"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        Text(
            text = when (selectedTab) {
                "PLANNED" -> "You haven't planned any trips yet. Start planning your next African adventure!"
                "ACTIVE" -> "No trips are currently active. Plan a new trip to get started!"
                "COMPLETED" -> "You haven't completed any trips yet. Create your first adventure!"
                else -> "Ready to explore Africa? Create your first trip and start your adventure!"
            },
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Create Trip Button
        Button(
            onClick = onCreateTripClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF6B5B95)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Trip",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create Your First Trip",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional encouragement text
        Text(
            text = "✈️ Discover amazing destinations\n🌍 Plan unforgettable experiences\n📸 Create lasting memories",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}