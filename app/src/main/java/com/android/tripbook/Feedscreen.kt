package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.ui.theme.TripBookTheme
import com.example.tripbook.PostActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
    // Make posts mutable state list so UI recomposes on changes
    val posts = remember {
        mutableStateListOf(
            Post(
                username = "Randy",
                profileImage = "https://randomuser.me/api/portraits/men/1.jpg",
                postImage = "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
                caption = "Enjoying the sunny day at the beach! #sunshine",
                timeAgo = "2h",
                isLiked = false,
                likeCount = 10
            ),
            Post(
                username = "Steven",
                profileImage = "https://randomuser.me/api/portraits/women/2.jpg",
                postImage = "https://images.unsplash.com/photo-1500534623283-312aade485b7",
                caption = "Delicious homemade pizza 🍕",
                timeAgo = "5h",
                isLiked = false,
                likeCount = 25
            )
        )
    }

    // Scaffold and LazyColumn unchanged except PostItem call:
    Scaffold(
        topBar = {
            // ... your existing TopAppBar code
        },
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(posts.size) { index ->
                PostItem(
                    post = posts[index],
                    onLikeClicked = {
                        // Toggle like state & update likeCount
                        val currentPost = posts[index]
                        val newLikedState = !currentPost.isLiked
                        val newLikeCount = if (newLikedState) currentPost.likeCount + 1 else currentPost.likeCount - 1
                        posts[index] = currentPost.copy(isLiked = newLikedState, likeCount = newLikeCount)
                    }
                )
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
    val timeAgo: String,
    val isLiked: Boolean = false,      // added
    val likeCount: Int = 0             // added
)

@Composable
fun PostItem(post: Post, onLikeClicked: () -> Unit) {
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLikeClicked) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) Color.Red else LocalContentColor.current
                )
            }
            Text(text = post.likeCount.toString(), style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { /* TODO: Comment action */ }) {
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
    val context = LocalContext.current

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = {

            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AddCircle, contentDescription = "Post") },
            label = { Text("Post") },
            selected = false,
            onClick = {
                context.startActivity(Intent(context, PostActivity::class.java))
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = {
                context.startActivity(Intent(context, Profilescreen::class.java))
            }
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
