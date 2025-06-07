package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NotificationsList(
    notifications: List<TripNotification>,
    onNotificationClick: (TripNotification) -> Unit,
    onMarkAsRead: (TripNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(notifications) { notification ->
            NotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification) },
                onMarkAsRead = { onMarkAsRead(notification) }
            )
        }
        
        if (notifications.isEmpty()) {
            item {
                EmptyNotificationsState()
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: TripNotification,
    onClick: () -> Unit,
    onMarkAsRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF8F9FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Notification Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getNotificationColor(notification.type).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationColor(notification.type),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Notification Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF667EEA))
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatNotificationTime(notification.scheduledTime),
                        fontSize = 11.sp,
                        color = Color(0xFF999999)
                    )
                    
                    NotificationPriorityBadge(priority = notification.priority)
                }
            }
        }
    }
}

@Composable
fun NotificationPriorityBadge(priority: NotificationPriority) {
    val (color, text) = when (priority) {
        NotificationPriority.LOW -> Color(0xFF4CAF50) to "Low"
        NotificationPriority.NORMAL -> Color(0xFF2196F3) to "Normal"
        NotificationPriority.HIGH -> Color(0xFFFF9800) to "High"
        NotificationPriority.URGENT -> Color(0xFFF44336) to "Urgent"
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
fun EmptyNotificationsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "You're all caught up! We'll notify you about important trip milestones.",
            fontSize = 14.sp,
            color = Color(0xFF999999),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun NotificationBanner(
    notification: TripNotification,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getNotificationColor(notification.type).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getNotificationIcon(notification.type),
                contentDescription = null,
                tint = getNotificationColor(notification.type),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            TextButton(
                onClick = onAction,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = getNotificationColor(notification.type)
                )
            ) {
                Text(
                    text = getActionText(notification.type),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Helper functions
fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.TRIP_STARTING_SOON,
        NotificationType.TRIP_STARTING_TODAY -> Icons.Default.FlightTakeoff
        NotificationType.ADD_ACTIVITIES -> Icons.Default.Add
        NotificationType.BUDGET_REMINDER -> Icons.Default.AttachMoney
        NotificationType.PACKING_REMINDER -> Icons.Default.Luggage
        NotificationType.DOCUMENT_REMINDER -> Icons.Default.Description
        NotificationType.TRIP_ENDING_SOON -> Icons.Default.FlightLand
        NotificationType.TRIP_COMPLETED -> Icons.Default.CheckCircle
    }
}

fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.TRIP_STARTING_SOON,
        NotificationType.TRIP_STARTING_TODAY -> Color(0xFF4CAF50)
        NotificationType.ADD_ACTIVITIES -> Color(0xFF2196F3)
        NotificationType.BUDGET_REMINDER -> Color(0xFFFF9800)
        NotificationType.PACKING_REMINDER -> Color(0xFF9C27B0)
        NotificationType.DOCUMENT_REMINDER -> Color(0xFFF44336)
        NotificationType.TRIP_ENDING_SOON -> Color(0xFF795548)
        NotificationType.TRIP_COMPLETED -> Color(0xFF4CAF50)
    }
}

fun getActionText(type: NotificationType): String {
    return when (type) {
        NotificationType.TRIP_STARTING_SOON,
        NotificationType.TRIP_STARTING_TODAY -> "View Trip"
        NotificationType.ADD_ACTIVITIES -> "Add Activity"
        NotificationType.BUDGET_REMINDER -> "Track Budget"
        NotificationType.PACKING_REMINDER -> "View Checklist"
        NotificationType.DOCUMENT_REMINDER -> "Check Docs"
        NotificationType.TRIP_ENDING_SOON -> "View Trip"
        NotificationType.TRIP_COMPLETED -> "Rate Trip"
    }
}

fun formatNotificationTime(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
    
    return when {
        dateTime.toLocalDate() == now.toLocalDate() -> {
            "Today ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
        dateTime.toLocalDate() == now.toLocalDate().minusDays(1) -> {
            "Yesterday ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
        else -> dateTime.format(formatter)
    }
}
