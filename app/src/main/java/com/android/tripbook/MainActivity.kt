// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.padding
// import androidx.compose.material3.Scaffold
// import androidx.compose.material3.Text
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview
// import com.android.tripbook.ui.theme.TripBookTheme

// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContent {
//             TripBookTheme {
//                 Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                     Greeting(
//                         name = "Android",
//                         modifier = Modifier.padding(innerPadding)
//                     )
//                 }
//             }
//         }
//     }
// }


// @Composable
// fun Greeting(name: String, modifier: Modifier = Modifier) {
//     Text(
//         text = "Hello $name!",
//         modifier = modifier
//     )
// }

// @Preview
// @Composable
// fun GreetingPreview() {
//     TripBookTheme {
//         Greeting("Android")
//     }
// }

// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.padding
// import androidx.compose.material3.Scaffold
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview
// import com.android.tripbook.ui.screens.CreatePostScreen
// import com.android.tripbook.ui.theme.TripBookTheme

// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContent {
//             TripBookTheme {
//                 Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                     CreatePostScreen(modifier = Modifier.padding(innerPadding))
//                 }
//             }
//         }
//     }
// }

// @Preview(showBackground = true)
// @Composable
// fun PreviewCreatePostScreen() {
//     TripBookTheme {
//         CreatePostScreen()
//     }
// }

// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.padding
// import androidx.compose.material3.Scaffold
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.unit.dp
// import com.android.tripbook.model.PostModel
// import com.android.tripbook.ui.components.PostCard
// import com.android.tripbook.ui.theme.TripBookTheme

// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContent {
//             TripBookTheme {
//                 Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                     MainScreen(Modifier.padding(innerPadding))
//                 }
//             }
//         }
//     }
// }

// @Composable
// fun MainScreen(modifier: Modifier = Modifier) {
//     // Dummy post data
//     val dummyPost = PostModel(
//         title = "Trip to Bali",
//         description = "A beautiful and relaxing trip to Bali with friends. Beaches, sunsets, and more!",
//         imageUrl = "https://picsum.photos/600/300", // You can use this for testing
//         hashtags = listOf("beach", "sunset", "vacation")
//     )

//     Column(modifier = modifier) {
//         PostCard(post = dummyPost, onClick = {
//             // Action when the card is clicked (can be left empty for now)
//         })
//     }
// }

// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.Image
// import androidx.compose.foundation.clickable
// import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.LocationOn
// import androidx.compose.material3.*
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.text.style.TextOverflow
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.unit.dp
// import coil.compose.rememberAsyncImagePainter
// import coil.request.ImageRequest
// import com.android.tripbook.ui.theme.TripBookTheme
// import java.text.SimpleDateFormat
// import java.util.Date
// import java.util.Locale

// data class ImageModel(val uri: String)
// data class TagModel(val name: String)
// data class PostModel(
//     val title: String,
//     val description: String,
//     val location: String,
//     val createdAt: Long,
//     val images: List<ImageModel>,
//     val tags: List<TagModel>,
//     val hashtags: List<String>
// )

// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContent {
//             TripBookTheme {
//                 Surface(
//                     modifier = Modifier.fillMaxSize(),
//                     color = MaterialTheme.colorScheme.background
//                 ) {
//                     PostCard(
//                         post = samplePost,
//                         onPostClick = {}
//                     )
//                 }
//             }
//         }
//     }
// }

// @Composable
// fun PostCard(post: PostModel, onPostClick: (PostModel) -> Unit, modifier: Modifier = Modifier) {
//     val context = LocalContext.current
//     val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

//     Card(
//         modifier = modifier
//             .fillMaxWidth()
//             .padding(16.dp)
//             .clickable { onPostClick(post) },
//         shape = RoundedCornerShape(16.dp),
//         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//     ) {
//         Column(modifier = Modifier.padding(16.dp)) {
//             Row(
//                 modifier = Modifier.fillMaxWidth(),
//                 horizontalArrangement = Arrangement.SpaceBetween,
//                 verticalAlignment = Alignment.Top
//             ) {
//                 Text(
//                     text = post.title,
//                     style = MaterialTheme.typography.headlineSmall,
//                     color = MaterialTheme.colorScheme.onSurface,
//                     maxLines = 2,
//                     overflow = TextOverflow.Ellipsis,
//                     modifier = Modifier.weight(1f)
//                 )
//                 Text(
//                     text = dateFormat.format(Date(post.createdAt)),
//                     style = MaterialTheme.typography.bodySmall,
//                     color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                 )
//             }

//             Spacer(modifier = Modifier.height(8.dp))

