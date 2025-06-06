package com.android.tripbook.ui.uis

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.compiler.processing.util.Resource
import com.android.tripbook.ui.viewmodel.BookingViewModel
//import com.android.tripbook.util.Resource // Ensure this is imported

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavController,
    agencyId: String?,
    viewModel: BookingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.bookingStatus) {
        when (uiState.bookingStatus) {
            is Resource.Success -> {
                val message = uiState.bookingStatus?.data?.message ?: "Booking successful!"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                // Optionally navigate back or to a confirmation screen
                if (message.contains("confirmed")) {
                    navController.popBackStack(route = "travel_agency_list", inclusive = false)
                }
            }
            is Resource.Error -> {
                val message = uiState.bookingStatus?.message ?: "Booking failed!"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            else -> {} // Do nothing for Loading or null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Service for ${uiState.selectedAgency?.name ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.selectedAgency == null) {
                CircularProgressIndicator()
                Text("Loading agency details...")
            } else {
                Text(
                    text = "Booking for: ${uiState.selectedAgency.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Service Selection Dropdown
                ServiceSelectionDropdown(
                    services = uiState.selectedAgency.servicesOffered,
                    selectedService = uiState.selectedService,
                    onServiceSelected = viewModel::onServiceSelected
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Traveler Information
                Text("Traveler Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.travelerName,
                    onValueChange = viewModel::onTravelerNameChanged,
                    label = { Text("Your Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.travelerEmail,
                    onValueChange = viewModel::onTravelerEmailChanged,
                    label = { Text("Your Email") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.foundation.text.KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.travelerPhone,
                    onValueChange = viewModel::onTravelerPhoneChanged,
                    label = { Text("Your Phone Number") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.foundation.text.KeyboardType.Phone
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Preferred Date (Simplified)
                OutlinedTextField(
                    value = uiState.preferredDate,
                    onValueChange = viewModel::onPreferredDateChanged,
                    label = { Text("Preferred Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Number of Travelers
                OutlinedTextField(
                    value = uiState.numberOfTravelers,
                    onValueChange = viewModel::onNumberOfTravelersChanged,
                    label = { Text("Number of Travelers") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.foundation.text.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Additional Notes
                OutlinedTextField(
                    value = uiState.additionalNotes,
                    onValueChange = viewModel::onAdditionalNotesChanged,
                    label = { Text("Additional Notes (optional)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = viewModel::submitBooking,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Confirm Booking", style = MaterialTheme.typography.titleMedium)
                    }
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSelectionDropdown(
    services: List<String>,
    selectedService: String,
    onServiceSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedService,
            onValueChange = {}, // Read-only
            readOnly = true,
            label = { Text("Select a Service") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            services.forEach { service ->
                DropdownMenuItem(
                    text = { Text(service) },
                    onClick = {
                        onServiceSelected(service)
                        expanded = false
                    }
                )
            }
        }
    }
}