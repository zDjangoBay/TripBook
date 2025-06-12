package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.models.Notification
import com.android.tripbook.data.models.NotificationType
import com.android.tripbook.data.services.NotificationDispatcher
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(notificationDispatcher: NotificationDispatcher) {
    val notifications = notificationDispatcher.notifications.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    var selectedType by remember { mutableStateOf<NotificationType?>(null) }

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
                        Button(onClick = { selectedType = type }) {
                            Text(type.name)
                        }
                    }
                    Button(onClick = { selectedType = null }) {
                        Text("All")
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    val filteredNotifications = selectedType?.let {
                        notifications.filter { it.type == it }
                    } ?: notifications

                    items(filteredNotifications.size) { index ->
                        val notification = filteredNotifications[index]
                        NotificationItem(notification) {
                            coroutineScope.launch {
                                notificationDispatcher.markAsRead(notification.id)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NotificationItem(notification: Notification, onMarkAsRead: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
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
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.message, style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Type: ${notification.type}", style = MaterialTheme.typography.caption)
            }

            if (!notification.isRead) {
                Button(onClick = onMarkAsRead) {
                    Text("Mark as Read")
                }
            }
        }
    }
}
