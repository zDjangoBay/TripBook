@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TravelTip(
    val title: String,
    val description: String,
    val category: String,
    val upvotes: Int,
    val icon: ImageVector
)

@Composable
fun TipsScreen() {
    val tips = remember { getSampleTips() }
    val categories = remember { listOf("All", "Safety", "Culture", "Budget", "Food", "Transport") }
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Travel Tips",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Category filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    selected = selectedCategory == category
                )
            }
        }

        // Tips list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredTips = if (selectedCategory == "All") {
                tips
            } else {
                tips.filter { it.category == selectedCategory }
            }

            items(filteredTips) { tip ->
                TipCard(tip = tip)
            }
        }
    }
}

@Composable
private fun TipCard(tip: TravelTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = tip.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = tip.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvotes",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tip.upvotes.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun getSampleTips(): List<TravelTip> {
    return listOf(
        TravelTip(
            title = "Always carry cash in Cameroon",
            description = "Many local vendors and small restaurants in Cameroon only accept cash payments. ATMs can be scarce outside major cities like Douala and Yaoundé.",
            category = "Budget",
            upvotes = 45,
            icon = Icons.Default.AttachMoney
        ),
        TravelTip(
            title = "Learn basic French phrases",
            description = "French is the official language in most of Cameroon. Learning basic phrases will help you communicate better with locals and enhance your travel experience.",
            category = "Culture",
            upvotes = 38,
            icon = Icons.Default.Language
        ),
        TravelTip(
            title = "Get yellow fever vaccination",
            description = "Yellow fever vaccination is mandatory for entry into Cameroon. Make sure to get vaccinated at least 10 days before travel and carry your vaccination certificate.",
            category = "Safety",
            upvotes = 67,
            icon = Icons.Default.LocalHospital
        ),
        TravelTip(
            title = "Try local Cameroonian cuisine",
            description = "Don't miss dishes like Ndolé (bitter leaf stew), Poulet DG (chicken with plantains), and fresh fish from the coastal regions. Street food is generally safe in busy areas.",
            category = "Food",
            upvotes = 52,
            icon = Icons.Default.Restaurant
        ),
        TravelTip(
            title = "Use registered taxi services",
            description = "In major cities, use registered taxi services or ride-sharing apps. For longer distances, consider using established bus companies like Garanti Express or Central Voyages.",
            category = "Transport",
            upvotes = 41,
            icon = Icons.Default.DirectionsCar
        ),
        TravelTip(
            title = "Respect local customs",
            description = "Cameroon has diverse cultural groups. Dress modestly when visiting rural areas or religious sites. Always ask permission before photographing people.",
            category = "Culture",
            upvotes = 33,
            icon = Icons.Default.People
        )
    )
}