//             if (post.location.isNotEmpty()) {
//                 Row(
//                     verticalAlignment = Alignment.CenterVertically,
//                     modifier = Modifier.padding(bottom = 8.dp)
//                 ) {
//                     Icon(
//                         imageVector = Icons.Default.LocationOn,
//                         contentDescription = "Location",
//                         tint = MaterialTheme.colorScheme.primary,
//                         modifier = Modifier.size(16.dp)
//                     )
//                     Spacer(modifier = Modifier.width(4.dp))
//                     Text(
//                         text = post.location,
//                         style = MaterialTheme.typography.bodyMedium,
//                         color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//                     )
//                 }
//             }

//             if (post.images.isNotEmpty()) {
//                 Box(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .aspectRatio(16f / 9f)
//                         .clip(RoundedCornerShape(12.dp))
//                 ) {
//                     Image(
//                         painter = rememberAsyncImagePainter(
//                             ImageRequest.Builder(context)
//                                 .data(post.images.first().uri)
//                                 .build()
//                         ),
//                         contentDescription = "Post Image",
//                         modifier = Modifier.fillMaxWidth(),
//                         contentScale = ContentScale.Crop
//                     )

//                     if (post.images.size > 1) {
//                         Card(
//                             modifier = Modifier
//                                 .align(Alignment.BottomEnd)
//                                 .padding(8.dp),
//                             colors = CardDefaults.cardColors(
//                                 containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
//                             )
//                         ) {
//                             Text(
//                                 text = "+${post.images.size - 1}",
//                                 style = MaterialTheme.typography.bodySmall,
//                                 modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                             )
//                         }
//                     }
//                 }
//                 Spacer(modifier = Modifier.height(8.dp))
//             }

//             Text(
//                 text = post.description,
//                 style = MaterialTheme.typography.bodyMedium,
//                 color = MaterialTheme.colorScheme.onSurface,
//                 maxLines = 3,
//                 overflow = TextOverflow.Ellipsis,
//                 modifier = Modifier.padding(vertical = 8.dp)
//             )

//             if (post.tags.isNotEmpty()) {
//                 Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
//                     post.tags.take(3).forEach { tag ->
//                         AssistChip(
//                             onClick = {},
//                             label = { Text(text = tag.name, style = MaterialTheme.typography.bodySmall) }
//                         )
//                     }
//                     if (post.tags.size > 3) {
//                         Text(
//                             text = "+${post.tags.size - 3} more",
//                             style = MaterialTheme.typography.bodySmall,
//                             color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                             modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                         )
//                     }
//                 }
//             }

//             if (post.hashtags.isNotEmpty()) {
//                 Text(
//                     text = post.hashtags.take(3).joinToString(" "),
//                     style = MaterialTheme.typography.bodySmall,
//                     color = MaterialTheme.colorScheme.primary,
//                     maxLines = 1,
//                     overflow = TextOverflow.Ellipsis
//                 )
//             }
//         }
//     }
// }

// val samplePost = PostModel(
//     title = "Exploring Buea Mountain",
//     description = "We had an amazing time hiking and exploring the beautiful Buea mountain trails!",
//     location = "Mount Buea, Cameroon",
//     createdAt = System.currentTimeMillis(),
//     images = listOf(
//         ImageModel(uri = "https://images.unsplash.com/photo-1506744038136-46273834b3fb")
//     ),
//     tags = listOf(TagModel("Adventure"), TagModel("Hiking"), TagModel("Cameroon"), TagModel("Travel")),
//     hashtags = listOf("#mountain", "#buea", "#tripbook")
// )

// @Preview(showBackground = true)
// @Composable
// fun PostCardPreview() {
//     TripBookTheme {
//         PostCard(post = samplePost, onPostClick = {})
//     }
// }

package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.ui.screens.CreatePostScreen
import com.android.tripbook.ui.screens.PostListScreen
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.ui.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                TripBookApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBookApp() {
    var currentScreen by remember { mutableStateOf("list") }
    val postViewModel: PostViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = when (currentScreen) {
                            "create" -> "Create Post"
                            "list" -> "My Trip Posts"
                            else -> "TripBook"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Posts") },
                    label = { Text("Posts") },
                    selected = currentScreen == "list",
                    onClick = { currentScreen = "list" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Create") },
                    label = { Text("Create") },
                    selected = currentScreen == "create",
                    onClick = { currentScreen = "create" }
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            "create" -> CreatePostScreen(
                viewModel = postViewModel,
                modifier = Modifier.padding(innerPadding),
                onPostCreated = { currentScreen = "list" }
            )
            "list" -> PostListScreen(
                viewModel = postViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripBookAppPreview() {
    TripBookTheme {
        TripBookApp()
    }
}