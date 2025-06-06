package com.android.tripbook.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.Model.Comment
import com.android.tripbook.ui.components.CommentCard
import com.android.tripbook.ViewModel.MockReviewViewModel
import com.android.tripbook.ViewModel.MockCommentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailReviewScreen(
    reviewId: Int,
    tripId: Int,
    onLikeClicked: (reviewId: Int) -> Unit,
    onFlagClicked: (reviewId: Int) -> Unit,
    onBack: () -> Unit
) {
    val reviewViewModel = remember { MockReviewViewModel() }
    val commentViewModel = remember { MockCommentViewModel() }

    val reviews by reviewViewModel.reviews.collectAsState()
    val comments by commentViewModel.comments.collectAsState()

    val review = reviews.find { it.id == reviewId } ?: return

    // Add the default image to the review's images list if it's not already there
    val reviewWithImage = remember(review) {
        val imageUrl = "https://miro.medium.com/max/1024/1*PGyTnM-6mv3jVCImDI38mg.jpeg"
        if (!review.images.contains(imageUrl)) {
            review.copy(images = listOf(imageUrl) + review.images)
        } else {
            review
        }
    }

    // Load comments when screen loads
    LaunchedEffect(reviewId) {
        commentViewModel.loadCommentsForReview(reviewId)
    }

    var newComment by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLiked by remember { mutableStateOf(false) }
    var isFlagged by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf((10..50).random()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Review Details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Hero Image Carousel
            item {
                HeroImageCarousel(images = reviewWithImage.images)
            }

            // Review Content Card
            item {
                ReviewContentCard(
                    review = reviewWithImage,
                    isLiked = isLiked,
                    isFlagged = isFlagged,
                    likeCount = likeCount,
                    onLikeClick = {
                        isLiked = !isLiked
                        likeCount += if (isLiked) 1 else -1
                    },
                    onFlagClick = { isFlagged = !isFlagged }
                )
            }

            // Comments Section Header
            item {
                CommentsHeader(commentsCount = comments.size)
            }

            // Comments List or Empty State
            item {
                if (comments.isNotEmpty()) {
                    CommentsCarousel(
                        comments = comments,
                        commentViewModel = commentViewModel,
                        reviewId = reviewId
                    )
                } else {
                    EmptyCommentsState()
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Add Comment Section
            item {
                AddCommentSection(
                    newComment = newComment,
                    selectedImageUri = selectedImageUri,
                    onCommentChange = { newComment = it },
                    onImagePick = { imagePickerLauncher.launch("image/*") },
                    onImageRemove = { selectedImageUri = null },
                    onSubmit = {
                        if (newComment.isNotBlank()) {
                            val comment = Comment(
                                text = newComment,
                                imageUri = selectedImageUri?.toString(),
                                authorName = "You"
                            )
                            commentViewModel.addComment(reviewId, comment)
                            newComment = ""
                            selectedImageUri = null
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HeroImageCarousel(images: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                items(images) { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .width(350.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Gradient overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun ReviewContentCard(
    review: com.android.tripbook.Model.Review,
    isLiked: Boolean,
    isFlagged: Boolean,
    likeCount: Int,
    onLikeClick: () -> Unit,
    onFlagClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Rating section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (index < review.rating) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${review.rating}.0",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Review text
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onLikeClick() }
                        .background(
                            if (isLiked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = likeCount.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isLiked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Flag button
                IconButton(
                    onClick = { onFlagClick() },
                    modifier = Modifier
                        .background(
                            if (isFlagged) Color(0xFFFF9800).copy(alpha = 0.1f)
                            else Color.Transparent,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFlagged) Icons.Filled.Flag else Icons.Default.OutlinedFlag,
                        contentDescription = "Flag",
                        tint = if (isFlagged) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentsHeader(commentsCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Comments ($commentsCount)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (commentsCount > 0) {
            Text(
                text = "See all",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Handle see all */ }
            )
        }
    }
}

@Composable
private fun CommentsCarousel(
    comments: List<Comment>, 
    commentViewModel: MockCommentViewModel, 
    reviewId: Int
) {
    var replyingToComment by remember { mutableStateOf<Comment?>(null) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Comments list
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(comments) { comment ->
                CommentCard(
                    comment = comment,
                    onReactionSelected = { emoji ->
                        commentViewModel.addReaction(reviewId, comment.id, emoji)
                    },
                    onReplySelected = { selectedComment ->
                        replyingToComment = selectedComment
                    }
                )
            }
        }
        
        // Reply input if replying to a comment
        replyingToComment?.let { comment ->
            Spacer(modifier = Modifier.height(8.dp))
            com.android.tripbook.ui.components.ReplyInput(
                parentAuthor = comment.authorName,
                onSendReply = { replyText: String ->
                    commentViewModel.addReply(reviewId, comment.id, replyText)
                    replyingToComment = null
                },
                onCancel = {
                    replyingToComment = null
                }
            )
        }
    }
}

@Composable
private fun EmptyCommentsState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No comments yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Be the first to share your thoughts!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddCommentSection(
    newComment: String,
    selectedImageUri: Uri?,
    onCommentChange: (String) -> Unit,
    onImagePick: () -> Unit,
    onImageRemove: () -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Add a Comment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comment input field
            OutlinedTextField(
                value = newComment,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Share your thoughts...") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = onImagePick) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Add Image",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            // Selected image preview
            selectedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(12.dp))
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = onImageRemove,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove Image",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = onSubmit,
                modifier = Modifier.align(Alignment.End),
                enabled = newComment.isNotBlank(),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Post Comment")
            }
        }
    }
}
