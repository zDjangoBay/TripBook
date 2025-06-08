package com.tripbook.posts.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripbook.posts.ui.components.SubmitPostButton
import com.tripbook.posts.viewmodel.PostViewModel

@Composable
fun CreatePostScreen() {
    val viewModel: PostViewModel = viewModel()
    var postContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = postContent,
            onValueChange = { postContent = it },
            label = { Text("Share your travel story...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        SubmitPostButton(
            viewModel = viewModel,
            postContent = postContent
        )
    }
}
