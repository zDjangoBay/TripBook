
package com.android.tripbook.ui.screens.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.ui.viewmodel.TravelPreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPreferencesScreen(
    onBack: () -> Unit,
    viewModel: TravelPreferencesViewModel = viewModel()
) {
    val prefs = viewModel.travelPreferences.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Preferences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            PreferencesForm(
                modifier = Modifier.fillMaxWidth(),
                prefs = prefs.value,
                onSave = {
                    viewModel.savePreferences(it)
                    onBack()
                }
            )
        }
    }
}
