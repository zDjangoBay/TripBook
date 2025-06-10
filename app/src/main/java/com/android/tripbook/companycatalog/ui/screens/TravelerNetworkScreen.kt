
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Traveler(
    val id: String,
    val name: String,
    val currentLocation: String,
    val homeCountry: String,
    val bio: String,
    val interests: List<String>,
    val traveledCountries: Int,
    val isOnline: Boolean,
    val mutualConnections: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelerNetworkScreen() {
    var searchQuery by remember { mutableStateOf("") }
    
    val travelers = remember {
        listOf(
            Traveler(
                id = "1",
                name = "Alexandra Kane",
                currentLocation = "Cape Town, South Africa",
                homeCountry = "United States",
                bio = "Digital nomad exploring Africa's startup scene. Love hiking, photography, and local cuisines.",
                interests = listOf("Photography", "Hiking", "Tech", "Food"),
                traveledCountries = 28,
                isOnline = true,
                mutualConnections = 5
            ),
            Traveler(
                id = "2",
                name = "Kwame Asante",
                currentLocation = "Accra, Ghana",
                homeCountry = "Ghana",
                bio = "Local guide and adventure enthusiast. Helping travelers discover authentic Ghana experiences.",
                interests = listOf("Culture", "History", "Adventure", "Music"),
                traveledCountries = 12,
                isOnline = false,
                mutualConnections = 3
            ),
            Traveler(
                id = "3",
                name = "Sofia Rodriguez",
                currentLocation = "Marrakech, Morocco",
                homeCountry = "Spain",
                bio = "Travel blogger documenting hidden gems across North Africa. Always up for spontaneous adventures!",
                interests = listOf("Blogging", "Culture", "Art", "Adventure"),
                traveledCountries = 34,
                isOnline = true,
                mutualConnections = 8
            ),
            Traveler(
                id = "4",
                name = "Joseph Mbeki",
                currentLocation = "Nairobi, Kenya",
                homeCountry = "Kenya",
                bio = "Wildlife photographer and safari guide. Passionate about conservation and sharing Africa's beauty.",
                interests = listOf("Wildlife", "Photography", "Conservation", "Safari"),
                traveledCountries = 15,
                isOnline = true,
                mutualConnections = 2
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Traveler Network",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        
        Text(
            text = "Connect with fellow adventurers",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search travelers...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("Nearby", "Online", "New Members", "Guides", "Photographers")) { filter ->
                AssistChip(
                    onClick = { /* Apply filter */ },
                    label = { Text(filter) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFE8F5E8)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Travelers list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(travelers) { traveler ->
                TravelerCard(traveler = traveler)
            }
        }
    }
}

@Composable
fun TravelerCard(traveler: Traveler) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = traveler.name.split(" ").map { it.first() }.joinToString(""),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        
                        // Online indicator
                        if (traveler.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = traveler.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = traveler.currentLocation,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "From ${traveler.homeCountry}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // Connection status
                if (traveler.mutualConnections > 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Text(
                            text = "${traveler.mutualConnections} mutual",
                            fontSize = 10.sp,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            Text(
                text = traveler.bio,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stats
            Text(
                text = "🌍 ${traveler.traveledCountries} countries visited",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Interests
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(traveler.interests) { interest ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                    ) {
                        Text(
                            text = interest,
                            fontSize = 10.sp,
                            color = Color(0xFF558B2F),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Connect */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = "Connect",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Connect")
                }
                
                Button(
                    onClick = { /* Message */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(
                        Icons.Default.Message,
                        contentDescription = "Message",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Message")
                }
            }
        }
    }
}
