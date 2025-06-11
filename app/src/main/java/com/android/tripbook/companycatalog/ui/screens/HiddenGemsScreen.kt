@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
importandroidx.compose.foundation.shape.RoundedCornerShape
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

// Mock data for Cameroon hidden gems
private val mockHiddenGems = listOf(
    HiddenGem(
        id = "1",
        name = "Ekom Nkam Waterfalls",
        location = "Melong, Littoral Region",
        description = "A stunning 80-meter waterfall hidden in lush tropical forest. Perfect for photography and nature lovers.",
        category = "Natural Wonder",
        discoveredBy = "Amina Bassong",
        rating = 4.8f,
        reviewCount = 23,
        imageResId = R.drawable.mock_logo_1,
        tips = "Best visited during rainy season (May-October). Wear good hiking shoes.",
        timeAgo = "2 weeks ago"
    ),
    HiddenGem(
        id = "2",
        name = "Lake Nyos",
        location = "Northwest Region",
        description = "A mysterious crater lake with deep blue waters surrounded by volcanic hills.",
        category = "Geological Site",
        discoveredBy = "Dr. Emmanuel Fru",
        rating = 4.6f,
        reviewCount = 15,
        imageResId = R.drawable.mock_logo_2,
        tips = "Guided tours available from Wum. Respect local traditions and customs.",
        timeAgo = "1 month ago"
    ),
    HiddenGem(
        id = "3",
        name = "Bafut Palace Gardens",
        location = "Bafut, Northwest Region",
        description = "Secret gardens behind the royal palace with rare medicinal plants and traditional sculptures.",
        category = "Cultural Heritage",
        discoveredBy = "Princess Ngozi Bafut",
        rating = 4.7f,
        reviewCount = 31,
        imageResId = R.drawable.mock_logo_3,
        tips = "Ask permission from palace officials. Photography may require special permission.",
        timeAgo = "3 days ago"
    ),
    HiddenGem(
        id = "4",
        name = "Mefou Primate Sanctuary Secret Trail",
        location = "Centre Region",
        description = "An unmarked trail leading to a hidden viewpoint over the entire sanctuary.",
        category = "Wildlife Viewing",
        discoveredBy = "Jean-Claude Mvondo",
        rating = 4.9f,
        reviewCount = 12,
        imageResId = R.drawable.mock_logo_4,
        tips = "Early morning visits offer best wildlife viewing. Bring binoculars.",
        timeAgo = "5 days ago"
    ),
    HiddenGem(
        id = "5",
        name = "Limbe Botanical Garden Cave",
        location = "Limbe, Southwest Region",
        description = "A small cave hidden behind the botanical garden with ancient rock formations.",
        category = "Geological Site",
        discoveredBy = "Sarah Eteki",
        rating = 4.4f,
        reviewCount = 18,
        imageResId = R.drawable.mock_logo_5,
        tips = "Bring flashlight. Best combined with botanical garden visit.",
        timeAgo = "1 week ago"
    )
)

@Composable
fun HiddenGemsScreen() {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Natural Wonder", "Geological Site", "Cultural Heritage", "Wildlife Viewing")

    val filteredGems = if (selectedCategory == "All") {
        mockHiddenGems
    } else {
        mockHiddenGems.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hidden Gems",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { /* TODO: Add new gem */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Hidden Gem")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    selected = selectedCategory == category
                )
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

        // Hidden gems list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredGems) { gem ->
                HiddenGemCard(gem = gem)
            }
        }
    }
}

@Composable
private fun HiddenGemCard(gem: HiddenGem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Image
            Image(
                painter = painterResource(id = gem.imageResId),
                contentDescription = gem.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Category badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = gem.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = gem.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = gem.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = gem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tips
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "💡 ${gem.tips}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom row with rating, discovered by, and time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${gem.rating} (${gem.reviewCount})",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Text(
                            text = "Discovered by ${gem.discoveredBy}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = gem.timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}