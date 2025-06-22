package com.android.tripbook.posts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.ui.components.PostCard
import com.android.tripbook.posts.viewmodel.PostEvent
import com.android.tripbook.posts.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: PostViewModel,
    postId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(postId) {
        viewModel.handleEvent(PostEvent.SelectPost(postId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.selectedPost != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        PostCard(
                            post = uiState.selectedPost,
                            onCardClick = { },
                            onLikeClick = { 
                                viewModel.handleEvent(PostEvent.ToggleLike(uiState.selectedPost.id)) 
                            },
                            onCommentClick = { },
                            showFullContent = true
                        )
                    }
                    
                    item {
                        Text(
                            text = "Comments (${uiState.selectedPost.comments.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    items(uiState.selectedPost.comments) { comment ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = comment.username,
                                    style = MaterialTheme.typography.titleSmall
                                )