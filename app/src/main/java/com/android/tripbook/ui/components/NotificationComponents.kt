package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.*
import java.time.format.DateTimeFormatter

/**
 * Individual notification card component
 */
@Composable
fun NotificationCard(
    notification: NotificationItem,
    onNotificationClick: (NotificationItem) -> Unit,
    onMarkAsRead: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNotificationClick(notification) },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Notification type icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = getNotificationTypeColor(notification.type),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notification.type.icon,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Notification content
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
                        fontSize = 16.sp,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Unread indicator
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Trip name (if applicable)
                    notification.tripName?.let { tripName ->
                        Text(
                            text = "📍 $tripName",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Timestamp
                    Text(
                        text = formatNotificationTime(notification.timestamp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Action buttons
            Column {
                if (!notification.isRead) {
                    IconButton(
                        onClick = { onMarkAsRead(notification.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = "Mark as read",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = { onDelete(notification.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Notification filter chips
 */
@Composable
fun NotificationFilterChips(
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit,
    stats: NotificationStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotificationFilter.values().forEach { filter ->
            val isSelected = filter == selectedFilter
            val count = when (filter) {
                NotificationFilter.ALL -> stats.totalNotifications
                NotificationFilter.UNREAD -> stats.unreadCount
                NotificationFilter.TRIP_RELATED -> stats.totalNotifications // Simplified
                NotificationFilter.TODAY -> stats.todayCount
                NotificationFilter.THIS_WEEK -> stats.thisWeekCount
            }
            
            FilterChip(
                onClick = { onFilterSelected(filter) },
                label = { 
                    Text(
                        text = "${filter.displayName} ($count)",
                        fontSize = 12.sp
                    ) 
                },
                selected = isSelected,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

/**
 * Scheduled notification card
 */
@Composable
fun ScheduledNotificationCard(
    scheduledNotification: ScheduledNotificationInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Schedule icon
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Scheduled",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scheduledNotification.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = scheduledNotification.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "Scheduled: ${formatScheduledTime(scheduledNotification.scheduledTime)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (scheduledNotification.isActive) 
                            Color.Green 
                        else 
                            Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Notification stats summary
 */
@Composable
fun NotificationStatsSummary(
    stats: NotificationStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Total",
                value = stats.totalNotifications.toString(),
                icon = Icons.Default.Notifications
            )
            
            StatItem(
                label = "Unread",
                value = stats.unreadCount.toString(),
                icon = Icons.Default.MarkEmailUnread,
                highlight = stats.unreadCount > 0
            )
            
            StatItem(
                label = "Scheduled",
                value = stats.upcomingScheduled.toString(),
                icon = Icons.Default.Schedule
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Helper functions
 */
private fun getNotificationTypeColor(type: NotificationItemType): Color {
    return when (type) {
        NotificationItemType.TRIP_STARTING -> Color(0xFF4CAF50)
        NotificationItemType.TRIP_ENDING -> Color(0xFFFF9800)
        NotificationItemType.ITINERARY_REMINDER -> Color(0xFF2196F3)
        NotificationItemType.TRIP_UPDATE -> Color(0xFF9C27B0)
        NotificationItemType.SYSTEM -> Color(0xFF607D8B)
        NotificationItemType.TEST -> Color(0xFFE91E63)
    }
}

private fun formatNotificationTime(timestamp: java.time.LocalDateTime): String {
    val now = java.time.LocalDateTime.now()
    val duration = java.time.Duration.between(timestamp, now)
    
    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
        duration.toHours() < 24 -> "${duration.toHours()}h ago"
        duration.toDays() < 7 -> "${duration.toDays()}d ago"
        else -> timestamp.format(DateTimeFormatter.ofPattern("MMM dd"))
    }
}

private fun formatScheduledTime(scheduledTime: java.time.LocalDateTime): String {
    val now = java.time.LocalDateTime.now()
    val duration = java.time.Duration.between(now, scheduledTime)

    return when {
        duration.toMinutes() < 60 -> "in ${duration.toMinutes()}m"
        duration.toHours() < 24 -> "in ${duration.toHours()}h"
        duration.toDays() < 7 -> "in ${duration.toDays()}d"
        else -> scheduledTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
    }
}

/**
 * Notification badge for showing unread count
 */
@Composable
fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        Box(
            modifier = modifier
                .size(20.dp)
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
