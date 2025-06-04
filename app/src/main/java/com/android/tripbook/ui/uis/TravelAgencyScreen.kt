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
    var isLoading by remember { mutableStateOf(false) }
    var agencies by remember { mutableStateOf<List<TravelAgency>>(emptyList()) }

    // Load agencies when screen loads or destination changes
    LaunchedEffect(destination) {
        isLoading = true
        try {
            val allAgencies = travelAgencyService.getAgenciesForDestination(destination)
            agencies = travelAgencyService.filterAgencies(allAgencies, minRating, maxPrice, serviceType)
        } catch (e: Exception) {
            // Handle error
            println("Error loading agencies: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Update filter results
    LaunchedEffect(minRating, maxPrice, serviceType) {
        if (agencies.isNotEmpty()) {
            val allAgencies = travelAgencyService.getAgenciesForDestination(destination)
            agencies = travelAgencyService.filterAgencies(allAgencies, minRating, maxPrice, serviceType)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Travel Agencies in $destination",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Content Card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Filters
                    Text(
                        text = "Filter Services",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151)
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
                            label = { Text("Rating 4.0+") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF667EEA),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = maxPrice != null,
                            onClick = {
                                maxPrice = if (maxPrice == null) 300 else null
                            },
                            label = { Text("Price ≤ $300") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF667EEA),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = serviceType != null,
                            onClick = {
                                serviceType = if (serviceType == null) "Tour" else null
                            },
                            label = { Text("Tours Only") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF667EEA),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Loading indicator
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF667EEA)
                            )
                        }
                    }
                    // Agency List or Empty State
                    else if (agencies.isEmpty()) {
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
                                EnhancedAgencyCard(
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
private fun EnhancedAgencyCard(agency: TravelAgency, onServiceClick: (AgencyService) -> Unit) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = agency.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                    if (agency.address != null) {
                        Text(
                            text = agency.address,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1
                        )
                    }
                }

                // API indicator
                if (agency.isFromApi) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

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
                EnhancedServiceItem(service = service, onClick = { onServiceClick(service) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EnhancedServiceItem(service: AgencyService, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (service.isFromApi) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A202C)
                )
                if (service.isFromApi) {
                    Text(
                        text = "LIVE DATA",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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