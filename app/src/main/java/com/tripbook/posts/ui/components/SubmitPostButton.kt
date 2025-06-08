package com.tripbook.posts.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripbook.posts.viewmodel.PostViewModel
import com.tripbook.posts.ui.event.PostUiEvent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun SubmitPostButton(
    viewModel: PostViewModel,
    postContent: String,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Button(
        onClick = { viewModel.onEvent(PostUiEvent.SubmitPost, postContent) },
        enabled = !state.isLoading,
        modifier = modifier
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text("Share Post")
        }
    }
}
