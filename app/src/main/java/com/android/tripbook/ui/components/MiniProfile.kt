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
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.data.User
import com.android.tripbook.ui.screens.UserProfileDemoScreen

@Composable
fun MiniProfile(user: User, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (!user.profileImageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(user.profileImageUrl),
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
        Text(text = user.name, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun UserAvatar(
    user: User,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val avatarModifier = if (onClick != null) {
        modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    } else {
        modifier
            .size(32.dp)
            .clip(CircleShape)
    }

    if (!user.profileImageUrl.isNullOrEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(user.profileImageUrl),
            contentDescription = null,
            modifier = avatarModifier
        )
    } else {
        Surface(
            modifier = avatarModifier,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
    }
}

@Composable
fun UserProfilePopup(
    user: User,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${user.name}'s Profile",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show the mini profile demo content for the specific user
                UserProfileDemoScreen(
                    modifier = Modifier.fillMaxSize(),
                    specificUser = user
                )
            }
        }
    }
}

@Composable
fun UserListDialog(users: List<User>, onDismiss: () -> Unit) {
    var selectedUser by remember { mutableStateOf<User?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Travelers who took this trip") },
        text = {
            Column {
                users.forEach { user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { selectedUser = user }
                    ) {
                        UserAvatar(user)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )

    // Show profile popup when user is selected
    selectedUser?.let { user ->
        UserProfilePopup(
            user = user,
            onDismiss = { selectedUser = null }
        )
    }
}

@Composable
fun MiniProfileTruncated(users: List<User>, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    val maxVisible = 3
    val visibleUsers = users.take(maxVisible)
    val extraCount = users.size - maxVisible

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { showDialog = true }
    ) {
        visibleUsers.forEach { user ->
            UserAvatar(
                user = user,
                modifier = Modifier.padding(end = 4.dp),
                onClick = { selectedUser = user }
            )
        }
        if (extraCount > 0) {
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { showDialog = true },
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

    // Show profile popup when user avatar is clicked directly
    selectedUser?.let { user ->
        UserProfilePopup(
            user = user,
            onDismiss = { selectedUser = null }
        )
    }
}
