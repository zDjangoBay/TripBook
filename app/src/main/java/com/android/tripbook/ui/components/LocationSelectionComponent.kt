package com.android.tripbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A composable for selecting a location
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionComponent(
    currentLocation: String,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    
    // Mock locations for demonstration purposes
    val mockLocations = remember {
        listOf(
            "Nairobi, Kenya",
            "Cape Town, South Africa",
            "Marrakech, Morocco",
            "Cairo, Egypt",
            "Lagos, Nigeria",
            "Zanzibar, Tanzania",
            "Victoria Falls, Zimbabwe",
            "Serengeti National Park, Tanzania",
            "Johannesburg, South Africa",
            "Accra, Ghana"
        )
    }
    
    // Search functionality
    var searchQuery by remember { mutableStateOf("") }
    val filteredLocations = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            mockLocations
        } else {
            mockLocations.filter {
                it.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    // A fake loading state to simulate API calls
    var isSearching by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Location selection field
    OutlinedTextField(
        value = currentLocation,
        onValueChange = { /* Read-only field */ },
        readOnly = true,
        label = { Text("Location") },
        leadingIcon = {
            Icon(Icons.Default.LocationOn, contentDescription = "Location")
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showLocationDialog = true },
        placeholder = { Text("Add a location") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = LocalContentColor.current,
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        enabled = false
    )
    
    // Location selection dialog
    if (showLocationDialog) {
        Dialog(
            onDismissRequest = { showLocationDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Location",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            // Simulate search behavior
                            if (it.isNotEmpty()) {
                                isSearching = true
                                coroutineScope.launch {
                                    delay(300) // Debounce
                                    isSearching = false
                                }
                            } else {
                                isSearching = false
                            }
                        },
                        label = { Text("Search locations") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Location results
                    if (isSearching) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (filteredLocations.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No locations found")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(filteredLocations) { location ->
                                LocationItem(
                                    location = location,
                                    onSelect = {
                                        onLocationSelected(location)
                                        showLocationDialog = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Custom location button
                    TextButton(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                onLocationSelected(searchQuery)
                                showLocationDialog = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Use custom location")
                    }
                }
            }
        }
    }
}

/**
 * Individual location item in the selection list
 */
@Composable
private fun LocationItem(
    location: String,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = location,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
