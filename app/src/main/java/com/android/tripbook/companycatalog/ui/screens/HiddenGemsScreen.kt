Updated mock data to use Cameroon-based information for HiddenGemsScreen.
```

```kotlin
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R

data class HiddenGem(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val category: String,
    val discoveredBy: String,
    val rating: Float,
    val reviewCount: Int,
    val imageResId: Int,
    val tips: String,
    val timeAgo: String
)

@Composable
fun HiddenGemsScreen() {
    val gems = remember {
        listOf(
            HiddenGem(
                id = "1",
                name = "Sipi Falls Coffee Trail",
                location = "Mount Elgon, Uganda",
                description = "A breathtaking hiking trail that combines stunning waterfalls with authentic coffee farm experiences. Local farmers share their traditional brewing methods.",
                category = "Nature & Culture",
                discoveredBy = "Peter Nakamura",
                rating = 4.8f,
                reviewCount = 23,
                imageResId = R.drawable.mock_logo_1,
                tips = "Visit during harvest season (October-January) for the full coffee experience",
                timeAgo = "2 weeks ago"
            ),
            HiddenGem(
                id = "2",
                name = "Rainbow Beach Caves",
                location = "Kilifi, Kenya",
                description = "Secret limestone caves accessible only during low tide. The walls shimmer with different colors when sunlight hits the water inside.",
                category = "Adventure",
                discoveredBy = "Sarah Kimani",
                rating = 4.6f,
                reviewCount = 18,
                imageResId = R.drawable.mock_logo_2,
                tips = "Check tide times carefully and bring waterproof lights",
                timeAgo = "1 month ago"
            ),
            HiddenGem(
                id = "3",
                name = "Foumban Royal Night Market",
                location = "Foumban, Cameroon",
                description = "A vibrant night market that comes alive after 10 PM. Local artisans sell unique Bamoun handicrafts and traditional street food.",
                category = "Culture & Food",
                discoveredBy = "Ibrahim Njoya",
                rating = 4.4f,
                reviewCount = 31,
                imageResId = R.drawable.mock_logo_3,
                tips = "Bring cash and learn a few Bamoun phrases - locals appreciate the effort!",
                timeAgo = "3 weeks ago"
            ),
            HiddenGem(
                id = "4",
                name = "Rhumsiki Peak Sunrise",
                location = "Rhumsiki, Cameroon",
                description = "A lesser-known viewpoint in the Mandara Mountains. Perfect for sunrise viewing with spectacular volcanic rock formations.",
                category = "Nature",
                discoveredBy = "Paul Mbarga",
                rating = 4.9f,
                reviewCount = 15,
                imageResId = R.drawable.mock_logo_4,
                tips = "Start hiking at 5 AM and bring warm clothes - it gets cold at altitude",
                timeAgo = "1 week ago"
            ),
            HiddenGem(
                id = "5",
                name = "Dja Reserve Canopy Walk",
                location = "Dja Biosphere Reserve, Cameroon",
                description = "A hidden canopy walkway deep in the rainforest. Local guides reveal the secrets of medicinal plants and rare wildlife sightings.",
                category = "Nature & Adventure",
                discoveredBy = "Anatole Biyiha",
                rating = 4.7f,
                reviewCount = 12,
                imageResId = R.drawable.mock_logo_5,
                tips = "Wear insect repellent and bring binoculars for the best wildlife viewing",
                timeAgo = "4 days ago"
            )
        )
    }

    val categories = listOf("All", "Nature & Culture", "Adventure", "Food & Culture", "Culture & History")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hidden Gems",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "Discover local secrets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            FloatingActionButton(
                onClick = { /* Add new gem */ },
                modifier = Modifier.size(56.dp),
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Hidden Gem",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    selected = selectedCategory == category,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gems list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                gems.filter { 
                    selectedCategory == "All" || it.category == selectedCategory 
                }
            ) { gem ->
                HiddenGemCard(gem = gem)
            }
        }
    }
}

