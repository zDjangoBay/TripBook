import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import android.graphics.BitmapFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                FeedScreen()
            }
        }
    }
}

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
    var likeCount by remember { mutableStateOf(post.likes) }

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
                Toast.makeText(context, "Comment clicked!", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "Comment"
                )
            }
            Text(text = "${post.comments}")
        }
    }
}
