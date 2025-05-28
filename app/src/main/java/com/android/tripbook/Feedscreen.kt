package com.example.tripbook

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class Post(
    val id: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val caption: String = "",
    val imageBase64: String = "",
    val likes: Int = 0,
    val comments: Int = 0,
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    unreadNotifications: Int,
    unreadMessages: Int,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit
) {
    TopAppBar(
        title = { Text("TripBook") },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                if (unreadNotifications > 0) {
                    BadgedBox(
                        badge = {
                            Badge { Text(unreadNotifications.toString()) }
                        }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                } else {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }
            IconButton(onClick = onMessagesClick) {
                if (unreadMessages > 0) {
                    BadgedBox(
                        badge = {
                            Badge { Text(unreadMessages.toString()) }
                        }
                    ) {
                        Icon(Icons.Default.Message, contentDescription = "Messages")
                    }
                } else {
                    Icon(Icons.Default.Message, contentDescription = "Messages")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    val context = LocalContext.current
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    val firestore = remember { Firebase.firestore }

    var unreadNotifications by remember { mutableStateOf(3) }
    var unreadMessages by remember { mutableStateOf(2) }

    LaunchedEffect(true) {
        try {
            val snapshot = firestore.collection("post").get().await()
            posts = snapshot.documents.mapNotNull { doc ->
                val username = doc.getString("username") ?: "Unknown"
                val profileImageUrl = doc.getString("profileImageUrl") ?: ""
                val caption = doc.getString("caption") ?: return@mapNotNull null
                val imageBase64 = doc.getString("imageBase64") ?: ""
                val likes = doc.getLong("likes")?.toInt() ?: 0
                val comments = doc.getLong("comments")?.toInt() ?: 0
                Post(
                    id = doc.id,
                    username = username,
                    profileImageUrl = profileImageUrl,
                    caption = caption,
                    imageBase64 = imageBase64,
                    likes = likes,
                    comments = comments
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load posts: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            MainTopBar(
                unreadNotifications = unreadNotifications,
                unreadMessages = unreadMessages,
                onNotificationsClick = {
                    Toast.makeText(context, "Notifications clicked!", Toast.LENGTH_SHORT).show()
                    unreadNotifications = 0
                },
                onMessagesClick = {
                    Toast.makeText(context, "Messages clicked!", Toast.LENGTH_SHORT).show()
                    unreadMessages = 0
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(posts) { post ->
                PostItem(post = post)
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    val context = LocalContext.current
    val firestore = remember { FirebaseFirestore.getInstance() }
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(post.likes) }
    var showCommentBox by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            if (post.profileImageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.profileImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
            } else {

                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ) {}
            }
            Text(text = post.username, style = MaterialTheme.typography.titleMedium)
        }

        if (post.imageBase64.isNotBlank()) {
            val imageBytes = Base64.decode(post.imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }
        } else if (post.imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = post.caption, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val newLikeStatus = !isLiked
                val increment = if (newLikeStatus) 1 else -1
                val postRef = firestore.collection("post").document(post.id)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)
                    val currentLikes = snapshot.getLong("likes") ?: 0
                    val updatedLikes = currentLikes + increment
                    transaction.update(postRef, "likes", updatedLikes)
                }.addOnSuccessListener {
                    isLiked = newLikeStatus
                    likeCount += increment
                    Toast.makeText(
                        context,
                        if (isLiked) "Liked!" else "Unliked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update like: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Like",
                    tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(text = "$likeCount")

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = {
                showCommentBox = !showCommentBox
            }) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "Comment"
                )
            }
            Text(text = "${post.comments}")
        }

        if (showCommentBox) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Write a comment...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                if (commentText.isNotBlank()) {
                    val commentMap = mapOf("text" to commentText, "timestamp" to System.currentTimeMillis())
                    val postRef = firestore.collection("post").document(post.id)
                    val commentsRef = postRef.collection("comments")

                    commentsRef.add(commentMap).addOnSuccessListener {
                        Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
                        commentText = ""

                        firestore.runTransaction { transaction ->
                            val snapshot = transaction.get(postRef)
                            val currentComments = snapshot.getLong("comments") ?: 0
                            transaction.update(postRef, "comments", currentComments + 1)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to post comment", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Post Comment")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    MaterialTheme {
        Scaffold(
            topBar = {
                MainTopBar(
                    unreadNotifications = 5,
                    unreadMessages = 2,
                    onNotificationsClick = {},
                    onMessagesClick = {}
                )
            }
        ) { paddingValues ->
            FeedScreenPreviewContent(
                modifier = Modifier.padding(paddingValues),
                posts = listOf(
                    Post(
                        id = "1",
                        username = "Alice",
                        profileImageUrl = "https://randomuser.me/api/portraits/women/1.jpg",
                        caption = "Enjoying the beach!",
                        imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
                        likes = 12,
                        comments = 3
                    ),
                    Post(
                        id = "2",
                        username = "Bob",
                        profileImageUrl = "https://randomuser.me/api/portraits/men/2.jpg",
                        caption = "Hiking through the mountains",
                        imageUrl = "https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=800&q=80",
                        likes = 8,
                        comments = 2
                    )
                )
            )
        }
    }
}

@Composable
fun FeedScreenPreviewContent(modifier: Modifier = Modifier, posts: List<Post>) {
    LazyColumn(modifier = modifier) {
        items(posts) { post ->
            PostItemPreview(post)
        }
    }
}

@Composable
fun PostItemPreview(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            if (post.profileImageUrl.isNotBlank()) {
                AsyncImage(
                    model = post.profileImageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ) {}
            }
            Text(text = post.username, style = MaterialTheme.typography.titleMedium)
        }

        if (post.imageUrl.isNotBlank()) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = post.caption, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Likes",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${post.likes}")

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Filled.ChatBubble,
                contentDescription = "Comments"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${post.comments}")
        }
    }
}
