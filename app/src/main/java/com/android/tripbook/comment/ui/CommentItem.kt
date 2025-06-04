package com.android.tripbook.comment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.R
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
        Image(
            painter = painterResource(R.drawable.ic_user),
            contentDescription = "User avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .padding(end = 8.dp)
        )

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
                text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    .format(Date(comment.timestamp)),
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
        avatarUrl = null,
        text = "This is a sample comment!",
        timestamp = System.currentTimeMillis()
    )
    CommentItem(comment = mockComment)
}
