package com.android.tripbook.ui.uis


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.service.AgencyService
import com.android.tripbook.service.TravelAgency
import com.android.tripbook.service.TravelAgencyService
import androidx. compose. foundation. clickable

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF667EEA))
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Travel Agencies in $destination",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            
            // Filters
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Filters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = minRating != null,
                            onClick = {
                                minRating = if (minRating == null) 4.0f else null
                            },
                            label = { Text("4+ Stars") }
                        )
                        
                        FilterChip(
                            selected = maxPrice != null,
                            onClick = {
                                maxPrice = if (maxPrice == null) 200 else null
                            },
                            label = { Text("Under $200") }
                        )
                        
                        FilterChip(
                            selected = serviceType != null,
                            onClick = {
                                serviceType = if (serviceType == null) "Tour" else null
                            },
                            label = { Text("Tours Only") }
                        )
                    }
                }
            }
            
            // Agency List
            if (agencies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No agencies found for $destination",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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

@Composable
fun AgencyCard(
    agency: TravelAgency,
    onServiceClick: (AgencyService) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = agency.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${agency.rating}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Text(
                text = "Services",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            agency.services.forEach { service ->
                ServiceItem(
                    service = service,
                    onClick = { onServiceClick(service) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ServiceItem(
    service: AgencyService,
    onClick: () -> Unit
) {
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
                    text = "$${service.price}",
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