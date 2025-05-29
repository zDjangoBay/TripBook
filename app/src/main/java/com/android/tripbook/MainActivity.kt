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

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Theme Colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Typography
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
)

// Color Schemes
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

// Data Models
data class ImageModel(val uri: String)
data class TagModel(val name: String)
data class PostModel(
    val title: String,
    val description: String,
    val location: String,
    val createdAt: Long,
    val images: List<ImageModel>,
    val tags: List<TagModel>,
    val hashtags: List<String>
)

// Theme Composable
@Composable
fun TripBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostCard(
                        post = samplePost,
                        onPostClick = { post ->
                            // Handle post click - you can add navigation or other actions here
                            println("Clicked on post: ${post.title}")
                        }
                    )
                }
            }
        }
    }
}

// PostCard Composable
@Composable
fun PostCard(
    post: PostModel, 
    onPostClick: (PostModel) -> Unit, 
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onPostClick(post) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = dateFormat.format(Date(post.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            if (post.location.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Images
            if (post.images.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(post.images.first().uri)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Post Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Multiple images indicator
                    if (post.images.size > 1) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "+${post.images.size - 1}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Description
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Tags
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    post.tags.take(3).forEach { tag ->
                        AssistChip(
                            onClick = { /* Handle tag click */ },
                            label = { 
                                Text(
                                    text = tag.name, 
                                    style = MaterialTheme.typography.bodySmall
                                ) 
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    if (post.tags.size > 3) {
                        Text(
                            text = "+${post.tags.size - 3} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            // Hashtags
            if (post.hashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.hashtags.take(3).joinToString(" "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Sample Data
val samplePost = PostModel(
    title = "Exploring Buea Mountain",
    description = "We had an amazing time hiking and exploring the beautiful Buea mountain trails! The views were absolutely breathtaking and the weather was perfect for our adventure.",
    location = "Mount Buea, Cameroon",
    createdAt = System.currentTimeMillis(),
    images = listOf(
        ImageModel(uri = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80"),
        ImageModel(uri = "https://images.unsplash.com/photo-1551632811-561732d1e306?w=800&q=80"),
        ImageModel(uri = "https://images.unsplash.com/photo-1464822759844-d150vef38eb9?w=800&q=80")
    ),
    tags = listOf(
        TagModel("Adventure"), 
        TagModel("Hiking"), 
        TagModel("Cameroon"), 
        TagModel("Travel"),
        TagModel("Nature")
    ),
    hashtags = listOf("#mountain", "#buea", "#tripbook", "#adventure")
)

// Preview
@Preview(showBackground = true, name = "Light Theme")
@Composable
fun PostCardPreview() {
    TripBookTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PostCard(post = samplePost, onPostClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun PostCardDarkPreview() {
    TripBookTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PostCard(post = samplePost, onPostClick = {})
        }
    }
}