@Composable
fun HiddenGemCard(gem: HiddenGem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = gem.imageResId),
                    contentDescription = gem.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Category badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = gem.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Content section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title and rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = gem.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = gem.location,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${gem.rating} (${gem.reviewCount})",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = gem.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tips section
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "💡 Local Tip",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color(0xFF558B2F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = gem.tips,
                            fontSize = 12.sp,
                            color = Color(0xFF558B2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discovered by ${gem.discoveredBy}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = gem.timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
```package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R

data class HiddenGem(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val category: String,
    val discoveredBy: String,
    val rating: Float,
    val reviewCount: Int,
    val imageResId: Int,
    val tips: String,
    val timeAgo: String
)

@Composable
fun HiddenGemsScreen() {
    val gems = remember {
        listOf(
            HiddenGem(
                id = "1",
                name = "Sipi Falls Coffee Trail",
                location = "Mount Elgon, Uganda",
                description = "A breathtaking hiking trail that combines stunning waterfalls with authentic coffee farm experiences. Local farmers share their traditional brewing methods.",
                category = "Nature & Culture",
                discoveredBy = "Peter Nakamura",
                rating = 4.8f,
                reviewCount = 23,
                imageResId = R.drawable.mock_logo_1,
                tips = "Visit during harvest season (October-January) for the full coffee experience",
                timeAgo = "2 weeks ago"
            ),
            HiddenGem(
                id = "2",
                name = "Rainbow Beach Caves",
                location = "Kilifi, Kenya",
                description = "Secret limestone caves accessible only during low tide. The walls shimmer with different colors when sunlight hits the water inside.",
                category = "Adventure",
                discoveredBy = "Sarah Kimani",
                rating = 4.6f,
                reviewCount = 18,
                imageResId = R.drawable.mock_logo_2,
                tips = "Check tide times carefully and bring waterproof lights",
                timeAgo = "1 month ago"
            ),
            HiddenGem(
                id = "3",
                name = "Foumban Royal Night Market",
                location = "Foumban, Cameroon",
                description = "A vibrant night market that comes alive after 10 PM. Local artisans sell unique Bamoun handicrafts and traditional street food.",
                category = "Culture & Food",
                discoveredBy = "Ibrahim Njoya",
                rating = 4.4f,
                reviewCount = 31,
                imageResId = R.drawable.mock_logo_3,
                tips = "Bring cash and learn a few Bamoun phrases - locals appreciate the effort!",
                timeAgo = "3 weeks ago"
            ),
            HiddenGem(
                id = "4",
                name = "Rhumsiki Peak Sunrise",
                location = "Rhumsiki, Cameroon",
                description = "A lesser-known viewpoint in the Mandara Mountains. Perfect for sunrise viewing with spectacular volcanic rock formations.",
                category = "Nature",
                discoveredBy = "Paul Mbarga",
                rating = 4.9f,
                reviewCount = 15,
                imageResId = R.drawable.mock_logo_4,
                tips = "Start hiking at 5 AM and bring warm clothes - it gets cold at altitude",
                timeAgo = "1 week ago"
            ),
            HiddenGem(
                id = "5",
                name = "Dja Reserve Canopy Walk",
                location = "Dja Biosphere Reserve, Cameroon",
                description = "A hidden canopy walkway deep in the rainforest. Local guides reveal the secrets of medicinal plants and rare wildlife sightings.",
                category = "Nature & Adventure",
                discoveredBy = "Anatole Biyiha",
                rating = 4.7f,
                reviewCount = 12,
                imageResId = R.drawable.mock_logo_5,
                tips = "Wear insect repellent and bring binoculars for the best wildlife viewing",
                timeAgo = "4 days ago"
            )
        )
    }

    val categories = listOf("All", "Nature & Culture", "Adventure", "Food & Culture", "Culture & History")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hidden Gems",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "Discover local secrets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            FloatingActionButton(
                onClick = { /* Add new gem */ },
                modifier = Modifier.size(56.dp),
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Hidden Gem",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    selected = selectedCategory == category,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gems list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                gems.filter { 
                    selectedCategory == "All" || it.category == selectedCategory 
                }
            ) { gem ->
                HiddenGemCard(gem = gem)
            }
        }
    }
}

@Composable
fun HiddenGemCard(gem: HiddenGem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = gem.imageResId),
                    contentDescription = gem.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Category badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = gem.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Content section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title and rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = gem.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = gem.location,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${gem.rating} (${gem.reviewCount})",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = gem.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tips section
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "💡 Local Tip",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color(0xFF558B2F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = gem.tips,
                            fontSize = 12.sp,
                            color = Color(0xFF558B2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discovered by ${gem.discoveredBy}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = gem.timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}