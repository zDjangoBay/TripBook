
package com.android.tripbook.ui.screens.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.model.TravelPreferences

@Composable
fun PreferencesForm(
    modifier: Modifier = Modifier,
    prefs: TravelPreferences,
    onSave: (TravelPreferences) -> Unit
) {
    var destinationTypes by remember { mutableStateOf(prefs.destinationTypes.toMutableSet()) }
    var travelStyle by remember { mutableStateOf(prefs.travelStyle) }
    var accommodation by remember { mutableStateOf(prefs.accommodation.toMutableSet()) }
    var activities by remember { mutableStateOf(prefs.activities.toMutableSet()) }
    var transport by remember { mutableStateOf(prefs.transport) }
    var frequency by remember { mutableStateOf(prefs.frequency) }
    var duration by remember { mutableStateOf(prefs.duration) }
    var budget by remember { mutableStateOf(prefs.budget) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Destination Types", style = MaterialTheme.typography.titleMedium)
        listOf("Beaches", "Mountains", "Cities", "Historical Sites", "Countryside", "Adventure Spots").forEach {
            LabeledCheckbox(it, it in destinationTypes) { checked ->
                if (checked) destinationTypes.add(it) else destinationTypes.remove(it)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Travel Style", style = MaterialTheme.typography.titleMedium)
        listOf("Budget", "Mid-range", "Luxury", "Backpacking", "Family").forEach {
            LabeledRadioButton(it, it == travelStyle) { travelStyle = it }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Accommodation", style = MaterialTheme.typography.titleMedium)
        listOf("Hotel", "Hostel", "Apartment", "Camping").forEach {
            LabeledCheckbox(it, it in accommodation) { checked ->
                if (checked) accommodation.add(it) else accommodation.remove(it)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Activities", style = MaterialTheme.typography.titleMedium)
        listOf("Hiking", "Museum Visits", "Nightlife", "Food Tours", "Shopping", "Local Experiences").forEach {
            LabeledCheckbox(it, it in activities) { checked ->
                if (checked) activities.add(it) else activities.remove(it)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Transport", style = MaterialTheme.typography.titleMedium)
        listOf("Flights only", "Flights + Trains", "Road Trips", "Cruises").forEach {
            LabeledRadioButton(it, it == transport) { transport = it }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Travel Frequency", style = MaterialTheme.typography.titleMedium)
        DropdownSelector(
            options = listOf("Weekly", "Monthly", "Quarterly", "Annually"),
            selected = frequency,
            onSelected = { frequency = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Duration: $duration days")
        Slider(
            value = duration.toFloat(),
            onValueChange = { duration = it.toInt() },
            valueRange = 1f..30f
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Budget: $$budget")
        Slider(
            value = budget.toFloat(),
            onValueChange = { budget = it.toInt() },
            valueRange = 100f..5000f
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    destinationTypes.clear()
                    travelStyle = ""
                    accommodation.clear()
                    activities.clear()
                    transport = ""
                    frequency = "Monthly"
                    duration = 1
                    budget = 100
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("Clear")
            }

            Button(
                onClick = {
                    onSave(
                        TravelPreferences(
                            destinationTypes = destinationTypes.toList(),
                            travelStyle = travelStyle,
                            accommodation = accommodation.toList(),
                            activities = activities.toList(),
                            transport = transport,
                            frequency = frequency,
                            duration = duration,
                            budget = budget
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }
}
