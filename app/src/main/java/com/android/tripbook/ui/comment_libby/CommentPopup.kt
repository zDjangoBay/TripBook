package com.android.tripbook.ui.components.comment_libby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.tripbook.ui.components.comment_libby.model.Comment

/**
 * A reusable comment popup component that slides up from the bottom
 * Created by Libby
 *
 * @param isVisible Whether the popup is visible
 * @param tripId The ID of the trip to show comments for
 * @param onDismiss Callback when the popup is dismissed
 * @param onCommentSubmit Callback when a new comment is submitted
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentPopup(
    isVisible: Boolean,
    tripId: String,
    onDismiss: () -> Unit,
    onCommentSubmit: (String) -> Unit = {}
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(16.dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                CommentContent(
                    tripId = tripId,
                    onDismiss = onDismiss,
                    onCommentSubmit = onCommentSubmit
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentContent(
    tripId: String,
    onDismiss: () -> Unit,
    onCommentSubmit: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    // Dummy data for demonstration
    val dummyComments = remember {
        listOf(
            Comment(
                id = "1",
                userId = "user1",
                userName = "John Traveler",
                userAvatar = "",
                text = "Amazing trip! The views were absolutely breathtaking. Can't wait to visit again next year!",
                timestamp = "2 hours ago",
                likes = 5
            ),
            Comment(
                id = "2",
                userId = "user2",
                userName = "Sarah Explorer",
                userAvatar = "",
                text = "Great experience overall. The guide was very knowledgeable and friendly. Highly recommend!",
                timestamp = "5 hours ago",
                likes = 3
            ),
            Comment(
                id = "3",
                userId = "user3",
                userName = "Mike Adventure",
                userAvatar = "",
                text = "Perfect weather during the trip. The accommodation was comfortable and the food was delicious.",
                timestamp = "1 day ago",
                likes = 7
            ),
            Comment(
                id = "4",
                userId = "user4",
                userName = "Emma Wanderer",
                userAvatar = "",
                text = "This was my first time visiting this destination and it exceeded all my expectations!",
                timestamp = "2 days ago",
                likes = 12
            ),
            Comment(
                id = "5",
                userId = "user5",
                userName = "David Nomad",
                userAvatar = "",
                text = "Well organized trip with great attention to detail. The team was professional throughout.",
                timestamp = "3 days ago",
                likes = 8
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Trip ID: $tripId",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comments List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dummyComments) { comment ->
                CommentItem(comment = comment)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comment Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Add a comment...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onCommentSubmit(commentText)
                        commentText = ""
                    }
                },
                enabled = commentText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Comment",
                    tint = if (commentText.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
