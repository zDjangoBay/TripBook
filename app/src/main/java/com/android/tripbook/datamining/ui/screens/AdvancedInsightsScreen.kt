package com.android.tripbook.datamining.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.android.tripbook.datamining.ui.components.AdvancedInsightsDashboard
import com.android.tripbook.datamining.ui.viewmodel.DataMiningViewModel

/**
 * Screen for displaying advanced data mining insights with interactive visualizations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedInsightsScreen(
    viewModel: DataMiningViewModel,
    onNavigateBack: () -> Unit
) {
    val seasonalChartData by viewModel.seasonalChartData.collectAsState()
    val destinationPopularityData by viewModel.destinationPopularityData.collectAsState()
    val budgetDistributionData by viewModel.budgetDistributionData.collectAsState()
    val travelStylePreferencesData by viewModel.travelStylePreferencesData.collectAsState()
    val destinationHeatMapData by viewModel.destinationHeatMapData.collectAsState()
    val destinationComparisonData by viewModel.destinationComparisonData.collectAsState()
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error message if any
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
                        text = "Advanced Travel Insights",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (
                seasonalChartData != null &&
                destinationPopularityData != null &&
                budgetDistributionData != null &&
                travelStylePreferencesData != null &&
                destinationHeatMapData != null &&
                destinationComparisonData != null
            ) {
                // All data is available, show the dashboard
                AdvancedInsightsDashboard(
                    seasonalData = seasonalChartData!!,
                    destinationPopularity = destinationPopularityData!!,
                    budgetDistribution = budgetDistributionData!!,
                    travelStylePreferences = travelStylePreferencesData!!,
                    destinationHeatMap = destinationHeatMapData!!,
                    destinationComparison = destinationComparisonData!!
                )
            } else {
                // Some data is missing
                Text(
                    text = "Unable to load visualization data",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
