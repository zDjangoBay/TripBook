package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.HorizontalDivider // <-- Renommé
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.model.Comment // <-- Import du modèle canonique Comment
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: String, // Ce paramètre n'est actuellement pas utilisé dans ce composable
    onReplyClick: () -> Unit,
    onSendReply: (String) -> Unit,
    isReplying: Boolean,
    replyText: String,
    onReplyTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, hh:mma", Locale.ENGLISH) }

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        comment.username,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        comment.timestamp.atZone(ZoneId.systemDefault()).format(dateTimeFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 2.dp)
                )
                TextButton(
                    onClick = onReplyClick,
                    contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp, start = 0.dp, end = 8.dp)
                ) {
                    Text("Reply", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (comment.replies.isNotEmpty()) {
            Column(modifier = Modifier.padding(start = 48.dp, top = 8.dp)) {
                comment.replies.forEach { reply ->
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = reply.username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    reply.username,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    reply.timestamp.atZone(ZoneId.systemDefault()).format(dateTimeFormatter),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                reply.text,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        if (isReplying) {
            OutlinedTextField(
                value = replyText,
                onValueChange = onReplyTextChange,
                label = { Text("Replying to ${comment.username}...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, top = 12.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onSendReply(replyText)
                                focusManager.clearFocus()
                            }
                        },
                        enabled = replyText.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send Reply") // <-- Icône AutoMirrored
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(onSend = {
                    if (replyText.isNotBlank()) {
                        onSendReply(replyText)
                        focusManager.clearFocus()
                    }
                }),
                singleLine = false,
                maxLines = 3
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentItem() {
    TripBookTheme {
        val sampleComment = Comment( // Utilise le modèle Comment canonique
            id = "c1",
            postId = "p1",
            userId = "u1",
            username = "TravelerDude",
            userAvatar = null,
            text = "What an amazing shot! I'd love to visit this place someday. Any tips?",
            timestamp = Instant.now().minusSeconds(3600),
            replies = listOf( // Utilise le modèle Comment canonique pour les réponses
                Comment(
                    id = "r1",
                    postId = "p1",
                    userId = "u2",
                    username = "AdventureGal",
                    userAvatar = null,
                    text = "It's incredible! You should definitely go. Best time is spring.",
                    timestamp = Instant.now().minusSeconds(1800),
                    replies = emptyList()
                )
            )
        )
        var replyText by remember { mutableStateOf("") }
        var isReplying by remember { mutableStateOf(false) }

        Column {
            CommentItem(
                comment = sampleComment,
                currentUserId = "myUserId",
                onReplyClick = { isReplying = !isReplying },
                onSendReply = { reply -> println("Sending reply: $reply") },
                isReplying = isReplying,
                replyText = replyText,
                onReplyTextChange = { replyText = it }
            )
            HorizontalDivider() // <-- Renommé
            CommentItem(
                comment = sampleComment.copy( // Utilise le modèle Comment canonique
                    id = "c2",
                    username = "NatureLover",
                    text = "I love the composition here. Beautiful work!"
                ),
                currentUserId = "myUserId",
                onReplyClick = { },
                onSendReply = { },
                isReplying = false,
                replyText = "",
                onReplyTextChange = { }
            )
        }
    }
}
