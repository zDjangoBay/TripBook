package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.android.tripbook.comment.model.Comment
import com.android.tripbook.comment.model.Reply
import com.android.tripbook.comment.ui.CommentScreen
import com.android.tripbook.ui.theme.TripBookTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripBookTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    var comments by remember {
        mutableStateOf(
            listOf(
                Comment(
                    id = "1", 
                    userId = "u1", 
                    username = "Alice", 
                    avatarUrl = null, 
                    text = "Wow!", 
                    timestamp = System.currentTimeMillis() - 3600000,
                    likes = 5,
                    replies = listOf(
                        Reply(
                            id = "r1",
                            userId = "u2",
                            username = "Bob",
                            avatarUrl = null,
                            text = "I agree!",
                            timestamp = System.currentTimeMillis() - 1800000,
                            likes = 2
                        )
                    )
                ),
                Comment(
                    id = "2", 
                    userId = "u2", 
                    username = "Bob", 
                    avatarUrl = null, 
                    text = "Love the views!", 
                    timestamp = System.currentTimeMillis() - 7200000,
                    likes = 3
                )
            )
        )
    }

    CommentScreen(
        comments = comments,
        onPost = { newComment ->
            comments = comments + newComment
        },
        onReply = { commentId, userId, reply ->
            comments = comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(replies = comment.replies + reply)
                } else {
                    comment
                }
            }
        },
        onLike = { commentId ->
            comments = comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(likes = comment.likes + 1)
                } else {
                    comment
                }
            }
        },
        onDelete = { commentId ->
            comments = comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(isDeleted = true)
                } else {
                    comment
                }
            }
        },
        onBack = {
            // Do nothing for now (optional: Toast or navigate back)
        }
    )
}
