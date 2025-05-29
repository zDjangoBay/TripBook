package com.android.tripbook.screens.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.R
import com.android.tripbook.di.ServiceLocator
import com.android.tripbook.screens.discovery.TravelPost
import com.android.tripbook.screens.discovery.TravelPostCard
import com.android.tripbook.screens.discovery.User
import com.android.tripbook.util.Resource
import com.android.tripbook.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserContentScreen(
    navigateToCreatePost: () -> Unit,
    navigateToEditPost: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateBack: () -> Unit
) {
    // Get ViewModel using factory
    val factory = ServiceLocator.provideViewModelFactory()
    val viewModel: ContentViewModel = viewModel(factory = factory)
    
    // State for UI
    var isLoading by remember { mutableStateOf(true) }
    var userPosts by remember { mutableStateOf<List<TravelPost>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // In a real app, we'd convert from data model to UI model
    // For now, using sample data that would be loaded from the ViewModel
    LaunchedEffect(Unit) {
        // Initial load of user posts
        viewModel.getUserPosts()
    }
    
    // Observe user posts state
    LaunchedEffect(viewModel.userPostsState.value) {
        val state = viewModel.userPostsState.value
        when (state) {
            is Resource.Success -> {
                isLoading = false
                // In a real app, we'd convert from the data model to the UI model
                // For now, use our sample data
                userPosts = listOf(
                    TravelPost(
                        id = "1",
                        title = "Amazing Safari Experience",
                        description = "My journey through the Serengeti was incredible. Saw the big five in just two days!",
                        imageResId = R.drawable.app_logo,
                        likes = 246,
                        comments = 52,
                        user = User("1", "John Doe", R.drawable.app_logo),
                        destination = "Serengeti, Tanzania"
                    ),
                    TravelPost(
                        id = "2",
                        title = "Victoria Falls Wonder",
                        description = "Standing at the edge of Victoria Falls was breathtaking. Nature at its finest!",
                        imageResId = R.drawable.app_logo,
                        likes = 312,
                        comments = 68,
                        user = User("1", "John Doe", R.drawable.app_logo),
                        destination = "Victoria Falls, Zimbabwe"
                    ),
                    TravelPost(
                        id = "3",
                        title = "Cape Town Adventures",
                        description = "Exploring Table Mountain and the beautiful beaches of Cape Town. What a city!",
                        imageResId = R.drawable.app_logo,
                        likes = 189,
                        comments = 37,
                        user = User("1", "John Doe", R.drawable.app_logo),
                        destination = "Cape Town, South Africa"
                    )
                )
            }
            is Resource.Loading -> {
                isLoading = true
            }
            is Resource.Error -> {
                isLoading = false
                errorMessage = state.message
                // Keep any previously loaded posts
            }
            null -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Posts") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open filter options */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToCreatePost,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New Post")
            }
        }    ) { innerPadding ->
        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null && userPosts.isEmpty()) {
            // Error state with no existing data
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "Failed to load posts",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.getUserPosts() },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry")
                    }
                }
            }
        } else if (userPosts.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.NoteAdd,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Posts Yet",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Create your first travel post to share your adventures with the community",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = navigateToCreatePost,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Post")
                    }
                }
            }
        } else {
            // Content state - display posts
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Show error banner if there was an error but we have existing data
                if (errorMessage != null) {
                    item {
                        ErrorBanner(
                            message = errorMessage ?: "Failed to update posts",
                            onRetry = { viewModel.getUserPosts() },
                            onDismiss = { errorMessage = null }
                        )
                    }
                }
                
                items(userPosts) { post ->
                    TravelPostCard(
                        post = post,
                        onPostClick = { navigateToPostDetail(post.id) },
                        onUserClick = { /* This is the current user */ }
                    )
                    
                    // Edit and Delete buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { navigateToEditPost(post.id) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }
                              var showDeleteDialog by remember { mutableStateOf(false) }
                
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                    
                    // Delete confirmation dialog
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Delete Post") },
                            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
                            confirmButton = {
                                Button(
                                    onClick = { 
                                        viewModel.deletePost(post.id)
                                        showDeleteDialog = false
                                        // In a real app, we'd reload the posts list or remove from current list
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                    }
                    
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorBanner(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
            
            Row {
                TextButton(onClick = onRetry) {
                    Text(
                        "Retry",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
