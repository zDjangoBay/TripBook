package com.android.tripbook.posts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.controller.PostListController
import com.android.tripbook.posts.model.PostModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.ui.Alignment
import com.android.tripbook.posts.ui.components.ReactionBar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(controller: PostListController) {
    val uiState by controller.uiState.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { controller.refresh() }
    ) {
        LazyColumn {
            items(uiState.posts) { post ->
                PostItem(post)
            }
        }
    }
}



@Composable
fun PostItem(post: PostModel) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = post.title, style = MaterialTheme.typography.titleLarge)
        Text(text = post.content, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        ReactionBar(onReact = { emoji ->
            println("Reacted with $emoji to post ${post.id}")
        })
    }
}

