package com.android.tripbook.posts.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel is now passed
import coil.compose.AsyncImage
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.ui.components.CommentItem
import com.android.tripbook.posts.ui.components.PostTagChip
import com.android.tripbook.posts.viewmodel.PostEvent
import com.android.tripbook.posts.viewmodel.PostViewModel

// Assuming R class is correctly imported from your app module
// import com.android.tripbook.R
// Placeholder R class if not resolved by IDE:


import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object R {
    object drawable {
        const val ic_avatar_placeholder = 0 // Replace with actual if you have one
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: PostViewModel, // Accepts the shared ViewModel
    postId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(postId, viewModel) { // Re-select post if postId or ViewModel instance changes
        viewModel.handleEvent(PostEvent.SelectPost(postId))
    }

    val uiState by viewModel.uiState.collectAsState()
    val post = uiState.selectedPost // This will be updated reactively by the ViewModel
    val focusManager = LocalFocusManager.current

    var commentText by remember { mutableStateOf("") }
    var replyingToCommentId by remember { mutableStateOf<String?>(null) }
    var currentReplyText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = post?.title ?: "Post Detail",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (post?.userId == "current_user_simulated") { // Match ViewModel's currentUserId
                        IconButton(onClick = {
                            viewModel.handleEvent(PostEvent.DeletePost(postId))
                            onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Post"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            if (post != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            label = { Text("Add a comment...") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Send,
                                capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                            ),
                            keyboardActions = KeyboardActions(onSend = {
                                if (commentText.isNotBlank()) {
                                    viewModel.handleEvent(PostEvent.AddComment(postId, commentText))
                                    commentText = ""
                                    focusManager.clearFocus()
                                }
                            }),
                            maxLines = 3
                        )
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    viewModel.handleEvent(PostEvent.AddComment(postId, commentText))
                                    commentText = ""
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = commentText.isNotBlank()
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "Send comment")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && post == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            uiState.error != null && post == null -> { // Error specific to loading this post
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Error: ${uiState.error}", // Show specific error
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            post == null -> { // Post not found or deselected after deletion
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { Text("Post not found.", style = MaterialTheme.typography.headlineSmall) }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues), // Apply padding from Scaffold
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp) // Adjust bottom for content
                ) {
                    item { PostDetailHeader(post = post, viewModel = viewModel) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { PostDetailContent(post = post) }
                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item {
                        Text(
                            "Comments (${post.comments.size})",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider()
                    }

                    if (post.comments.isEmpty()) {
                        item {
                            Text(
                                "No comments yet. Be the first to share your thoughts!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(post.comments, key = { it.id }) { comment ->
                            CommentItem(
                                comment = comment,
                                currentUserId = "current_user_simulated", // Match ViewModel
                                onReplyClick = {
                                    replyingToCommentId = if (replyingToCommentId == comment.id) null else comment.id
                                    currentReplyText = ""
                                },
                                onSendReply = { replyContent ->
                                    viewModel.handleEvent(PostEvent.AddReply(postId, comment.id, replyContent))
                                    replyingToCommentId = null
                                    currentReplyText = ""
                                },
                                isReplying = replyingToCommentId == comment.id,
                                replyText = if (replyingToCommentId == comment.id) currentReplyText else "",
                                onReplyTextChange = { if (replyingToCommentId == comment.id) currentReplyText = it }
                            )
                            if (post.comments.lastOrNull()?.id != comment.id) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostDetailHeader(post: PostModel, viewModel: PostViewModel) {
    val currentUserId = "current_user_simulated" // Match ViewModel
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mma", Locale.ENGLISH) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.userAvatar ?: R.drawable.ic_avatar_placeholder,
                contentDescription = "${post.username}'s Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                error = if (R.drawable.ic_avatar_placeholder != 0) painterResource(id = R.drawable.ic_avatar_placeholder) else null,
                placeholder = if (R.drawable.ic_avatar_placeholder != 0) painterResource(id = R.drawable.ic_avatar_placeholder) else null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        post.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (post.isVerified) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified User",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp).padding(start = 4.dp)
                        )
                    }
                }
                Text(
                    text = post.timestamp.atZone(ZoneId.systemDefault()).format(dateTimeFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { viewModel.handleEvent(PostEvent.ToggleLike(post.id)) }) {
                Icon(
                    imageVector = if (post.likes.contains(currentUserId)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.likes.contains(currentUserId)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun PostDetailContent(post: PostModel) {
    // This composable remains largely the same as in your `code new new.txt`
    // Ensure PostTagChip is correctly imported or defined if it was local
    Column {
        Text(
            post.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (post.images.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(post.images, key = { it.id }) { image ->
                    AsyncImage(
                        model = image.uri,
                        contentDescription = "Post Image",
                        modifier = Modifier
                            .height(220.dp)
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            post.description,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "${post.location.name}, ${post.location.city}, ${post.location.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (post.categories.isNotEmpty() || post.tags.isNotEmpty()) {
            Text(
                "Categories & Tags",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row( // Consider FlowRow for better wrapping if many items
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                post.categories.forEach { category ->
                    PostTagChip(text = category.displayName, isCategory = true)
                }
                post.tags.forEach { tag ->
                    PostTagChip(text = tag.name, isCategory = false)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (post.hashtags.isNotEmpty()) {
            Text(
                "Hashtags",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row( // Consider FlowRow
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                post.hashtags.forEach { hashtag ->
                    Text(
                        text = "#$hashtag",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Divider()
    }
}
