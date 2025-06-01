package com.android.tripbook.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.android.tripbook.screens.discovery.TravelPost
import com.android.tripbook.screens.discovery.TravelPostCard
import com.android.tripbook.screens.discovery.User
import com.android.tripbook.ui.theme.Primary
import com.android.tripbook.ui.theme.Secondary
import com.android.tripbook.ui.theme.TextPrimary
import com.android.tripbook.ui.theme.TextSecondary

data class UserProfile(
    val id: String,
    val name: String,
    val profileImageResId: Int,
    val bio: String,
    val location: String,
    val followers: Int,
    val following: Int,
    val postsCount: Int,
    val traveledCountries: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    navigateToPostDetail: (String) -> Unit,
    navigateBack: () -> Unit
) {
    // Sample user profile data
    val userProfile = remember {
        UserProfile(
            id = userId,
            name = "John Doe",
            profileImageResId = R.drawable.app_logo,
            bio = "Travel enthusiast | Photographer | Nature lover | Visited 25+ countries across 5 continents",
            location = "Cape Town, South Africa",
            followers = 1245,
            following = 356,
            postsCount = 87,
            traveledCountries = 25
        )
    }
    
    // Sample posts data
    val userPosts = remember {
        listOf(
            TravelPost(
                id = "1",
                title = "Amazing Safari Experience",
                description = "My journey through the Serengeti was incredible. Saw the big five in just two days!",
                imageResId = R.drawable.app_logo,
                likes = 246,
                comments = 52,
                user = User(userId, userProfile.name, userProfile.profileImageResId),
                destination = "Serengeti, Tanzania"
            ),
            TravelPost(
                id = "2",
                title = "Victoria Falls Wonder",
                description = "Standing at the edge of Victoria Falls was breathtaking. Nature at its finest!",
                imageResId = R.drawable.app_logo,
                likes = 312,
                comments = 68,
                user = User(userId, userProfile.name, userProfile.profileImageResId),
                destination = "Victoria Falls, Zimbabwe"
            ),
            TravelPost(
                id = "3",
                title = "Cape Town Adventures",
                description = "Exploring Table Mountain and the beautiful beaches of Cape Town. What a city!",
                imageResId = R.drawable.app_logo,
                likes = 189,
                comments = 37,
                user = User(userId, userProfile.name, userProfile.profileImageResId),
                destination = "Cape Town, South Africa"
            )
        )
    }
    
    var isFollowing by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "Photos", "Saved")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userProfile.name) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open more options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
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
            // Profile Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image
                    Image(
                        painter = painterResource(id = userProfile.profileImageResId),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Name
                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Location
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = userProfile.location,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    // Bio
                    Text(
                        text = userProfile.bio,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 32.dp)
                    )
                    
                    // Stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(value = userProfile.postsCount, label = "Posts")
                        StatItem(value = userProfile.followers, label = "Followers")
                        StatItem(value = userProfile.following, label = "Following")
                        StatItem(value = userProfile.traveledCountries, label = "Countries")
                    }
                    
                    // Follow button
                    Button(
                        onClick = { isFollowing = !isFollowing },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) Color.LightGray else Primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isFollowing) "Following" else "Follow",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFollowing) TextPrimary else Color.White
                        )
                    }
                }
            }
            
            // Content Tabs
            item {
                Divider()
                
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                            icon = {
                                when (index) {
                                    0 -> Icon(Icons.Default.List, contentDescription = null)
                                    1 -> Icon(Icons.Default.Photo, contentDescription = null)
                                    2 -> Icon(Icons.Default.Bookmark, contentDescription = null)
                                }
                            }
                        )
                    }
                }
                
                Divider()
            }
            
            // Content based on selected tab
            when (selectedTab) {
                0 -> {
                    // Posts tab
                    items(userPosts) { post ->
                        TravelPostCard(
                            post = post,
                            onPostClick = { navigateToPostDetail(post.id) },
                            onUserClick = { /* Already on user profile */ }
                        )
                    }
                }
                1 -> {
                    // Photos tab - Just a placeholder for now
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Photos Gallery",
                                fontSize = 18.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                2 -> {
                    // Saved tab - Just a placeholder for now
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Saved Posts",
                                fontSize = 18.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatItem(value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}
