
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TravelStory(
    val id: String,
    val authorName: String,
    val location: String,
    val title: String,
    val content: String,
    val timeAgo: String,
    val likes: Int,
    val isLiked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen() {
    val stories = remember {
        listOf(
            TravelStory(
                id = "1",
                authorName = "Sarah Johnson",
                location = "Cape Town, South Africa",
                title = "Sunset at Table Mountain",
                content = "Just witnessed the most breathtaking sunset from Table Mountain! The cable car ride was worth every penny. Pro tip: book tickets online to avoid queues.",
                timeAgo = "2 hours ago",
                likes = 24
            ),
            TravelStory(
                id = "2",
                authorName = "Mike Chen",
                location = "Victoria Falls, Zambia",
                title = "Thunder of the Falls",
                content = "Standing at Victoria Falls, feeling the mist on my face - nature's power is incredible! Don't forget waterproof gear for the best viewing spots.",
                timeAgo = "1 day ago",
                likes = 18
            ),
            TravelStory(
                id = "3",
                authorName = "Amara Okafor",
                location = "Marrakech, Morocco",
                title = "Lost in the Medina",
                content = "Got completely lost in Marrakech's medina today, but what an adventure! Found the most amazing local restaurant. Sometimes the best experiences are unplanned.",
                timeAgo = "3 days ago",
                likes = 31
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Travel Stories",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            
            FloatingActionButton(
                onClick = { /* Open story creation */ },
                modifier = Modifier.size(56.dp),
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Story",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stories List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stories) { story ->
                StoryCard(story = story)
            }
        }
    }
}

@Composable
fun StoryCard(story: TravelStory) {
    var isLiked by remember { mutableStateOf(story.isLiked) }
    var likeCount by remember { mutableStateOf(story.likes) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Author Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = story.authorName.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = story.authorName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${story.location} • ${story.timeAgo}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Story Content
            Text(
                text = story.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = story.content,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
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
