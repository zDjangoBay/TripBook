package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home

import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.ui.theme.TripBookTheme

class Feedscreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                FeedScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    val posts = remember {
        listOf(
            Post(
                username = "Randy",
                profileImage = "https://randomuser.me/api/portraits/men/1.jpg",
                postImage = "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
                caption = "Enjoying the sunny day at the beach! #sunshine",
                timeAgo = "2h"
            ),
            Post(
                username = "Steven",
                profileImage = "https://randomuser.me/api/portraits/women/2.jpg",
                postImage = "https://images.unsplash.com/photo-1500534623283-312aade485b7",
                caption = "Delicious homemade pizza 🍕",
                timeAgo = "5h"
            )
        )
    }


    val notificationCount = 3
    val messageCount = 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feed") },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {

                    BadgedBox(
                        badge = {
                            if (messageCount > 0) {
                                Badge { Text(messageCount.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { /* TODO: Open messages */ }) {
                            Icon(
                                imageVector = Icons.Filled.ChatBubble,
                                contentDescription = "Messages"
                            )
                        }
                    }


                    BadgedBox(
                        badge = {
                            if (notificationCount > 0) {
                                Badge { Text(notificationCount.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { /* TODO: Open notifications */ }) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(posts) { post ->
                PostItem(post)
                Divider()
            }
        }
    }
}

data class Post(
    val username: String,
    val profileImage: String,
    val postImage: String,
    val caption: String,
    val timeAgo: String
)

@Composable
fun PostItem(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(post.profileImage),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(text = post.username, style = MaterialTheme.typography.titleMedium)
                Text(text = post.timeAgo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = rememberAsyncImagePainter(post.postImage),
            contentDescription = "Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = post.caption, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            IconButton(onClick = { /* TODO: Like action */ }) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Like"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { /* TODO: Comment actio */ }) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comment"
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AddCircle, contentDescription = "Post") },
            label = { Text("Post") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    TripBookTheme {
        FeedScreen()
    }
}
