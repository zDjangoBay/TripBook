package com.android.tripbook.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.tripbook.model.BoatCompany
import com.android.tripbook.model.Destination
import com.android.tripbook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoatCompaniesScreen(
    navController: NavController,
    boatCompanies: List<BoatCompany>,
    destinations: List<Destination>,
    isLoadingCompanies: Boolean = false,
    isLoadingDestinations: Boolean = false,
    onCompanyClick: (BoatCompany) -> Unit = {},
    onDestinationClick: (Destination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Boat Travel",
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
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Popular Destinations Section
            item {
                Text(
                    text = "Popular Destinations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
            item {
                if (isLoadingDestinations) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(destinations) { destination ->
                            DestinationCard(
                                destination = destination,
                                onClick = { onDestinationClick(destination) }
                            )
                        }
                    }
                }
            }
            // Boat Companies Section
            item {
                Text(
                    text = "Boat Companies",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
            if (isLoadingCompanies) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(boatCompanies) { company ->
                    BoatCompanyCard(
                        company = company,
                        onClick = { onCompanyClick(company) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationCard(
    destination: Destination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Background image or placeholder
            if (destination.imageRes != 0) {
                Image(
                    painter = painterResource(destination.imageRes),
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE1F5FE))
                )
            }

            // Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = destination.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${destination.distance} km away",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Composable
private fun BoatCompanyCard(
    company: BoatCompany,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Company Logo
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(if (company.logoRes != 0) company.logoRes else R.drawable.boat),
                    contentDescription = "Company Logo",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Company Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = company.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${company.rating}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    if (company.totalTrips > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${company.totalTrips} trips",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (company.startingPrice.isNotEmpty()) "From ${company.startingPrice} FCFA" else company.priceRange,
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }

            // Amenities
            if (company.amenities.isNotEmpty()) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row {
                        company.amenities.take(3).forEach { amenity ->
                            Icon(
                                painter = painterResource(getAmenityIcon(amenity)),
                                contentDescription = amenity,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(horizontal = 2.dp)
                            )
                        }
                    }
                    if (company.amenities.size > 3) {
                        Text(
                            text = "+${company.amenities.size - 3} more",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getAmenityIcon(amenity: String): Int {
    return when (amenity.lowercase()) {
        "wifi" -> R.drawable.ic_wifi
        "ac" -> R.drawable.ic_ac
        "charging" -> R.drawable.ic_charging
        "entertainment" -> R.drawable.ic_entertainment
        "snacks", "refreshments" -> R.drawable.ic_snacks
        "blanket" -> R.drawable.ic_blanket
        "life jackets" -> R.drawable.ic_amenity_default
        else -> R.drawable.ic_amenity_default
    }
}