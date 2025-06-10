
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TravelTip(
    val id: String,
    val category: String,
    val title: String,
    val content: String,
    val author: String,
    val location: String,
    val upvotes: Int,
    val timeAgo: String
)

@Composable
fun TipsScreen() {
    val tips = remember {
        listOf(
            TravelTip(
                id = "1",
                category = "Money",
                title = "Currency Exchange Tips",
                content = "Always exchange money at official exchange bureaus. Avoid street vendors. Keep receipts for converting back unused currency.",
                author = "Emma Thompson",
                location = "Kenya",
                upvotes = 45,
                timeAgo = "2 days ago"
            ),
            TravelTip(
                id = "2",
                category = "Safety",
                title = "Stay Connected",
                content = "Buy a local SIM card immediately upon arrival. Share your location with family regularly. Keep emergency contacts saved offline.",
                author = "James Mutai",
                location = "Tanzania",
                upvotes = 62,
                timeAgo = "1 week ago"
            ),
            TravelTip(
                id = "3",
                category = "Transport",
                title = "Local Transport Hack",
                content = "Use matatu apps like Ma3Route in Nairobi. Always negotiate taxi fares before getting in. Uber is reliable in major cities.",
                author = "Grace Akinyi",
                location = "Nairobi, Kenya",
                upvotes = 38,
                timeAgo = "3 days ago"
            ),
            TravelTip(
                id = "4",
                category = "Health",
                title = "Water Safety",
                content = "Drink only bottled or boiled water. Use water purification tablets as backup. Avoid ice in drinks unless from reputable places.",
                author = "Dr. Michael Osei",
                location = "Ghana",
                upvotes = 71,
                timeAgo = "5 days ago"
            )
        )
    }

    val categories = listOf("All", "Money", "Safety", "Transport", "Health", "Food", "Culture")
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
            Text(
                text = "Travel Tips",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            
            FloatingActionButton(
                onClick = { /* Add new tip */ },
                modifier = Modifier.size(56.dp),
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Tip",
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

        // Tips List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                tips.filter { 
                    selectedCategory == "All" || it.category == selectedCategory 
                }
            ) { tip ->
                TipCard(tip = tip)
            }
        }
    }
}

@Composable
fun TipCard(tip: TravelTip) {
    var upvoteCount by remember { mutableStateOf(tip.upvotes) }
    var isUpvoted by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Category badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when(tip.category) {
                        "Money" -> Color(0xFF4CAF50)
                        "Safety" -> Color(0xFFFF9800)
                        "Transport" -> Color(0xFF2196F3)
                        "Health" -> Color(0xFFE91E63)
                        else -> Color.Gray
                    }
                ),
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = tip.category,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tip content
            Text(
                text = tip.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tip.content,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Author and location
            Text(
                text = "by ${tip.author} • ${tip.location} • ${tip.timeAgo}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Upvote section
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isUpvoted = !isUpvoted
                        upvoteCount += if (isUpvoted) 1 else -1
                    }
                ) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = if (isUpvoted) Color(0xFF4CAF50) else Color.Gray
                    )
                }
                Text(
                    text = "$upvoteCount helpful",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
