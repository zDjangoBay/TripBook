package com.android.tripbook.ui.uis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.manager.NotificationManager
import com.android.tripbook.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestNotificationScreen(
    notificationManager: NotificationManager,
    trips: List<Trip>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Test Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF667EEA),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Test Notification System",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap any button below to trigger a test notification for the selected trip milestone.",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
            
            if (trips.isNotEmpty()) {
                val testTrip = trips.first()
                
                items(NotificationType.values()) { notificationType ->
                    TestNotificationCard(
                        notificationType = notificationType,
                        trip = testTrip,
                        onTestClick = {
                            notificationManager.scheduleTestNotification(testTrip, notificationType)
                        }
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFF856404),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Trips Available",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF856404)
                            )
                            Text(
                                text = "Create a trip first to test notifications",
                                fontSize = 14.sp,
                                color = Color(0xFF856404)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestNotificationCard(
    notificationType: NotificationType,
    trip: Trip,
    onTestClick: () -> Unit
) {
    val (icon, title, description, color) = when (notificationType) {
        NotificationType.TRIP_STARTING_SOON -> {
            Quadruple(
                Icons.Default.FlightTakeoff,
                "Trip Starting Soon",
                "Notification sent 3 days before trip starts",
                Color(0xFF4CAF50)
            )
        }
        NotificationType.TRIP_STARTING_TODAY -> {
            Quadruple(
                Icons.Default.Today,
                "Trip Starting Today",
                "Notification sent on the day trip starts",
                Color(0xFF2196F3)
            )
        }
        NotificationType.ADD_ACTIVITIES -> {
            Quadruple(
                Icons.Default.Add,
                "Add Activities",
                "Reminder to add activities to itinerary",
                Color(0xFF9C27B0)
            )
        }
        NotificationType.BUDGET_REMINDER -> {
            Quadruple(
                Icons.Default.AttachMoney,
                "Budget Reminder",
                "Reminder to track trip expenses",
                Color(0xFFFF9800)
            )
        }
        NotificationType.PACKING_REMINDER -> {
            Quadruple(
                Icons.Default.Luggage,
                "Packing Reminder",
                "Reminder to start packing for trip",
                Color(0xFF795548)
            )
        }
        NotificationType.DOCUMENT_REMINDER -> {
            Quadruple(
                Icons.Default.Description,
                "Document Reminder",
                "Reminder to check travel documents",
                Color(0xFFF44336)
            )
        }
        NotificationType.TRIP_ENDING_SOON -> {
            Quadruple(
                Icons.Default.FlightLand,
                "Trip Ending Soon",
                "Notification sent 1 day before trip ends",
                Color(0xFF607D8B)
            )
        }
        NotificationType.TRIP_COMPLETED -> {
            Quadruple(
                Icons.Default.CheckCircle,
                "Trip Completed",
                "Notification sent after trip completion",
                Color(0xFF4CAF50)
            )
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "Trip: ${trip.name}",
                    fontSize = 11.sp,
                    color = Color(0xFF999999)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Test Button
            Button(
                onClick = onTestClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Test",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Helper data class for quadruple values
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
