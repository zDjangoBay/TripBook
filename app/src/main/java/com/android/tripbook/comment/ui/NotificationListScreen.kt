package com.android.tripbook.comment.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.android.tripbook.comment.model.Notification

@Composable
fun NotificationListScreen(notifications: List<Notification>, onNotificationClick: (Notification) -> Unit) {
    LazyColumn {
        items(notifications) { notification ->
            NotificationItem(notification = notification) {
                onNotificationClick(notification)
            }
        }
    }
}

