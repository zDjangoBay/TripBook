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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.model.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) } // For geocoding loading state
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> selectedImageUris = uris.take(2) }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Trip Destination") },
                navigationIcon = {
                    IconButton(onClick = { if (!isLoading) onBack() }) { // Prevent back while loading
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Trip Title / Specific Place Name*") }, // Clarified label
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Short Caption (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Full Description*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = region,
                onValueChange = { region = it },
                label = { Text("Region/State (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !isLoading
            )

            Column {
                Text(
                    "Add Photos (Required, max 2)",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Add photos")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Images")
                }

                if (selectedImageUris.isNotEmpty()) {
                    LazyRow(
                        Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedImageUris) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected image preview",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Text(
                        "Please select 1 or 2 photos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            Button(
                onClick = {
                    isLoading = true
                    CoroutineScope(Dispatchers.Main).launch {
                        var fetchedLatitude = 0.0
                        var fetchedLongitude = 0.0
                        var geocodingAttempted = false
                        var geocodingSuccess = false

                        // Construct a descriptive address string for better geocoding results
                        val addressParts = mutableListOf<String>()
                        if (title.isNotBlank()) addressParts.add(title.trim()) // Title might be a specific place
                        if (city.isNotBlank()) addressParts.add(city.trim())
                        if (region.isNotBlank()) addressParts.add(region.trim())
                        if (country.isNotBlank()) addressParts.add(country.trim())
                        val addressString = addressParts.joinToString(", ")

                        if (addressString.isNotBlank()) {
                            geocodingAttempted = true
                            if (Geocoder.isPresent()) {
                                try {
                                    val geocoder = Geocoder(context)
                                    val addresses = withContext(Dispatchers.IO) {
                                        Log.d("AddPlaceScreen", "Attempting to geocode: $addressString")
                                        geocoder.getFromLocationName(addressString, 1)
                                    }
                                    if (addresses != null && addresses.isNotEmpty()) {
                                        fetchedLatitude = addresses[0].latitude
                                        fetchedLongitude = addresses[0].longitude
                                        geocodingSuccess = true
                                        Log.i("AddPlaceScreen", "Geocoded '$addressString' to: Lat $fetchedLatitude, Lng $fetchedLongitude")
                                    } else {
                                        Log.w("AddPlaceScreen", "No location found for address: $addressString")
                                        Toast.makeText(context, "Could not find location for the address.", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: IOException) {
                                    Log.e("AddPlaceScreen", "Geocoding network/IO error for '$addressString'", e)
                                    Toast.makeText(context, "Geocoding failed: Network error.", Toast.LENGTH_LONG).show()
                                } catch (e: IllegalArgumentException) {
                                    Log.e("AddPlaceScreen", "Geocoding invalid address for '$addressString'", e)
                                    Toast.makeText(context, "Geocoding failed: Invalid address.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Log.e("AddPlaceScreen", "Geocoder not available on this device.")
                                Toast.makeText(context, "Location service (Geocoder) not available.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Log.w("AddPlaceScreen", "Address string is blank, cannot geocode.")
                            Toast.makeText(context, "Please provide address details (city, country).", Toast.LENGTH_LONG).show()
                        }

                        // Only proceed to save if geocoding was successful or not attempted (though form validation should prevent blank address)
                        // Or if you decide to save with 0.0, 0.0 as a fallback
                        // For now, we save regardless, but lat/lng might be 0.0

                        val newTrip = Trip(
                            id = SampleTrips.generateId(),
                            title = title.trim(),
                            caption = caption.trim().ifEmpty { "Explore ${title.trim()}" },
                            description = description.trim(),
                            imageUrl = selectedImageUris.map { it.toString() },
                            latitude = fetchedLatitude, // Use fetched or default 0.0
                            longitude = fetchedLongitude, // Use fetched or default 0.0
                            city = city.trim(),
                            country = country.trim(),
                            region = region.trim().ifEmpty { null }
                        )
                        SampleTrips.addTrip(newTrip)
                        isLoading = false // Reset loading state
                        Toast.makeText(context, "'${newTrip.title}' added successfully!", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = isFormValid && !isLoading // Disable button during form invalidity or loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary, // Or another contrasting color
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Trip Destination")
                }
            }
        }
    }
}