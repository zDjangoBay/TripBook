package com.android.tripbook.screens.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R
import com.android.tripbook.ui.theme.*

data class PostImage(
    val id: String,
    val imageResId: Int,
    val description: String?
)

data class Comment(
    val id: String,
    val user: User,
    val text: String,
    val timestamp: String,
    val likes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    navigateToUserProfile: (String) -> Unit,
    navigateBack: () -> Unit
) {
    // Sample data for post details
    val post = remember {
        TravelPost(
            id = postId,
            title = "Amazing Safari Experience in Serengeti",
            description = "My journey through the Serengeti National Park was truly incredible. I spent five unforgettable days exploring the vast plains and witnessing the abundant wildlife in their natural habitat. " +
                    "The highlight was definitely seeing the big five in just two days! The lions were majestic, the elephants gentle giants, and the leopard sighting was particularly rare and special. " +
                    "\n\nMy guide, Emmanuel, was extremely knowledgeable about the animals and the ecosystem. He shared fascinating stories and facts that made the experience even more enriching. " +
                    "The accommodations at the tented camp were comfortable yet authentic, giving me that true safari experience while not compromising on essential comforts. " +
                    "\n\nI would highly recommend visiting during the migration season if possible. The sight of thousands of wildebeest crossing the Mara River is simply breathtaking. " +
                    "Also, don't forget to bring a good camera with a telephoto lens – you'll want to capture those distant animal sightings!",
            imageResId = R.drawable.app_logo,
            likes = 246,
            comments = 52,
            user = User("1", "John Doe", R.drawable.app_logo),
            destination = "Serengeti National Park, Tanzania"
        )
    }
    
    val postImages = remember {
        listOf(
            PostImage("1", R.drawable.app_logo, "Lion pride resting under a tree"),
            PostImage("2", R.drawable.app_logo, "Elephant herd at the watering hole"),
            PostImage("3", R.drawable.app_logo, "Leopard spotting in a tree"),
            PostImage("4", R.drawable.app_logo, "Sunset over the Serengeti plains"),
            PostImage("5", R.drawable.app_logo, "Giraffes grazing in the distance")
        )
    }
    
    val comments = remember {
        listOf(
            Comment(
                id = "1",
                user = User("2", "Jane Smith", R.drawable.app_logo),
                text = "This looks incredible! I've been planning a safari for ages. Did you book with a travel agency or plan it yourself?",
                timestamp = "2 days ago",
                likes = 8
            ),
            Comment(
                id = "2",
                user = User("3", "Mike Wilson", R.drawable.app_logo),
                text = "Great photos! I was in the Serengeti last year and had a similar experience. Truly a magical place.",
                timestamp = "1 day ago",
                likes = 5
            ),
            Comment(
                id = "3",
                user = User("4", "Sarah Johnson", R.drawable.app_logo),
                text = "That leopard sighting is impressive! They're so elusive.",
                timestamp = "12 hours ago",
                likes = 3
            )
        )
    }
    
    var liked by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Detail") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { saved = !saved }) {
                        Icon(
                            if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Post"
                        )
                    }
                    IconButton(onClick = { /* Share post */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Image Gallery
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    // Main featured image
                    Image(
                        painter = painterResource(id = post.imageResId),
                        contentDescription = post.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Image count indicator
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "1/${postImages.size}",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Thumbnail gallery
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(postImages) { image ->
                        Image(
                            painter = painterResource(id = image.imageResId),
                            contentDescription = image.description,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { /* Switch to this image */ },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            
            // Post Content
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Location
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = post.destination,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    // Title
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // User info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = post.user.profileImageResId),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { navigateToUserProfile(post.user.id) },
                            contentScale = ContentScale.Crop
                        )
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = post.user.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Posted on May 26, 2025",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        
                        Button(
                            onClick = { navigateToUserProfile(post.user.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            ),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Follow")
                        }
                    }
                    
                    // Post Description
                    Text(
                        text = post.description,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Engagement metrics
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { liked = !liked }
                        ) {
                            Icon(
                                if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (liked) Accent else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = if (liked) "${post.likes + 1}" else "${post.likes}",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = "Comments",
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "${post.comments}",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Share",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            
            // Map section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Placeholder for map
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TextSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Map of ${post.destination}",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Comments section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Comments (${post.comments})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Comment input
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Add a comment...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = { /* Submit comment */ },
                                enabled = commentText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = if (commentText.isNotEmpty()) Primary else TextSecondary
                                )
                            }
                        }
                    )
                }
            }
            
            // Comment list
            items(comments) { comment ->
                CommentItem(
                    comment = comment,
                    navigateToUserProfile = { navigateToUserProfile(comment.user.id) }
                )
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    navigateToUserProfile: () -> Unit
) {
    var liked by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = comment.user.profileImageResId),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = navigateToUserProfile),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = comment.user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    Text(
                        text = comment.timestamp,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                
                Text(
                    text = comment.text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    IconButton(
                        onClick = { liked = !liked },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like comment",
                            tint = if (liked) Accent else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Text(
                        text = if (liked) "${comment.likes + 1}" else "${comment.likes}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Reply",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary,
                        modifier = Modifier.clickable { /* Open reply input */ }
                    )
                }
            }
        }
    }
}
