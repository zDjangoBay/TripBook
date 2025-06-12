@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class TravelStory(
    val title: String,
    val author: String,
    val excerpt: String,
    val readTime: String,
    val timeAgo: String,
    val likes: Int
)

@Composable
fun StoriesScreen() {
    val stories = listOf(
        TravelStory(
            title = "Mountain Adventure in the Alps",
            author = "John Smith",
            excerpt = "An incredible journey through the Swiss Alps with breathtaking views...",
            readTime = "5 min read",
            timeAgo = "2 hours ago",
            likes = 42
        ),
        TravelStory(
            title = "Beach Paradise in Maldives",
            author = "Sarah Johnson",
            excerpt = "Crystal clear waters and pristine beaches made this trip unforgettable...",
            readTime = "3 min read",
            timeAgo = "1 day ago",
            likes = 38
        ),
        TravelStory(
            title = "City Exploration in Tokyo",
            author = "Mike Chen",
            excerpt = "From ancient temples to modern skyscrapers, Tokyo offers endless discoveries...",
            readTime = "7 min read",
            timeAgo = "3 days ago",
            likes = 55
        ),
        TravelStory(
            title = "Safari Adventure in Kenya",
            author = "Emma Wilson",
            excerpt = "Wildlife encounters and stunning landscapes in the heart of Africa...",
            readTime = "6 min read",
            timeAgo = "1 week ago",
            likes = 67
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(stories) { story ->
            StoryCard(story = story)
        }
    }
}

@Composable
fun StoryCard(story: TravelStory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = story.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "By ${story.author}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = story.excerpt,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isLiked = !isLiked
                            likeCount += if (isLiked) 1 else -1
                        }
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else Color.Gray
                        )
                    }
                    Text(
                        text = likeCount.toString(),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                IconButton(onClick = { /* Share story */ }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class TravelStory(
    val title: String,
    val author: String,
    val location: String,
    val excerpt: String,
    val readTime: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen() {
    val stories = listOf(
        TravelStory(
            "Adventure in the Cameroon Highlands",
            "Sarah Johnson",
            "Bamenda, Cameroon",
            "An incredible journey through the mountains and villages of Northwest Cameroon, discovering local culture and breathtaking landscapes.",
            "5 min read"
        ),
        TravelStory(
            "Wildlife Safari Experience",
            "Mike Chen",
            "Waza National Park",
            "My unforgettable encounter with elephants, lions, and giraffes in one of Cameroon's most famous national parks.",
            "7 min read"
        ),
        TravelStory(
            "Coastal Paradise Discovery",
            "Emma Wilson",
            "Kribi Beach",
            "Relaxing days on pristine beaches, fresh seafood, and stunning waterfalls that flow directly into the ocean.",
            "4 min read"
        ),
        TravelStory(
            "Cultural Immersion in Douala",
            "James Okoye",
            "Douala, Cameroon",
            "Exploring the economic capital's vibrant markets, art scene, and meeting the friendly locals who made my trip special.",
            "6 min read"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Stories") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Read inspiring travel stories from fellow adventurers",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(stories) { story ->
                StoryCard(story = story)
            }
        }
    }
}

@Composable
fun StoryCard(story: TravelStory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = story.location,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = story.readTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = story.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "by ${story.author}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = story.excerpt,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(
                onClick = { /* TODO: Navigate to full story */ }
            ) {
                Text("Read More")
            }
        }
    }
}
