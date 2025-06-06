package com.android.tripbook.ui.comparison

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class) // This is needed for TopAppBar in Material 3
@Composable
fun ComparisonGraphScreen(
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar( // Corrected TopAppBar usage for Material 3
                title = { Text("Comparison Graph") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // This actions block is required for TopAppBar in Material 3, even if empty
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Planned vs. Actual Costs by Category",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            // TODO: Replace with actual grouped bar chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Bar Chart Placeholder", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}