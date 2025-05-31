package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.android.tripbook.comment.model.Comment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        AsyncImage(
            model = comment.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp)
        )
0
        Column {
            Text(
                text = comment.username,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(comment.timestamp)),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentItem() {
    val mockComment = Comment(
        id = "1",
        userId = "u1",
        username = "Jane Doe",
        avatarUrl = null, // Replace with a real URL if needed
        text = "This is a sample comment!",
        timestamp = System.currentTimeMillis()
    )
    CommentItem(comment = mockComment)
}
