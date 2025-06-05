// ui/screens/AddPlaceScreen.kt
package com.android.tripbook.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape // Import for RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.data.SampleTrips // Your updated SampleTrips
import com.android.tripbook.model.Trip // Your UNIFIED Trip model

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var ratingString by remember { mutableStateOf("") }
    var reviewCountString by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var latitudeString by remember { mutableStateOf("") }
    var longitudeString by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            selectedImageUris = uris.take(2)
        }
    )

    val isFormValid by remember {
        derivedStateOf {
            title.isNotBlank() &&
                    description.isNotBlank() &&
                    selectedImageUris.isNotEmpty() &&
                    city.isNotBlank() &&
                    country.isNotBlank() &&
                    latitudeString.isNotBlank() && longitudeString.isNotBlank() &&
                    latitudeString.toDoubleOrNull() != null && longitudeString.toDoubleOrNull() != null &&
                    price.isNotBlank() &&
                    duration.isNotBlank() &&
                    (ratingString.toFloatOrNull() ?: -1f) in 0f..5f && // check if null or in range
                    (reviewCountString.toIntOrNull() ?: -1) >= 0 // check if null or non-negative
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Trip Destination") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                label = { Text("Trip Title*") }, // Corrected
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Short Caption (Optional)") }, // Corrected
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Full Description*") }, // Corrected
                modifier = Modifier.fillMaxWidth().height(120.dp),
                // singleLine = false is default for multi-line text fields if not specified
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City*") }, // Corrected
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country*") }, // Corrected
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = region,
                onValueChange = { region = it },
                label = { Text("Region (Optional)") }, // Corrected
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = latitudeString,
                    onValueChange = { latitudeString = it },
                    label = { Text("Latitude*") }, // Corrected
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = longitudeString,
                    onValueChange = { longitudeString = it },
                    label = { Text("Longitude*") }, // Corrected
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
            }

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price (e.g., 50000 FCFA)*") }, // Corrected
                modifier = Modifier.fillMaxWidth(),
                singleLine = true, // Assuming price is single line
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next) // KeyboardType.Number for price often makes sense
            )
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (e.g., 3 days)*") }, // Corrected
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ratingString,
                    onValueChange = { ratingString = it },
                    label = { Text("Rating (0.0-5.0)*") }, // Corrected
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = reviewCountString,
                    onValueChange = { reviewCountString = it },
                    label = { Text("Reviews (e.g., 120)*") }, // Corrected
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
            }

            Column {
                Text("Add Photos (Required, max 2)", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
                Button(
                    onClick = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Add photos icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Images")
                }

                if (selectedImageUris.isNotEmpty()) {
                    LazyRow(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedImageUris) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected image preview",
                                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
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

            Spacer(modifier = Modifier.weight(1f, fill = false)) // Pushes button towards bottom, but only if there's space

            Button(
                onClick = {
                    val latitude = latitudeString.toDoubleOrNull() ?: 0.0
                    val longitude = longitudeString.toDoubleOrNull() ?: 0.0
                    val rating = ratingString.toFloatOrNull() ?: 0.0f
                    val reviewCount = reviewCountString.toIntOrNull() ?: 0

                    if (isFormValid) {
                        val newTrip = Trip(
                            id = SampleTrips.generateId(),
                            title = title.trim(),
                            caption = caption.trim().ifEmpty { "Explore ${title.trim()}" },
                            description = description.trim(),
                            imageUrl = selectedImageUris.map { it.toString() },
                            rating = rating,
                            reviewCount = reviewCount,
                            duration = duration.trim(),
                            latitude = latitude,
                            longitude = longitude,
                            city = city.trim(),
                            country = country.trim(),
                            region = region.trim().ifEmpty { null }
                        )
                        SampleTrips.addTrip(newTrip)
                        onBack()
                    } else {
                        println("Form is invalid. Please fill all required fields correctly.")
                        // Consider showing a Snackbar here for better UX
                        // val snackbarHostState = remember { SnackbarHostState() }
                        // LaunchedEffect(Unit) { snackbarHostState.showSnackbar("Please fill all required fields") }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = isFormValid
            ) {
                Text("Save Trip Destination")
            }
        }
    }
}

// Your existing helper function - you could use this throughout AddPlaceScreen
@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) }, // Correctly wraps label
        modifier = modifier,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        isError = isError
        // You might want to add other common parameters here if needed, like textStyle
    )
}