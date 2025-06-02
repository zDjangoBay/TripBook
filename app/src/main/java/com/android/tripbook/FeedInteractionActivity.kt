package com.example.tripbook.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tripbook.Post



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


@Composable
fun PostInteractionSection(post: Post) {
    val context = LocalContext.current
    val firestore = remember { FirebaseFirestore.getInstance() }

    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(post.likes) }
    var showCommentBox by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val newLikeStatus = !isLiked
                val increment = if (newLikeStatus) 1 else -1
                val postRef = firestore.collection("post").document(post.id)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)
                    val currentLikes = snapshot.getLong("likes") ?: 0
                    transaction.update(postRef, "likes", currentLikes + increment)
                }.addOnSuccessListener {
                    isLiked = newLikeStatus
                    likeCount += increment
                    Toast.makeText(context, if (isLiked) "Liked!" else "Unliked!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
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

            IconButton(onClick = { showCommentBox = !showCommentBox }) {
                Icon(Icons.Filled.ChatBubble, contentDescription = "Comment")
            }
            Text(text = "${post.comments}")

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "${post.username}'s post: ${post.caption}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share post via"))
            }) {
                Icon(Icons.Filled.Share, contentDescription = "Share")
            }
        }

        if (showCommentBox) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Write a comment...") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        val comment = mapOf("text" to commentText, "timestamp" to System.currentTimeMillis())
                        val postRef = firestore.collection("post").document(post.id)
                        postRef.collection("comments").add(comment).addOnSuccessListener {
                            Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
                            commentText = ""
                            firestore.runTransaction { transaction ->
                                val snapshot = transaction.get(postRef)
                                val currentComments = snapshot.getLong("comments") ?: 0
                                transaction.update(postRef, "comments", currentComments + 1)
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Comment failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text("Post Comment")
            }
        }
    }
}
