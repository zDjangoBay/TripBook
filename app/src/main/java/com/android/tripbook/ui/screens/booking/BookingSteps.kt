package com.android.tripbook.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Stub booking step components
 * These are placeholders until team members implement the full booking system
 */

@Composable
fun DateSelectionStep(
    onDateSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Date Selection Step",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is a placeholder for date selection functionality")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onDateSelected("2024-01-01", "2024-01-07") }
        ) {
            Text("Select Dates (Placeholder)")
        }
    }
}

@Composable
fun TravelerInfoStep(
    onInfoEntered: (String, String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Traveler Info Step",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is a placeholder for traveler information functionality")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { 
                onInfoEntered("John", "Doe", "john@example.com", "123-456-7890", "123 Main St", "Passport123")
            }
        ) {
            Text("Enter Info (Placeholder)")
        }
    }
}

@Composable
fun AdditionalOptionsStep(
    onOptionsSelected: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Additional Options Step",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is a placeholder for additional options functionality")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onOptionsSelected(listOf("Option 1", "Option 2")) }
        ) {
            Text("Select Options (Placeholder)")
        }
    }
}

@Composable
fun BookingSummaryStep(
    onConfirmBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Booking Summary Step",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is a placeholder for booking summary functionality")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onConfirmBooking
        ) {
            Text("Confirm Booking (Placeholder)")
        }
    }
}
