package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.android.tripbook.comment.model.Comment
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
                Comment("1", "u1", "Alice", null, "Wow!", System.currentTimeMillis()),
                Comment("2", "u2", "Bob", null, "Love the views!", System.currentTimeMillis())
            )
        )
    }

    CommentScreen(
        comments = comments,
        onPost = { newComment ->
            comments = comments + newComment
        },
        onBack = {
            // Do nothing for now (optional: Toast or navigate back)
        }
    )
}
