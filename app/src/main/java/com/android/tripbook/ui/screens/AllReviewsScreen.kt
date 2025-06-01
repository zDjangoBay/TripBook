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
import com.android.tripbook.viewmodel.MockReviewViewModel
import java.text.SimpleDateFormat
import java.util.*

// Use the same SharedComment data class from TripDetailScreen
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val imageUri: String? = null,
    val timestamp: String = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date()),
    val authorName: String = "You"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReviewsScreen(
    reviewId: Int,
    onBack: () -> Unit
) {
    val viewModel = remember { MockReviewViewModel() }
    val reviews by viewModel.reviews.collectAsState()
    val review = reviews.find { it.id == reviewId } ?: return

    // Add the image to the review's images list if it's not already there
    val reviewWithImage = remember(review) {
        val imageUrl = "https://miro.medium.com/max/1024/1*PGyTnM-6mv3jVCImDI38mg.jpeg"
        if (!review.images.contains(imageUrl)) {
            review.copy(images = listOf(imageUrl) + review.images)
        } else {
            review
        }
    }

    // Get comments from shared storage based on tripId (assuming reviewId maps to tripId)
    val sharedComments = remember(reviewId) {
        CommentStorage.getCommentsForTrip(reviewId)
    }

    // Convert shared comments to local comment format and combine with local comments
    val comments = remember { mutableStateListOf<Comment>() }

    // Update comments when shared comments change
    LaunchedEffect(sharedComments.size) {
        // Clear existing comments and add shared ones
        comments.clear()
        sharedComments.forEach { sharedComment ->
            comments.add(
                Comment(
                    id = sharedComment.id,
                    text = sharedComment.text,
                    imageUri = sharedComment.imageUri,
                    timestamp = sharedComment.timestamp,
                    authorName = sharedComment.authorName
                )
            )
        }
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
            item {
                // Hero Image Carousel
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
                            items(reviewWithImage.images) { imageUrl ->
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

            item {
                // Rating and Review Content
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
                                    imageVector = if (index < reviewWithImage.rating) Icons.Filled.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (index < reviewWithImage.rating) Color(0xFFFFD700) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${reviewWithImage.rating}.0",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Review text
                        Text(
                            text = reviewWithImage.comment,
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
                                    .clickable {
                                        isLiked = !isLiked
                                        likeCount += if (isLiked) 1 else -1
                                    }
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
                                onClick = { isFlagged = !isFlagged },
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

            item {
                // Comments Section Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comments (${comments.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (comments.isNotEmpty()) {
                        Text(
                            text = "See all",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { /* Handle see all */ }
                        )
                    }
                }
            }

            item {
                // Comments horizontal scroll
                if (comments.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(comments) { comment ->
                            CommentCard(comment = comment)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                } else {
                    // Empty state
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
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            item {
                // Add Comment Section
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
                            onValueChange = { newComment = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Share your thoughts...") },
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
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
                                    onClick = { selectedImageUri = null },
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
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    val newCommentObj = Comment(
                                        text = newComment,
                                        imageUri = selectedImageUri?.toString()
                                    )

                                    // Add to local comments list
                                    comments.add(0, newCommentObj)

                                    // Also add to shared storage
                                    CommentStorage.addComment(
                                        SharedComment(
                                            id = newCommentObj.id,
                                            text = newCommentObj.text,
                                            imageUri = newCommentObj.imageUri,
                                            timestamp = newCommentObj.timestamp,
                                            authorName = newCommentObj.authorName,
                                            tripId = reviewId
                                        )
                                    )

                                    newComment = ""
                                    selectedImageUri = null
                                }
                            },
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

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CommentCard(comment: Comment) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comment.authorName.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = comment.authorName,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = comment.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comment text
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            // Comment image if available
            comment.imageUri?.let { imageUri ->
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}