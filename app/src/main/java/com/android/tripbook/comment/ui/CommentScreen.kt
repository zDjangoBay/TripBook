package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.comment.model.Comment
import com.android.tripbook.comment.model.Reply
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    comments: List<Comment>,
    onPost: (Comment) -> Unit,
    onReply: (String, String, Reply) -> Unit,
    onLike: (String) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    currentUserId: String = "u1",
    currentUsername: String = "You",
    currentAvatar: String? = null
) {
    var replyingToComment by remember { mutableStateOf<Comment?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Comment?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comments", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                )
            )
        },
        contentWindowInsets = WindowInsets.ime
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            CommentList(
                comments = comments,
                currentUserId = currentUserId,
                onReply = { comment ->
                    replyingToComment = comment
                },
                onLike = { comment ->
                    onLike(comment.id)
                },
                onDelete = { comment ->
                    showDeleteDialog = comment
                },
                modifier = Modifier.weight(1f)
            )

            Divider()
            
            if (replyingToComment != null) {
                ReplyInput(
                    commentId = replyingToComment!!.id,
                    onPost = { commentId, text ->
                        val reply = Reply(
                            id = UUID.randomUUID().toString(),
                            userId = currentUserId,
                            username = currentUsername,
                            avatarUrl = currentAvatar,
                            text = text,
                            timestamp = System.currentTimeMillis()
                        )
                        onReply(commentId, replyingToComment!!.userId, reply)
                        replyingToComment = null
                    },
                    onCancel = {
                        replyingToComment = null
                    }
                )
            } else {
                CommentInput(onPost = { text ->
                    val comment = Comment(
                        id = UUID.randomUUID().toString(),
                        userId = currentUserId,
                        username = currentUsername,
                        avatarUrl = currentAvatar,
                        text = text,
                        timestamp = System.currentTimeMillis()
                    )
                    onPost(comment)
                })
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Comment") },
            text = { Text("Are you sure you want to delete this comment? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(showDeleteDialog!!.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCommentScreen() {
    val mockComments = listOf(
        Comment("1", "u1", "Alice", null, "Nice view!", System.currentTimeMillis()),
        Comment("2", "u2", "Bob", null, "Love this place!", System.currentTimeMillis())
    )

    CommentScreen(
        comments = mockComments,
        onPost = {},
        onBack = {},
        onReply = { _, _, _ -> },
        onLike = {},
        onDelete = {}
    )
}
