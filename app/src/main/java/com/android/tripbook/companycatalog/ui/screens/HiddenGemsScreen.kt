package com.android.tripbook.companycatalog.ui.screens

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
                reviewCount = 15,
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
                reviewCount = 8,
                imageResId = R.drawable.mock_logo_1,
                tips = "Best viewing at dawn between 5-7 AM. Book through local fishermen's association. Bring binoculars and maintain silence on water.",
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
