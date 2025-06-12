
@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class HiddenGem(
    val name: String,
    val location: String,
    val description: String,
    val difficulty: String,
    val estimatedTime: String,
    val highlights: List<String>,
    val icon: ImageVector
)

@Composable
fun HiddenGemsScreen() {
    val hiddenGems = remember { getSampleHiddenGems() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Hidden Gems in Cameroon",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Discover secret spots and off-the-beaten-path destinations that most tourists never see.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

            // Description
            Text(
                text = gem.description,
                style = MaterialTheme.typography.bodyMedium,
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
