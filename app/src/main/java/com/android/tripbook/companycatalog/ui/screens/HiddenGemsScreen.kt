
@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
<<<<<<< HEAD
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
=======
import androidx.compose.material.icons.filled.*
>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
=======
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3

data class HiddenGem(
    val name: String,
    val location: String,
    val description: String,
<<<<<<< HEAD
    val rating: Float,
    val coordinates: String
=======
    val difficulty: String,
    val estimatedTime: String,
    val highlights: List<String>,
    val icon: ImageVector
>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3
)

@Composable
fun HiddenGemsScreen() {
<<<<<<< HEAD
    val hiddenGems = remember {
        listOf(
            HiddenGem(
                id = "1",
                name = "Bana Sacred Forest",
                location = "Bana, West Region, Cameroon",
                description = "A mystical forest sanctuary where local Bamiléké traditions are preserved. Ancient trees house spirits according to local beliefs, and traditional healers gather medicinal plants.",
                category = "Culture & Nature",
                discoveredBy = "Marie Tchoua",
                rating = 4.8f,
                reviewCount = 23,
                imageResId = R.drawable.mock_logo_1,
                tips = "Visit with local guide from Bana village. Respect traditional customs and ask permission before taking photos.",
                timeAgo = "2 weeks ago"
            ),
            HiddenGem(
                id = "2",
                name = "Limbe Botanical Garden Night Trail",
                location = "Limbe, Southwest Region, Cameroon",
                description = "Secret night trails through Cameroon's oldest botanical garden. Experience nocturnal wildlife and bioluminescent plants guided by local botanists.",
                category = "Nature & Adventure",
                discoveredBy = "Dr. Emmanuel Taku",
                rating = 4.6f,
                reviewCount = 18,
                imageResId = R.drawable.mock_logo_2,
                tips = "Book night tours through Limbe Wildlife Centre. Bring insect repellent and red flashlight to preserve night vision.",
                timeAgo = "1 month ago"
            ),
            HiddenGem(
                id = "3",
                name = "Foumban Royal Pottery Village",
                location = "Foumban, West Region, Cameroon",
                description = "Hidden artisan quarter where Bamoun royal pottery traditions continue unchanged for centuries. Watch master potters create ceremonial vessels for the Sultan's palace.",
                category = "Culture & Art",
                discoveredBy = "Ibrahim Njoya",
                rating = 4.4f,
                reviewCount = 31,
                imageResId = R.drawable.mock_logo_3,
                tips = "Visit during mornings when clay is being prepared. Learn basic Bamoun greetings - 'Mbé ni wé' means good morning!",
                timeAgo = "3 weeks ago"
            ),
            HiddenGem(
                id = "4",
                name = "Rhumsiki Moonlight Market",
                location = "Rhumsiki, Far North Region, Cameroon",
                description = "A monthly market held under the full moon among the Mandara Mountains' dramatic rock formations. Local Kapsiki people trade traditional crafts and millet beer.",
                category = "Culture & Food",
                discoveredBy = "Paul Mbarga",
                rating = 4.9f,
                reviewCount = 42,
                imageResId = R.drawable.mock_logo_4,
                tips = "Check lunar calendar - market happens 3 days before full moon. Try local millet beer and dried fish specialties.",
                timeAgo = "1 week ago"
            ),
            HiddenGem(
                id = "5",
                name = "Dja Reserve Pygmy Village Experience",
                location = "Dja Biosphere Reserve, East Region, Cameroon",
                description = "Authentic cultural exchange with Baka pygmy communities deep in the rainforest. Learn traditional hunting techniques, forest medicine, and honey gathering.",
                category = "Culture & Nature",
                discoveredBy = "Anatole Biyiha",
                rating = 4.7f,
                reviewCount = 12,
                imageResId = R.drawable.mock_logo_5,
                tips = "Contact local NGOs for ethical visits. Bring small gifts like salt or soap. Respect photography restrictions in sacred areas.",
                timeAgo = "4 days ago"
            ),
            HiddenGem(
                id = "6",
                name = "Edéa Bridge Manatee Watching",
                location = "Edéa, Littoral Region, Cameroon",
                description = "Best kept secret for spotting West African manatees in Sanaga River. Early morning boat trips offer chances to see these gentle giants in their natural habitat.",
                category = "Wildlife & Nature",
                discoveredBy = "Christine Ndongo",
                rating = 4.5f,
                reviewCount = 27,
                imageResId = R.drawable.mock_logo_1,
                tips = "Best viewing between 6-8 AM when manatees surface to breathe. Bring binoculars and maintain respectful distance.",
                timeAgo = "5 days ago"
            )
        )
    }

    val categories = listOf("All", "Culture & Nature", "Nature & Adventure", "Culture & Art", "Culture & Food", "Wildlife & Nature")
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
                    text = "Discover Cameroon's secrets",
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

            // Info chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    label = gem.difficulty,
                    icon = Icons.Default.Terrain
                )
                InfoChip(
                    label = gem.estimatedTime,
                    icon = Icons.Default.Schedule
                )
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
                Image(
                    painter = painterResource(id = gem.imageResId),
                    contentDescription = gem.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Rating badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${gem.rating}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Category tag
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = gem.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Content section
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = gem.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gem.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = gem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tips section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Local Tip",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = gem.tips,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32),
                            lineHeight = 16.sp
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
                    Column {
                        Text(
                            text = "Discovered by ${gem.discoveredBy}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = gem.timeAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "${gem.reviewCount} reviews",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
