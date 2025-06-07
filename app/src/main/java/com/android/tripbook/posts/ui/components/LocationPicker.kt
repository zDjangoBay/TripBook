package com.android.tripbook.posts.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Import Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.ViewModel.PostEvent
import com.android.tripbook.posts.ViewModel.PostUIState
import com.android.tripbook.posts.ViewModel.PostViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPicker(
    selectedLocation: Location?,
    onLocationSelected: (Location) -> Unit,
    error: String?, // Validation error for the location field itself
    modifier: Modifier = Modifier,
    viewModel: PostViewModel
) {
    var showLocationDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Determine the border color conditionally
        val borderColor = if (error != null && selectedLocation == null) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.outline
        }

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.handleEvent(PostEvent.ClearLocationSearch)
                    showLocationDialog = true
                },
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (error != null && selectedLocation == null) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface
            ),
            // Apply the conditional border directly
            border = BorderStroke(width = 1.dp, color = borderColor) // Use 1.dp or CardDefaults.outlinedCardBorder.width
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Icon",
                    // Tint the icon based on the same error condition
                    tint = if (error != null && selectedLocation == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                if (selectedLocation != null) {
                    Column {
                        Text(
                            text = selectedLocation.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${selectedLocation.city}, ${selectedLocation.country}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "Select location",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (error != null && selectedLocation == null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    if (showLocationDialog) {
        LocationSelectionDialog(
            uiState = viewModel.uiState.collectAsState().value,
            onLocationSearch = { query ->
                viewModel.handleEvent(PostEvent.SearchLocation(query))
            },
            onLocationSelected = { location ->
                onLocationSelected(location)
                showLocationDialog = false
            },
            onDismiss = {
                viewModel.handleEvent(PostEvent.ClearLocationSearch)
                showLocationDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationSelectionDialog(
    uiState: PostUIState,
    onLocationSearch: (String) -> Unit,
    onLocationSelected: (Location) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Location") },
        text = {
            Column(modifier = Modifier.heightIn(min = 200.dp, max = 450.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        searchQuery = newQuery
                        searchJob?.cancel()
                        if (newQuery.length > 1) {
                            searchJob = coroutineScope.launch {
                                delay(350)
                                onLocationSearch(newQuery)
                            }
                        } else {
                            onLocationSearch("")
                        }
                    },
                    label = { Text("Search city or place name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon")},
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.isSearchingLocation -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp).weight(1f), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.locationSearchError != null -> {
                        Text(
                            text = uiState.locationSearchError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 20.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                    uiState.locationSearchResults.isEmpty() && searchQuery.length > 1 && !uiState.isSearchingLocation -> {
                        Text(
                            "No locations found for \"$searchQuery\".",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 20.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                    uiState.locationSearchResults.isNotEmpty() -> {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(uiState.locationSearchResults, key = { location -> "${location.name}-${location.city}-${location.country}-${location.coordinates?.latitude}-${location.coordinates?.longitude}"}) { location ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onLocationSelected(location) },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = location.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        val cityCountryInfo = mutableListOf<String>()
                                        if (!location.city.isNullOrEmpty() && location.city?.lowercase() != location.name.lowercase()) {
                                            cityCountryInfo.add(location.city!!)
                                        }
                                        if (!location.country.isNullOrEmpty()) {
                                            cityCountryInfo.add(location.country!!)
                                        }
                                        if (cityCountryInfo.isNotEmpty()) {
                                            Text(
                                                text = cityCountryInfo.joinToString(", "),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            "Start typing to search for a location.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 20.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
