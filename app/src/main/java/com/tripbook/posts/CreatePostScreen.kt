package com.tripbook.posts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// Fixed ViewModel usage
class PostViewModel : ViewModel() {
    var postTitle by mutableStateOf("")

    fun submitPost(content: String) {
        println("Post submitted! Title: $postTitle\nContent: $content")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: PostViewModel = viewModel()  // This now works
) {
    var postText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = viewModel.postTitle,
            onValueChange = { viewModel.postTitle = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = postText,
            onValueChange = { postText = it },
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(16.dp))

        SubmitPostButton(
            onClick = { viewModel.submitPost(postText) },
            enabled = postText.isNotBlank() && viewModel.postTitle.isNotBlank()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    MaterialTheme {
        CreatePostScreen()
    }
}