package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItineraryBuilder(
    onItineraryUpdated: (List<String>) -> Unit
) {
    var stopInput by remember { mutableStateOf("") }
    var stops by remember { mutableStateOf(listOf<String>()) }

    Column {
        OutlinedTextField(
            value = stopInput,
            onValueChange = { stopInput = it },
            label = { Text("Add Itinerary Stop") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (stopInput.isNotBlank()) {
                    stops = stops + stopInput.trim()
                    onItineraryUpdated(stops)
                    stopInput = ""
                }
            }
        ) {
            Text("Add Stop")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(stops) { stop ->
                Text(text = "• $stop", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
