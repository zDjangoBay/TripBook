package com.android.tripbook.ui.uis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.android.tripbook.data.model.Agency
import com.android.tripbook.resentation.viewmodel.TravelAgencyListViewModel
import com.android.tripbook.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelAgencyListScreen(
    navController: NavController,
    viewModel: TravelAgencyListViewModel = hiltViewModel()
) {
    val agenciesResource by viewModel.agencies.collectAsState()
    val filterRating by viewModel.filterRating.collectAsState()
    val filterPrice by viewModel.filterPrice.collectAsState()
    val filterService by viewModel.filterService.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Travel Agencies") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery ?: "",
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Search agencies...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Filter Options (Simplified for brevity, would be more complex UI)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Example: Rating Filter
                Text("Min Rating: ${filterRating?.let { "%.1f".format(it) } ?: "Any"}",
                    modifier = Modifier.clickable { /* Show rating filter dialog */ })

                // Example: Price Filter
                Text("Max Price: ${filterPrice?.let { "$%.0f".format(it) } ?: "Any"}",
                    modifier = Modifier.clickable { /* Show price filter dialog */ })

                // Example: Service Filter
                Text("Service: ${filterService ?: "Any"}",
                    modifier = Modifier.clickable { /* Show service filter dialog */ })
            }
            Spacer(modifier = Modifier.height(8.dp))

            when (agenciesResource) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val agencies = agenciesResource.data
                    if (!agencies.isNullOrEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(agencies) { agency ->
                                AgencyCard(agency = agency) {
                                    // Navigate to agency detail screen
                                    navController.navigate("agency_detail/${agency.id}")
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No travel agencies found.")
                        }
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(agenciesResource.message ?: "An unknown error occurred.")
                    }
                }
            }
        }
    }
}

@Composable
fun AgencyCard(agency: Agency, onClick: (Agency) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(agency) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (agency.logoUrl != null) {
                AsyncImage(
                    model = agency.logoUrl,
                    contentDescription = "${agency.name} logo",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder for no logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 12.dp)
                        .Column {
                            Text(agency.name.firstOrNull()?.toString() ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        },
                    contentAlignment = Alignment.Center
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(agency.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700), // Gold color
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${"%.1f".format(agency.rating)} (${agency.reviewCount} reviews)",
                        style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Services: ${agency.servicesOffered.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall)
            }
            if (agency.isVerified) {
                Icon(
                    imageVector = Icons.Default.CheckCircle, // Assuming a checkmark icon is available
                    contentDescription = "Verified Agency",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}