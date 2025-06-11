package com.android.tripbook.shared.ui.examples

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.shared.model.Location
import com.android.tripbook.shared.ui.components.LocationPicker
import com.android.tripbook.shared.viewmodel.LocationPickerViewModel

/**
 * Example usage of the LocationPicker component.
 * This demonstrates how to integrate the LocationPicker into your screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerExample(
    modifier: Modifier = Modifier,
    viewModel: LocationPickerViewModel = viewModel()
) {
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Location Picker Example",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "This example demonstrates the improved LocationPicker component with search functionality.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Location Picker
        LocationPicker(
            selectedLocation = selectedLocation,
            onLocationSelected = { location ->
                selectedLocation = location
                locationError = null // Clear error when location is selected
            },
            error = locationError,
            placeholder = "Choose your destination",
            viewModel = viewModel
        )

        // Validation button
        Button(
            onClick = {
                if (selectedLocation == null) {
                    locationError = "Please select a location"
                } else {
                    locationError = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validate Selection")
        }

        // Selected location display
        if (selectedLocation != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selected Location:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Name: ${selectedLocation!!.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (selectedLocation!!.city.isNotEmpty()) {
                        Text(
                            text = "City: ${selectedLocation!!.city}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (selectedLocation!!.country.isNotEmpty()) {
                        Text(
                            text = "Country: ${selectedLocation!!.country}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (selectedLocation!!.hasCoordinates()) {
                        Text(
                            text = "Coordinates: ${selectedLocation!!.coordinates!!.latitude}, ${selectedLocation!!.coordinates!!.longitude}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (selectedLocation!!.address.isNotEmpty()) {
                        Text(
                            text = "Address: ${selectedLocation!!.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Clear selection button
        if (selectedLocation != null) {
            OutlinedButton(
                onClick = {
                    selectedLocation = null
                    locationError = null
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Clear Selection")
            }
        }
    }
}
