/*
This composable serves as a simple, reusable empty state UI,
providing feedback when no companies match a search or dataset is empty.
 */
package com.android.tripbook.companycatalog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp) // Added top padding to push it down from the very top
            .padding(horizontal = 16.dp), // Maintain horizontal padding
        contentAlignment = Alignment.TopCenter // Changed alignment to top center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

