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
                Text(
                    text = "${story.readTime} • ${story.timeAgo}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${story.likes} likes",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}