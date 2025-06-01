package com.sasbergson.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sasbergson.tripbook.ui.components.PostDescriptionInput

@Composable
fun CreatePostScreen() {
    // --- Local UI State ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf<String?>(null) }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create a New Post",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- Title Input ---
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // --- Description Input (your component) ---
        PostDescriptionInput(
            description = description,
            onDescriptionChange = { description = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Submit Button ---
        Button(
            onClick = {
                if (title.isBlank() || description.isBlank()) {
                    showMessage = "Please fill in all fields."
                } else {
                    showMessage = "Post submitted! 🎉"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Post")
        }

        // --- Message Output ---
        showMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
