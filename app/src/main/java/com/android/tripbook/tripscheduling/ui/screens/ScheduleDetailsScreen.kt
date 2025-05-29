package com.android.tripbook.tripscheduling.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

@Composable
fun ScheduleDetailsScreen(scheduleId: String, onNavigateBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onNavigateBack) {
            Text("Back")
        }

        // Display schedule details (mock for now)
        Text("Schedule Details for ID: $scheduleId")
    }
}
