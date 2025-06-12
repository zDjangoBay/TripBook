package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.models.Notification
import com.android.tripbook.data.models.NotificationType
import com.android.tripbook.data.services.NotificationDispatcher
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotificationScreen(notificationDispatcher: NotificationDispatcher) {
    val notifications = notificationDispatcher.notifications.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    var selectedType by remember { mutableStateOf<NotificationType?>(null) }
    var detailedNotification by remember { mutableStateOf<Notification?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notifications") })
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NotificationType.values().forEach { type ->
                        Button(onClick = { selectedType = type }) { Text(type.name) }
                    }
                    Button(onClick = { selectedType = null }) { Text("All") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NotificationType.values().forEach { type ->
                        Button(onClick = {
                            coroutineScope.launch {
                                notificationDispatcher.sendNotification(type, "New ${type.name} notification!")
                            }
                        }) { Text("Send ${type.name}") }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    val filteredNotifications = selectedType?.let {
                        notifications.filter { it.type == selectedType }
                    } ?: notifications

                    items(filteredNotifications.size) { index ->
                        val notification = filteredNotifications[index]
                        NotificationItem(
                            notification,
                            onMarkAsRead = {
                                coroutineScope.launch {
                                    notificationDispatcher.markAsRead(notification.id)
                                }
                            },
                            onViewDetails = {
                                detailedNotification = notification
                            }
                        )
                    }
                }

                detailedNotification?.let {
                    DetailedNotificationView(notification = it, onDismiss = { detailedNotification = null })
                }
            }
        }
    )
}

@Composable
fun NotificationItem(notification: Notification, onMarkAsRead: () -> Unit, onViewDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (notification.type) {
                    NotificationType.PAYMENT_SUCCESS -> Icons.Default.CheckCircle
                    NotificationType.BOOKING_CONFIRMED -> Icons.Default.Event
                    NotificationType.GENERAL -> Icons.Default.Info
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.message, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Type: ${notification.type}", style = MaterialTheme.typography.bodySmall)
            }

            Row {
                Button(onClick = onMarkAsRead) {
                    Text("Mark as Read")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onViewDetails) {
                    Text("View Details")
                }
            }
        }
    }
}

@Composable
fun DetailedNotificationView(notification: Notification, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Notification Details")
        },
        text = {
            Column {
                Text(text = "Message: ${notification.message}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Type: ${notification.type}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Timestamp: ${notification.timestamp}", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
