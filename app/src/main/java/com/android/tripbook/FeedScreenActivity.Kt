package com.example.tripbook.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.tripbook.models.Post
import com.example.tripbook.components.MainTopBar
import com.example.tripbook.components.PostItem
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
