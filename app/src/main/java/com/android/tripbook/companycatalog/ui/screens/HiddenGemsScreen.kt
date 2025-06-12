<<<<<<< HEAD
@file:OptIn(ExperimentalMaterial3Api::class)

=======

@file:OptIn(ExperimentalMaterial3Api::class)

>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
=======
    val hiddenGems = remember { getSampleHiddenGems() }
>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
<<<<<<< HEAD
            text = "Hidden Gems",
=======
            text = "Hidden Gems in Cameroon",
>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
<<<<<<< HEAD
            text = "Discover secret locations shared by fellow travelers",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
=======
            text = "Discover secret spots and off-the-beaten-path destinations that most tourists never see.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3
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
<<<<<<< HEAD
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

=======
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon and name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = gem.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = gem.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = gem.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3
            // Description
            Text(
                text = gem.description,
                style = MaterialTheme.typography.bodyMedium,
<<<<<<< HEAD
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
=======
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Highlights
            Text(
                text = "Highlights:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            gem.highlights.forEach { highlight ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = highlight,
                        style = MaterialTheme.typography.bodySmall
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
private fun InfoChip(
    label: String,
    icon: ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
>>>>>>> f82f177a06fd7efe7a399c4320c9580979559da3
            )
        }
    }
}

private fun getSampleHiddenGems(): List<HiddenGem> {
    return listOf(
        HiddenGem(
            name = "Ekom-Nkam Waterfalls",
            location = "Melong, Littoral Region",
            description = "A stunning 80-meter waterfall hidden in the rainforest, featured in the movie Greystoke: The Legend of Tarzan. Perfect for nature photography and peaceful meditation.",
            difficulty = "Moderate",
            estimatedTime = "Half day",
            highlights = listOf(
                "80-meter high waterfall",
                "Natural swimming pool",
                "Diverse wildlife spotting",
                "Photography opportunities"
            ),
            icon = Icons.Default.Landscape
        ),
        HiddenGem(
            name = "Lobé Waterfalls",
            location = "Kribi, South Region",
            description = "One of the few waterfalls in the world that flows directly into the ocean. A unique geological phenomenon where river meets Atlantic Ocean.",
            difficulty = "Easy",
            estimatedTime = "2-3 hours",
            highlights = listOf(
                "Waterfall meets ocean",
                "Unique geological formation",
                "Beach access",
                "Local fishing villages"
            ),
            icon = Icons.Default.Waves
        ),
        HiddenGem(
            name = "Mefou National Park",
            location = "Centre Region, near Yaoundé",
            description = "A primate sanctuary and national park home to rescued chimpanzees, gorillas, and other endangered species. Offers guided tours and wildlife education.",
            difficulty = "Easy",
            estimatedTime = "Full day",
            highlights = listOf(
                "Chimpanzee sanctuary",
                "Guided wildlife tours",
                "Conservation education",
                "Forest hiking trails"
            ),
            icon = Icons.Default.Pets
        ),
        HiddenGem(
            name = "Lake Nyos",
            location = "Northwest Region",
            description = "A crater lake with a unique geological history, surrounded by beautiful highlands. Known for its deep blue waters and scientific significance.",
            difficulty = "Challenging",
            estimatedTime = "Full day",
            highlights = listOf(
                "Crater lake formation",
                "Highland scenery",
                "Scientific importance",
                "Remote location experience"
            ),
            icon = Icons.Default.Water
        ),
        HiddenGem(
            name = "Bafut Palace",
            location = "Bafut, Northwest Region",
            description = "Traditional Tikar architecture palace of the Fon of Bafut, showcasing centuries-old Cameroonian royal heritage and cultural traditions.",
            difficulty = "Easy",
            estimatedTime = "2-3 hours",
            highlights = listOf(
                "Traditional architecture",
                "Royal heritage site",
                "Cultural performances",
                "Historical artifacts"
            ),
            icon = Icons.Default.AccountBalance
        ),
        HiddenGem(
            name = "Limbe Botanical Garden",
            location = "Limbe, Southwest Region",
            description = "One of the oldest botanical gardens in Cameroon, featuring rare plant species from the Congo Basin and medicinal plants used by local communities.",
            difficulty = "Easy",
            estimatedTime = "Half day",
            highlights = listOf(
                "Rare plant species",
                "Medicinal plant collection",
                "Congo Basin flora",
                "Educational tours"
            ),
            icon = Icons.Default.LocalFlorist
        )
    )
}
