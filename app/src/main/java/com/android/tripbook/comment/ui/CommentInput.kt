package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CommentInput(onPost: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {

        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Write a comment...") },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                onPost(text)
                text = ""
            },
            enabled = text.isNotBlank()
        ) {
            Text("Post")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentInput() {
    CommentInput(onPost = { /* do nothing */ })
}
