package com.android.tripbook.comment.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.comment.model.Comment
import com.android.tripbook.comment.model.Reply
import com.android.tripbook.comment.util.TimeUtils

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: String,
    onReply: (Comment) -> Unit,
    onLike: (Comment) -> Unit,
    onDelete: (Comment) -> Unit
) {
    var showReplies by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    if (comment.isDeleted) {
        DeletedCommentItem()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = comment.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.username,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyMedium
                )

                comment.imageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Comment image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(top = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }

                Text(
                    text = TimeUtils.getRelativeTimeSpan(comment.timestamp),
                    style = MaterialTheme.typography.labelSmall
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onLike(comment) }) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Like"
                        )
                    }

                    Text(text = comment.likes.toString())

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(onClick = { onReply(comment) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reply"
                        )
                    }

                    if (comment.replies.isNotEmpty()) {
                        TextButton(onClick = { showReplies = !showReplies }) {
                            Text(
                                text = if (showReplies) "Hide replies" else "Show ${comment.replies.size} replies"
                            )
                        }
                    }
                }
            }

            if (comment.userId == currentUserId) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                onDelete(comment)
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showReplies,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(start = 48.dp)
            ) {
                comment.replies.forEach { reply ->
                    ReplyItem(
                        reply = reply,
                        currentUserId = currentUserId,
                        onLike = { onLike(comment.copy(replies = listOf(reply))) }
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun ReplyItem(
    reply: Reply,
    currentUserId: String,
    onLike: (Reply) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = reply.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reply.username,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = reply.text,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TimeUtils.getRelativeTimeSpan(reply.timestamp),
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { onLike(reply) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like"
                    )
                }

                Text(
                    text = reply.likes.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun DeletedCommentItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "This comment has been deleted",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentItem() {
    CommentItem(
        comment = Comment(
            id = "1",
            userId = "user1",
            username = "John Doe",
            avatarUrl = "",
            text = "This is a sample comment",
            timestamp = System.currentTimeMillis(),
            likes = 5,
            imageUri = null,
            replies = emptyList()
        ),
        currentUserId = "user1",
        onReply = {},
        onLike = {},
        onDelete = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewReplyItem() {
    ReplyItem(
        reply = Reply(
            id = "1",
            userId = "user2",
            username = "Jane Smith",
            avatarUrl = "",
            text = "This is a sample reply",
            timestamp = System.currentTimeMillis(),
            likes = 2
        ),
        currentUserId = "user1",
        onLike = {}
    )
}