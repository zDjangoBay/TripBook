package com.android.tripbook.screens.discovery

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R
import com.android.tripbook.ui.theme.*

// Sample data classes
data class Destination(
    val id: String,
    val name: String,
    val country: String,
    val imageResId: Int,
    val rating: Float
)

data class TravelPost(
    val id: String,
    val title: String,
    val description: String,
    val imageResId: Int,
    val likes: Int,
    val comments: Int,
    val user: User,
    val destination: String
)

data class User(
    val id: String,
    val name: String,
    val profileImageResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    navigateToPostDetail: (String) -> Unit,
    navigateToUserProfile: (String) -> Unit,
    navigateToUserContent: () -> Unit,
    navigateToAgency: (String) -> Unit,
    navigateToAdminAgency: () -> Unit
) {
    // Sample data
    val popularDestinations = listOf(
        Destination("1", "Cape Town", "South Africa", R.drawable.app_logo, 4.8f),
        Destination("2", "Marrakech", "Morocco", R.drawable.app_logo, 4.7f),
        Destination("3", "Zanzibar", "Tanzania", R.drawable.app_logo, 4.9f),
        Destination("4", "Cairo", "Egypt", R.drawable.app_logo, 4.6f)
    )
    
    val recentPosts = listOf(
        TravelPost(
            "1", 
            "Amazing Safari Experience", 
            "My journey through the Serengeti was incredible. Saw the big five in just two days!",
            R.drawable.app_logo, 
            246, 
            52,
            User("1", "John Doe", R.drawable.app_logo),
            "Serengeti, Tanzania"
        ),
        TravelPost(
            "2", 
            "Desert Adventures", 
            "Exploring the Sahara was an unforgettable experience. The dunes seemed endless!",
            R.drawable.app_logo, 
            189, 
            37,
            User("2", "Jane Smith", R.drawable.app_logo),
            "Sahara Desert, Morocco"
        ),
        TravelPost(
            "3", 
            "Victoria Falls Wonder", 
            "Standing at the edge of Victoria Falls was breathtaking. Nature at its finest!",
            R.drawable.app_logo, 
            312, 
            68,
            User("3", "Mike Wilson", R.drawable.app_logo),
            "Victoria Falls, Zimbabwe"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                actions = {
                    IconButton(onClick = { /* Open search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Open notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore") },
                    selected = true,
                    onClick = { /* Already on this screen */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { navigateToUserContent() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /* Navigate to settings */ }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to create post */ },
                containerColor = Primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post", tint = Color.White)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Section: Popular Destinations
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular Destinations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "View All",
                            color = Primary,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { /* Navigate to all destinations */ }
                        )
                    }
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(popularDestinations) { destination ->
                            DestinationCard(destination = destination)
                        }
                    }
                }
            }
            
            item {
                // Section: Recent Travel Posts
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Travel Stories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "View All",
                            color = Primary,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { /* Navigate to all posts */ }
                        )
                    }
                }
            }
            
            items(recentPosts) { post ->
                TravelPostCard(
                    post = post,
                    onPostClick = { navigateToPostDetail(post.id) },
                    onUserClick = { navigateToUserProfile(post.user.id) }
                )
            }
        }
    }
}

@Composable
fun DestinationCard(destination: Destination) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { /* Navigate to destination detail */ },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = destination.imageResId),
                contentDescription = destination.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = destination.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn, 
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = destination.country,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Star, 
                        contentDescription = "Rating",
                        tint = SecondaryLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = destination.rating.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TravelPostCard(
    post: TravelPost,
    onPostClick: () -> Unit,
    onUserClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onPostClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Post Image
            Image(
                painter = painterResource(id = post.imageResId),
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            
            // Post Content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Location and User Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = post.user.profileImageResId),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onUserClick),
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
                            fontSize = 14.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(14.dp),
                                tint = Primary
                            )
                            Text(
                                text = post.destination,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
                
                // Post Title
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Post Description
                Text(
                    text = post.description,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Engagement Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Likes",
                            tint = Accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = post.likes.toString(),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Comment,
                            contentDescription = "Comments",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = post.comments.toString(),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Share",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
