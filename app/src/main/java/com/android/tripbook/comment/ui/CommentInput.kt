package com.android.tripbook.comment.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun CommentInput(
    onPost: (String, String?) -> Unit,
    availableUsers: List<String> = emptyList()
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showUserSuggestions by remember { mutableStateOf(false) }
    var currentMentionQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    if (text.text.isEmpty()) {
                        Text(
                            text = "Write a comment...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { launcher.launch("image/*") }) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Attach image"
                )
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

        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onPost(text.text, selectedImageUri?.toString())
                text = TextFieldValue("")
                selectedImageUri = null
            },
            enabled = text.text.isNotBlank() || selectedImageUri != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Post")
        }
    }
}