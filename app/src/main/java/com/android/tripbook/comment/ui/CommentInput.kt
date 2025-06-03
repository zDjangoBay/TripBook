package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommentInput(
    onPost: (String) -> Unit,
    isReply: Boolean = false,
    onCancel: (() -> Unit)? = null
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { 
                Text(if (isReply) "Write a reply..." else "Write a comment...") 
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onPost(text)
                        text = ""
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Text("Post")
            }
            
            if (isReply && onCancel != null) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        }
    }
}


