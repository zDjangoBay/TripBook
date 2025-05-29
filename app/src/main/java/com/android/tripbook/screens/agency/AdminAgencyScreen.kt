package com.android.tripbook.screens.agency

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAgencyScreen(
    navigateBack: () -> Unit
) {
    // This is a stub implementation that would be completed in the next phase
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agency Management") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Agency Management Screen - Coming Soon")
        }
    }
}
