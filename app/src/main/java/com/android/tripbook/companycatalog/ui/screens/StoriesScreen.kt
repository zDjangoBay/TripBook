@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class TravelStory(
    val title: String,
    val author: String,
    val excerpt: String,
    val readTime: String,
    val author: String,
    val excerpt: String,
    val readTime: String,
    val timeAgo: String,
    val likes: Int
    val likes: Int
)

@Composable
fun StoriesScreen() {
    val stories = listOf(
        TravelStory(
            "Adventures in Kenya Safari",
            "Sarah Johnson",
            "My incredible journey through the Maasai Mara was filled with amazing wildlife encounters...",
            "5 min read",
            "2 hours ago",
            124
        ),
        TravelStory(
            "Hidden Beaches of Tanzania",
            "Mike Chen",
            "Discovered pristine coastlines that most tourists never see. The crystal clear waters...",
            "8 min read",
            "1 day ago",
            89
        ),
        TravelStory(
            "Street Food Adventures in Lagos",
            "Amara Okafor",
            "From jollof rice to suya, exploring Lagos through its incredible street food scene...",
            "6 min read",
            "3 days ago",
            203
        ),
        TravelStory(
            "Climbing Kilimanjaro Solo",
            "David Williams",
            "The mental and physical challenge of Africa's highest peak taught me so much...",
            "12 min read",
            "1 week ago",
            156
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Travel Stories",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stories) { story ->
                StoryCard(story = story)
            }
        }
    }
}

@Composable
fun StoryCard(story: TravelStory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = story.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "by ${story.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = story.excerpt,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = story.readTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " • ${story.timeAgo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Likes",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = story.likes.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
