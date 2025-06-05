package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun ReplyInput(
    commentId: String,
    onPost: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Reply to comment",
            style = MaterialTheme.typography.labelMedium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Write a reply...") },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onPost(commentId, text)
                            text = ""
                        }
                    },
                    enabled = text.isNotBlank()
                ) {
                    Text("Post")
                }
                
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        }
    }
}