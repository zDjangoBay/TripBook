package com.android.tripbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.model.User

@Composable
fun MiniProfile(user: User, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (!user.avatarUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(user.avatarUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        } else {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {}
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = user.displayName ?: user.username, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun UserAvatar(user: User, modifier: Modifier = Modifier) {
    if (!user.avatarUrl.isNullOrEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(user.avatarUrl),
            contentDescription = null,
            modifier = modifier
                .size(32.dp)
                .clip(CircleShape)
        )
    } else {
        Surface(
            modifier = modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
    }
}

@Composable
fun UserListDialog(users: List<User>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Travelers who took this trip") },
        text = {
            Column {
                users.forEach { user ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        UserAvatar(user)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.displayName ?: user.username)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun MiniProfileTruncated(users: List<User>, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    val maxVisible = 3
    val visibleUsers = users.take(maxVisible)
    val extraCount = users.size - maxVisible
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { showDialog = true }
    ) {
        visibleUsers.forEach { user ->
            UserAvatar(user, modifier = Modifier.padding(end = 4.dp))
        }
        if (extraCount > 0) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = "+$extraCount", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
    if (showDialog) {
        UserListDialog(users = users, onDismiss = { showDialog = false })
    }
}
