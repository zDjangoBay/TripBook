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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class Post(
    val id: String = "",
    val caption: String = "",
    val imageBase64: String = "",
    val likes: Int = 0,
    val comments: Int = 0
)

@Composable
fun FeedScreen() {
    val context = LocalContext.current
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    val firestore = remember { Firebase.firestore }

    LaunchedEffect(true) {
        try {
            val snapshot = firestore.collection("post").get().await()
            posts = snapshot.documents.mapNotNull { doc ->
                val caption = doc.getString("caption") ?: return@mapNotNull null
                val imageBase64 = doc.getString("imageBase64") ?: return@mapNotNull null
                val likes = doc.getLong("likes")?.toInt() ?: 0
                val comments = doc.getLong("comments")?.toInt() ?: 0
                Post(
                    id = doc.id,
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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(posts) { post ->
            PostItem(post = post)
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

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {

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


val mockBase64Image: String = Base64.encodeToString(
    byteArrayOf(
        -1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, -1, -37, 0, 67,
        0, 8, 6, 6, 7, 6, 5, 8, 7, 7, 7, 9, 9, 8, 10, 12, 20, 13, 12, 11, 11, 12, 25, 18, 19, 15,
        20, 29, 26, 31, 30, 29, 26, 28, 28, 32, 36, 46, 39, 32, 34, 44, 35, 28, 28, 40, 55, 41,
        44, 48, 49, 52, 52, 52, 31, 39, 57, 61, 56, 50, 60, 46, 51, 52, 50
    ),
    Base64.DEFAULT
)

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    MaterialTheme {
        FeedScreenPreviewContent(
            posts = listOf(
                Post(
                    id = "1",
                    caption = "Enjoying the beach!",
                    imageBase64 = mockBase64Image,
                    likes = 12,
                    comments = 3
                ),
                Post(
                    id = "2",
                    caption = "Hiking through the mountains",
                    imageBase64 = mockBase64Image,
                    likes = 8,
                    comments = 2
                )
            )
        )
    }
}


@Composable
fun FeedScreenPreviewContent(posts: List<Post>) {
    LazyColumn {
        items(posts) { post ->
            PostItemPreview(post = post)
        }
    }
}

@Composable
fun PostItemPreview(post: Post) {
    Column(modifier = Modifier.padding(12.dp)) {
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = post.caption, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Icon(Icons.Default.Favorite, contentDescription = null)
            Text("${post.likes}")
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.ChatBubble, contentDescription = null)
            Text("${post.comments}")
        }
    }
}
