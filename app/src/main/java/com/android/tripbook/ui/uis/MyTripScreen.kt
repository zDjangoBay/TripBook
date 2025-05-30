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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.TripStatus
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MyTripsScreen(onPlanNewTripClick: () -> Unit) {
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
                val trips = remember {
                    listOf(
                        Trip(
                            name = "Safari Adventure",
                            startDate = LocalDate.of(2024, 12, 15),
                            endDate = LocalDate.of(2024, 12, 22),
                            destination = "Kenya, Tanzania",
                            travelers = 4,
                            budget = 2400,
                            status = TripStatus.PLANNED
                        ),
                        Trip(
                            name = "Morocco Discovery",
                            startDate = LocalDate.of(2025, 1, 10),
                            endDate = LocalDate.of(2025, 1, 18),
                            destination = "Marrakech, Fez",
                            travelers = 2,
                            budget = 1800,
                            status = TripStatus.ACTIVE
                        ),
                        Trip(
                            name = "Cape Town Explorer",
                            startDate = LocalDate.of(2024, 9, 5),
                            endDate = LocalDate.of(2024, 9, 12),
                            destination = "South Africa",
                            travelers = 6,
                            budget = 3200,
                            status = TripStatus.COMPLETED
                        )
                    )
                }

                LazyColumn {
                    items(trips.filter {
                        when (selectedTab) {
                            "ALL" -> true
                            "PLANNED" -> it.status == TripStatus.PLANNED
                            "ACTIVE" -> it.status == TripStatus.ACTIVE
                            "COMPLETED" -> it.status == TripStatus.COMPLETED
                            else -> true
                        }
                    }) { trip ->
                        TripCard(trip)
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