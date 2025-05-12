package com.android.tripbook.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.R

@Composable
fun CreatePostScreen(modifier: Modifier = Modifier) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hashtags by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Select trip tag") }
    val tagOptions = listOf("Beach", "Mountain", "City", "Adventure", "Cultural")
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Placeholder, real image picker not included

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Create New Post", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        // Image upload (placeholder)
        Button(
            onClick = { /* TODO: Open gallery or camera */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Upload Image")
        }

        // Image preview (if available)
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Uploaded Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // Trip tag selector
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { /* TODO */ }
        ) {
            OutlinedTextField(
                value = selectedTag,
                onValueChange = { selectedTag = it },
                label = { Text("Trip Tag") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            // You can build a full dropdown here if needed
        }

        // Hashtags input
        OutlinedTextField(
            value = hashtags,
            onValueChange = { hashtags = it },
            label = { Text("Hashtags (e.g. #fun #trip)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { /* TODO: Submit Post */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Post")
        }
    }
}
