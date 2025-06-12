package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.models.Notification
import com.android.tripbook.data.services.NotificationDispatcher
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(notificationDispatcher: NotificationDispatcher) {
    val notifications = notificationDispatcher.notifications.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notifications") })
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(notifications.size) { index ->
                val notification = notifications[index]
                NotificationItem(notification) {
                    coroutineScope.launch {
                        notificationDispatcher.markAsRead(notification.id)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onMarkAsRead: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = notification.message, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Type: ${notification.type}", style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(8.dp))
            if (!notification.isRead) {
                Button(onClick = onMarkAsRead) {
                    Text("Mark as Read")
                }
            }
        }
    }
}
