package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.imePadding
import com.android.tripbook.comment.model.Comment
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    comments: List<Comment>,
    onPost: (Comment) -> Unit,
    onBack: () -> Unit, // ← add this to handle back navigation
    currentUserId: String = "u1",
    currentUsername: String = "You",
    currentAvatar: String? = null
) {
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
                    containerColor = Color(0xFF1976D2) // Blue background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
        ) {
            CommentList(
                comments = comments,
                modifier = Modifier.weight(1f)
            )

            Divider()

            CommentInput { text ->
                val comment = Comment(
                    id = UUID.randomUUID().toString(),
                    userId = currentUserId,
                    username = currentUsername,
                    avatarUrl = currentAvatar,
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                onPost(comment)
            }
        }
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
        onBack = {} // no-op in preview
    )
}
