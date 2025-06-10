package com.android.tripbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.android.tripbook.model.TrainCompany
import com.android.tripbook.model.TrainDestination
import com.android.tripbook.data.TrainMockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainCompaniesScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // State to trigger recomposition when ratings change
    var refreshKey by remember { mutableIntStateOf(0) }

    // Track which destinations have been rated (in a real app, this would be stored in a database/preferences)
    var ratedDestinations by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Train Services",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Train Companies",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            items(TrainMockData.trainCompanies) { company ->
                TrainCompanyCard(
                    company = company,
                    onClick = { /* Handle company click */ }
                )
            }
            item {
                Text(
                    text = "Popular Train Destinations",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )
            }
            items(
                items = TrainMockData.trainDestinations,
                key = { "${it.id}_${refreshKey}" }
            ) { destination ->
                TrainDestinationCard(
                    destination = destination,
                    onClick = { /* Handle destination click */ },
                    onRatingChange = { newRating ->
                        TrainMockData.updateDestinationRating(destination.id, newRating)
                        ratedDestinations = ratedDestinations + destination.id
                        refreshKey++
                    },
                    hasBeenRated = ratedDestinations.contains(destination.id)
                )
            }
        }
    }
}

@Composable
fun TrainCompanyCard(
    company: TrainCompany,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (company.logoUrl != null) {
                AsyncImage(
                    model = company.logoUrl,
                    contentDescription = "${company.name} logo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback to placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Train,
                        contentDescription = "Train",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = company.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = company.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = company.rating.toString(),
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = company.priceRange,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TrainDestinationCard(
    destination: TrainDestination,
    onClick: () -> Unit,
    onRatingChange: (Float) -> Unit,
    hasBeenRated: Boolean,
    modifier: Modifier = Modifier
) {
    var showRatingDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Destination Image
            if (destination.imageUrl != null) {
                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            color = Color(0xFFE8F5E8),
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Train,
                            contentDescription = "Train",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = destination.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Destination name
                Text(
                    text = destination.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Rating section - separate row for better visibility
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current rating display
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (destination.totalRatings > 0) {
                                String.format("%.1f (%d)", destination.rating, destination.totalRatings)
                            } else {
                                "No ratings yet"
                            },
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    // Rate button - show different states based on rating status
                    if (hasBeenRated) {
                        // Already rated - show confirmation
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF4CAF50),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Rated",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Rated",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Not rated yet - show rate button
                        Button(
                            onClick = { showRatingDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Rate",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = destination.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(
                        label = "Duration",
                        value = destination.duration,
                        backgroundColor = Color(0xFFE8F5E8)
                    )
                    InfoChip(
                        label = "Price",
                        value = destination.price,
                        backgroundColor = Color(0xFFFFF3E0)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(
                        label = "Distance",
                        value = destination.distance,
                        backgroundColor = Color(0xFFE3F2FD)
                    )
                    InfoChip(
                        label = "Popular",
                        value = destination.popularTimes,
                        backgroundColor = Color(0xFFF3E5F5)
                    )
                }
            }
        }
    }

    // Rating Dialog - only show if not already rated
    if (showRatingDialog && !hasBeenRated) {
        TrainRatingDialog(
            onDismiss = { showRatingDialog = false },
            onRatingSelected = { rating ->
                onRatingChange(rating)
                showRatingDialog = false
            },
            destinationName = destination.name
        )
    }
}

@Composable
fun TrainRatingDialog(
    onDismiss: () -> Unit,
    onRatingSelected: (Float) -> Unit,
    destinationName: String
) {
    var selectedRating by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rate $destinationName",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How would you rate this train destination?",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "You can only rate each destination once",
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (i <= selectedRating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { selectedRating = i }
                        )
                    }
                }

                if (selectedRating > 0) {
                    Text(
                        text = when (selectedRating) {
                            1 -> "Poor"
                            2 -> "Fair"
                            3 -> "Good"
                            4 -> "Very Good"
                            5 -> "Excellent"
                            else -> ""
                        },
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedRating > 0) {
                        onRatingSelected(selectedRating.toFloat())
                    }
                },
                enabled = selectedRating > 0
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InfoChip(
    label: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}