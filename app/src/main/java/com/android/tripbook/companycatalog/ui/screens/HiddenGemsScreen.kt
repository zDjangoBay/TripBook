@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

data class HiddenGem(
    val name: String,
    val location: String,
    val description: String,
    val rating: Float,
    val coordinates: String
)

@Composable
fun HiddenGemsScreen() {
    val hiddenGems = remember {
        listOf(
            HiddenGem(
                name = "Ekom Nkam Waterfalls",
                location = "Melong, Littoral Region",
                description = "Spectacular 80-meter waterfall hidden in the rainforest. Perfect for photography and swimming.",
                rating = 4.7f,
                coordinates = "5.1017°N, 10.1333°E"
            ),
            HiddenGem(
                name = "Lac Barombi Mbo",
                location = "Near Kumba, Southwest Region",
                description = "Volcanic crater lake with unique endemic fish species. Pristine and rarely visited.",
                rating = 4.5f,
                coordinates = "4.6667°N, 9.4000°E"
            ),
            HiddenGem(
                name = "Mandara Mountains Caves",
                location = "Far North Region",
                description = "Ancient caves with traditional rock paintings. Guided tours available from local communities.",
                rating = 4.3f,
                coordinates = "10.8500°N, 14.2000°E"
            ),
            HiddenGem(
                name = "Chutes de la Lobé",
                location = "Kribi, South Region",
                description = "Unique waterfall that flows directly into the Atlantic Ocean. Magical sunset views.",
                rating = 4.8f,
                coordinates = "2.9333°N, 9.9167°E"
            ),
            HiddenGem(
                name = "Bamenda Highlands",
                location = "Northwest Region",
                description = "Rolling hills with traditional villages and stunning panoramic views of the Ring Road.",
                rating = 4.4f,
                coordinates = "5.9597°N, 10.1489°E"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Hidden Gems",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Discover secret locations shared by fellow travelers",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(hiddenGems) { gem ->
                HiddenGemCard(gem = gem)
            }
        }
    }
}

@Composable
private fun HiddenGemCard(gem: HiddenGem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = gem.rating.toString(),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = gem.location,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = gem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Coordinates
            Text(
                text = "📍 ${gem.coordinates}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .background(
                        Color.Gray.copy(alpha = 0.1f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}