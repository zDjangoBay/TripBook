package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostListDemo()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListDemo() {
    val samplePosts = remember { createSamplePosts() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TripBook Posts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(samplePosts) { post ->
                PostCard(
                    post = post,
                    onCardClick = {
                        // Handle card click - navigate to post detail
                        println("Clicked on post: ${post.title}")
                    },
                    onLikeClick = {
                        // Handle like click - toggle like status
                        println("Liked post: ${post.title}")
                    },
                    onCommentClick = {
                        // Handle comment click - navigate to comments
                        println("Comment on post: ${post.title}")
                    },
                    currentUserId = "user123" // Sample current user ID
                )
            }
        }
    }
}

// PostCard Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    post: PostModel,
    onCardClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentUserId: String = "current_user"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User info and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.userAvatar,
                    contentDescription = "${post.username}'s Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.username,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (post.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verified User",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = post.timestamp.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Post description
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Location info
            if (post.location.name.isNotBlank() || post.location.city.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.location.name}, ${post.location.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Images
            if (post.images.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    items(post.images.take(3)) { image ->
                        AsyncImage(
                            model = image.uri,
                            contentDescription = "Post Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                    if (post.images.size > 3) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${post.images.size - 3}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Divider
            HorizontalDivider(
                thickness = 0.5.dp, 
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            
            // Action buttons (Like and Comment)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onLikeClick)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (post.likes.contains(currentUserId)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.likes.contains(currentUserId)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${post.likes.size} likes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Comment button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onCommentClick)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${post.comments.size} comments",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Sample data creation
fun createSamplePosts(): List<PostModel> {
    return listOf(
        PostModel(
            id = "1",
            userId = "user123",
            username = "John Explorer",
            userAvatar = "https://picsum.photos/200/200?random=1",
            title = "Amazing Sunset at Santorini",
            description = "Just witnessed the most breathtaking sunset at Oia, Santorini. The colors were absolutely magical and the view was unforgettable. Definitely a must-visit destination!",
            location = LocationModel(
                name = "Oia",
                city = "Santorini",
                country = "Greece",
                latitude = 36.4618,
                longitude = 25.3753
            ),
            images = listOf(
                ImageModel(uri = "https://picsum.photos/400/300?random=10", path = ""),
                ImageModel(uri = "https://picsum.photos/400/300?random=11", path = ""),
                ImageModel(uri = "https://picsum.photos/400/300?random=12", path = "")
            ),
            timestamp = LocalDateTime.now().minusHours(2),
            likes = mutableSetOf("user456", "user789", "user123"),
            comments = mutableListOf("comment1", "comment2"),
            isVerified = true
        ),
        PostModel(
            id = "2",
            userId = "user456",
            username = "Sarah Wanderlust",
            userAvatar = "https://picsum.photos/200/200?random=2",
            title = "Street Food Adventure in Bangkok",
            description = "Exploring the incredible street food scene in Bangkok! From pad thai to mango sticky rice, every bite is a flavor explosion. The night markets here are absolutely incredible.",
            location = LocationModel(
                name = "Chatuchak Weekend Market",
                city = "Bangkok",
                country = "Thailand",
                latitude = 13.7563,
                longitude = 100.5018
            ),
            images = listOf(
                ImageModel(uri = "https://picsum.photos/400/300?random=20", path = ""),
                ImageModel(uri = "https://picsum.photos/400/300?random=21", path = ""),
                ImageModel(uri = "https://picsum.photos/400/300?random=22", path = ""),
                ImageModel(uri = "https://picsum.photos/400/300?random=23", path = ""),
                ImageModel(uri = "https://picsum.photos/400/300?random=24", path = "")
            ),
            timestamp = LocalDateTime.now().minusDays(1),
            likes = mutableSetOf("user123", "user789"),
            comments = mutableListOf("comment1", "comment2", "comment3", "comment4"),
            isVerified = false
        ),
        PostModel(
            id = "3",
            userId = "user789",
            username = "Mike Adventure",
            userAvatar = "https://picsum.photos/200/200?random=3",
            title = "Hiking the Inca Trail to Machu Picchu",
            description = "Completed the 4-day Inca Trail hike and finally reached Machu Picchu! The journey was challenging but absolutely worth it. The ancient ruins are even more spectacular in person.",
            location = LocationModel(
                name = "Machu Picchu",
                city = "Cusco Region",
                country = "Peru",
                latitude = -13.1631,
                longitude = -72.5450
            ),
            images = listOf(
                ImageModel(uri = "https://picsum.photos/400/300?random=30", path = "")
            ),
            timestamp = LocalDateTime.now().minusDays(3),
            likes = mutableSetOf("user123", "user456", "user999", "user888"),
            comments = mutableListOf("comment1"),
            isVerified = true
        ),
        PostModel(
            id = "4",
            userId = "user999",
            username = "Emma Photographer",
            userAvatar = "https://picsum.photos/200/200?random=4",
            title = "Cherry Blossoms in Kyoto",
            description = "Spring in Kyoto is absolutely magical! The cherry blossoms are in full bloom and the temples look like something out of a fairy tale.",
            location = LocationModel(
                name = "Fushimi Inari Shrine",
                city = "Kyoto",
                country = "Japan",
                latitude = 34.9671,
                longitude = 135.7727
            ),
            images = listOf(
                ImageModel(uri = "https://picsum.photos/400/300?random=40", path = ""),
                ImageModel(uri = "https://picsum.photos/400/300?random=41", path = "")
            ),
            timestamp = LocalDateTime.now().minusHours(8),
            likes = mutableSetOf("user456"),
            comments = mutableListOf("comment1", "comment2", "comment3"),
            isVerified = false
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PostListPreview() {
    TripBookTheme {
        PostListDemo()
    }
}

