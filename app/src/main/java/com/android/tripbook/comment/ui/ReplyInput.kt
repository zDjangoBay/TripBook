package com.android.tripbook.comment.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ReplyInput(
    commentId: String,
    onPost: (String, String) -> Unit,
    onCancel: () -> Unit,
    availableUsers: List<String> = emptyList()
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var showUserSuggestions by remember { mutableStateOf(false) }
    var currentMentionQuery by remember { mutableStateOf("") }

    val filteredUsers = availableUsers.filter { user ->
        user.contains(currentMentionQuery, ignoreCase = true) &&
                !text.text.contains("@$user")
    }

    val onTextChange = { newValue: TextFieldValue ->
        val lastAtPos = newValue.text.lastIndexOf('@')
        val lastSpacePos = newValue.text.lastIndexOf(' ')

        if (lastAtPos > lastSpacePos) {
            currentMentionQuery = newValue.text.substring(lastAtPos + 1)
            showUserSuggestions = currentMentionQuery.isNotEmpty()
        } else {
            showUserSuggestions = false
        }

        text = newValue
    }

    fun insertMention(username: String) {
        val currentText = text.text
        val lastAtPos = currentText.lastIndexOf('@')

        if (lastAtPos >= 0) {
            val newText = currentText.substring(0, lastAtPos) + "@$username " +
                    currentText.substring(lastAtPos + currentMentionQuery.length + 1)

            text = TextFieldValue(
                text = newText,
                selection = TextRange(lastAtPos + username.length + 2)
            )
        }

        showUserSuggestions = false
        currentMentionQuery = ""
    }

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
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (text.text.isEmpty()) {
                        Text(
                            text = "Write a reply...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Button(
                    onClick = {
                        if (text.text.isNotBlank()) {
                            onPost(commentId, text.text)
                            text = TextFieldValue("")
                        }
                    },
                    enabled = text.text.isNotBlank()
                ) {
                    Text("Post")
                }

                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        }

        if (showUserSuggestions && filteredUsers.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                LazyColumn {
                    items(filteredUsers) { user ->
                        Text(
                            text = user,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { insertMention(user) }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}