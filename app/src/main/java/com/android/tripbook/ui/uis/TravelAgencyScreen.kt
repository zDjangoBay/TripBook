package com.android.tripbook.ui.uis


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import com.android.tripbook.ui.components.*
import com.android.tripbook.ui.theme.TripBookColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.service.AgencyService
import com.android.tripbook.service.TravelAgency
import com.android.tripbook.service.TravelAgencyService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelAgencyScreen(
    destination: String,
    travelAgencyService: TravelAgencyService,
    onBackClick: () -> Unit,
    onServiceSelected: (AgencyService, ItineraryType) -> Unit
) {
    var minRating by remember { mutableStateOf<Float?>(null) }
    var maxPrice by remember { mutableStateOf<Int?>(null) }
    var serviceType by remember { mutableStateOf<String?>(null) }

    val agencies by remember(destination, minRating, maxPrice, serviceType) {
        derivedStateOf {
            val destinationAgencies = travelAgencyService.getAgenciesForDestination(destination)
            travelAgencyService.filterAgencies(destinationAgencies, minRating, maxPrice, serviceType)
        }
    }

    TripBookGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // Header
            TripBookHeader(
                title = "Travel Agencies in $destination",
                onBackClick = onBackClick
            )

            // Content Card
            TripBookContentCard {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Filters
                    TripBookSectionTitle("Filter Services")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TripBookFilterChip(
                            selected = minRating != null,
                            onClick = {
                                minRating = if (minRating == null) 4.0f else null
                            },
                            label = "Rating 4.0+"
                        )
                        TripBookFilterChip(
                            selected = maxPrice != null,
                            onClick = {
                                maxPrice = if (maxPrice == null) 300 else null
                            },
                            label = "Price ≤ $300"
                        )
                        TripBookFilterChip(
                            selected = serviceType != null,
                            onClick = {
                                serviceType = if (serviceType == null) "Tour" else null
                            },
                            label = "Tours Only"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Agency List or Empty State
                    if (agencies.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No travel agencies found for $destination",
                                fontSize = 16.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Try adjusting filters or selecting a different destination",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(agencies) { agency ->
                                AgencyCard(
                                    agency = agency,
                                    onServiceClick = { service ->
                                        val itineraryType = when (service.type) {
                                            "Tour" -> ItineraryType.ACTIVITY
                                            "Accommodation" -> ItineraryType.ACCOMMODATION
                                            "Transportation" -> ItineraryType.TRANSPORTATION
                                            else -> ItineraryType.ACTIVITY
                                        }
                                        onServiceSelected(service, itineraryType)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgencyCard(agency: TravelAgency, onServiceClick: (AgencyService) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = agency.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${agency.rating}/5.0",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            agency.services.forEach { service ->
                ServiceItem(service = service, onClick = { onServiceClick(service) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ServiceItem(service: AgencyService, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = service.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A202C)
            )
            Text(
                text = service.description,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "XAF ${service.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667EEA)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Service Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${service.rating}/5.0",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}