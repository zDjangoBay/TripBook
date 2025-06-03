package com.android.tripbook.posts.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel is now passed as a parameter
import com.android.tripbook.posts.model.PostModel // Ensure this is imported
import com.android.tripbook.posts.ui.components.PostCard
import com.android.tripbook.posts.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostViewModel, // Accepts the shared ViewModel
    onNavigateToCreatePost: () -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // LaunchedEffect to load posts when the screen is first composed or viewModel instance changes.
    // This ensures posts are loaded if this screen is visited for the first time or if the ViewModel instance were to change.
    // Given the shared ViewModel from AppNavigation, this mainly handles the initial load.
    LaunchedEffect(key1 = viewModel) {
        viewModel.handleEvent(PostEvent.LoadPosts)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TripBook Posts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant, // Example color
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.handleEvent(PostEvent.ResetForm) // Ensure form is reset before navigating
                    onNavigateToCreatePost()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.posts.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error Loading Posts",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = uiState.error!!, // Safe due to null check
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(onClick = { viewModel.handleEvent(PostEvent.RefreshPosts) }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.posts.isEmpty() && !uiState.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No posts yet", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap the '+' to create your first travel post!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp), // Space for FAB
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.posts, key = { post -> post.id }) { post ->
                            PostCard(
                                post = post,
                                onCardClick = { onNavigateToPostDetail(post.id) },
                                onLikeClick = { viewModel.handleEvent(PostEvent.ToggleLike(post.id)) },
                                onCommentClick = { onNavigateToPostDetail(post.id) }
                                // currentUserId is handled internally in PostCard or passed from ViewModel if exposed
                            )
                        }
                    }
                }
            }
        }
    }
}
