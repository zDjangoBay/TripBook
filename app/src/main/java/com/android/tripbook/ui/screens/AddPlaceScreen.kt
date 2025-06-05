// file: com/android/tripbook/ui/screens/AddPlaceScreen.kt

package com.android.tripbook.ui.screens

import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.android.tripbook.model.Trip // YOUR FINAL Trip model
import com.android.tripbook.viewmodel.MapViewModel // Import your ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    onBack: () -> Unit,
    mapViewModel: MapViewModel = viewModel(), // Get reference to the shared MapViewModel
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> selectedImageUris = uris.take(2) } // Limit to 2 images
    )

    val isFormValid by remember {
        derivedStateOf {
            title.isNotBlank() &&
                    description.isNotBlank() &&
                    city.isNotBlank() &&
                    country.isNotBlank() &&
                    selectedImageUris.isNotEmpty()
        }
    }

    // Function to handle the complete saving logic
    fun handleSave() {
        if (!isFormValid) {
            Toast.makeText(context, "Please fill all required fields marked with *.", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        coroutineScope.launch {
            val addressString = listOf(title, city, region, country).filter { it.isNotBlank() }.joinToString(", ")
            var latitude = 0.0
            var longitude = 0.0
            var geocodingSuccess = false

            // --- Step A: Geocode address string to get latitude and longitude ---
            if (addressString.isNotBlank()) {
                if (Geocoder.isPresent()) {
                    try {
                        val geocoder = Geocoder(context)
                        val addresses = withContext(Dispatchers.IO) {
                            @Suppress("DEPRECATION")
                            geocoder.getFromLocationName(addressString, 1)
                        }
                        if (!addresses.isNullOrEmpty()) {
                            latitude = addresses[0].latitude
                            longitude = addresses[0].longitude
                            geocodingSuccess = true
                            Log.i("AddPlaceScreen", "Geocoding success: Lat $latitude, Lng $longitude")
                        } else {
                            Log.w("AddPlaceScreen", "No location found for address: $addressString")
                            Toast.makeText(context, "Could not find location for the address provided.", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: IOException) {
                        Log.e("AddPlaceScreen", "Geocoding network error", e)
                        Toast.makeText(context, "Geocoding failed: Network or service error.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("AddPlaceScreen", "Geocoder service not available on this device.")
                    Toast.makeText(context, "Location services not available on this device.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Address fields (city, country) are empty.", Toast.LENGTH_LONG).show()
            }

            // --- Step B: Only if geocoding was successful, create and add the Trip ---
            if (geocodingSuccess) {
                val newTrip = Trip(
                    id = mapViewModel.generateNewTripId(), // Use ViewModel to get a new ID
                    title = title.trim(),
                    description = description.trim(),
                    city = city.trim(),
                    country = country.trim(),
                    latitude = latitude,      // Use geocoded latitude
                    longitude = longitude,    // Use geocoded longitude
                    imageUrl = selectedImageUris.map { it.toString() },
                    caption = caption.trim(),
                    region = region.trim().ifEmpty { null }
                )

                // **THE FIX:** Call addTrip on the shared MapViewModel instance.
                // This updates the central state, which automatically updates the UI
                // on TripCatalogScreen, making your new place appear immediately.
                mapViewModel.addTrip(newTrip)

                Toast.makeText(context, "'${newTrip.title}' added!", Toast.LENGTH_SHORT).show()
                onBack() // Navigate back immediately after successful save
            }

            isLoading = false // Reset loading state regardless of outcome
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Trip Destination") },
                navigationIcon = {
                    IconButton(onClick = { if (!isLoading) onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Fields marked with * are required.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Trip Title*") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Short Caption (Optional)") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description*") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City*") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country*") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = region,
                onValueChange = { region = it },
                label = { Text("Region/State (Optional)") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            // Image Picker Section
            Column {
                Text("Add Photos (Required)*", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
                Button(
                    onClick = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth(), enabled = !isLoading
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Add photos icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Images")
                }

                if (selectedImageUris.isNotEmpty()) {
                    LazyRow(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedImageUris) { uri ->
                            AsyncImage(model = uri, contentDescription = "Selected image preview", modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

            Button(
                onClick = { handleSave() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = isFormValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 3.dp)
                } else {
                    Text("Save Trip Destination")
                }
            }
        }
    }
}