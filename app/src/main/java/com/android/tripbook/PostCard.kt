package com.android.tripbook

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostCard(
    post: PostModel, 
    onPostClick: (PostModel) -> Unit, 
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onPostClick(post) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = dateFormat.format(Date(post.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            if (post.location.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Images
            if (post.images.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(post.images.first().uri)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Post Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Multiple images indicator
                    if (post.images.size > 1) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "+${post.images.size - 1}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Description
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Tags
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    post.tags.take(3).forEach { tag ->
                        AssistChip(
                            onClick = { /* Handle tag click */ },
                            label = { 
                                Text(
                                    text = tag.name, 
                                    style = MaterialTheme.typography.bodySmall
                                ) 
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    if (post.tags.size > 3) {
                        Text(
                            text = "+${post.tags.size - 3} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            // Hashtags
            if (post.hashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.hashtags.take(3).joinToString(" "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Sample Data for Preview
val samplePost = PostModel(
    title = "Exploring Buea Mountain",
    description = "We had an amazing time hiking and exploring the beautiful Buea mountain trails! The views were absolutely breathtaking and the weather was perfect for our adventure.",
    location = "Mount Buea, Cameroon",
    createdAt = System.currentTimeMillis(),
    images = listOf(
        ImageModel(uri = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80"),
        ImageModel(uri = "https://images.unsplash.com/photo-1551632811-561732d1e306?w=800&q=80"),
        ImageModel(uri = "https://images.unsplash.com/photo-1464822759844-d150vef38eb9?w=800&q=80")
    ),
    tags = listOf(
        TagModel("Adventure"), 
        TagModel("Hiking"), 
        TagModel("Cameroon"), 
        TagModel("Travel"),
        TagModel("Nature")
    ),
    hashtags = listOf("#mountain", "#buea", "#tripbook", "#adventure")
)

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun PostCardPreview() {
    TripBookTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PostCard(post = samplePost, onPostClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun PostCardDarkPreview() {
    TripBookTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PostCard(post = samplePost, onPostClick = {})
        }
    }
}