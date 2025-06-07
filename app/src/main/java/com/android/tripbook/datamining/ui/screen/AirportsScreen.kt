package com.android.tripbook.datamining.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.android.tripbook.datamining.ui.components.AirportDetails
import com.android.tripbook.datamining.ui.components.AirportFiltersDialog
import com.android.tripbook.datamining.ui.components.AirportsList
import com.android.tripbook.datamining.ui.viewmodel.AirportViewModel

/**
 * Main screen for the airports feature
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirportsScreen(
    viewModel: AirportViewModel,
    onNavigateBack: () -> Unit
) {
    val airports by viewModel.filteredAirports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedAirport by viewModel.selectedAirport.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val availableCountries by viewModel.availableCountries.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error in snackbar if any
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (selectedAirport == null) {
                            "African Airports"
                        } else {
                            selectedAirport?.name ?: "Airport Details"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedAirport != null) {
                                viewModel.clearSelectedAirport()
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedAirport != null) {
                // Show airport details
                AirportDetails(
                    airport = selectedAirport,
                    isLoading = isLoading
                )
            } else {
                // Show airports list
                AirportsList(
                    airports = airports,
                    isLoading = isLoading,
                    onAirportClick = { airport ->
                        viewModel.selectAirport(airport.id)
                    },
                    onSearchQueryChange = viewModel::setSearchQuery,
                    onShowFilters = { showFilters = true },
                    searchQuery = searchQuery
                )
                
                // Show filters dialog if requested
                if (showFilters) {
                    AirportFiltersDialog(
                        regions = viewModel.availableRegions,
                        countries = availableCountries,
                        selectedRegion = selectedRegion,
                        selectedCountry = selectedCountry,
                        onRegionSelected = viewModel::setSelectedRegion,
                        onCountrySelected = viewModel::setSelectedCountry,
                        onDismiss = { showFilters = false },
                        onApply = { showFilters = false },
                        onReset = {
                            viewModel.setSelectedRegion("All")
                            viewModel.setSelectedCountry("All")
                        }
                    )
                }
            }
        }
    }
}
