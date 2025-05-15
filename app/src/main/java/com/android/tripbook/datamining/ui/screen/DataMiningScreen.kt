package com.android.tripbook.datamining.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.datamining.ui.components.FilterSection
import com.android.tripbook.datamining.ui.components.PersonalizedInsightsSection
import com.android.tripbook.datamining.ui.components.PredictiveRecommendationsSection
import com.android.tripbook.datamining.ui.components.SeasonalTrendsChart
import com.android.tripbook.datamining.ui.components.TravelRecommendationsSection
import com.android.tripbook.datamining.ui.components.TrendingDestinationsSection
import com.android.tripbook.datamining.ui.viewmodel.DataMiningViewModel

/**
 * Main screen for the data mining feature
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataMiningScreen(
    viewModel: DataMiningViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdvancedInsights: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val trendingDestinations by viewModel.trendingDestinations.collectAsState()
    val userTravelPatterns by viewModel.userTravelPatterns.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val seasonalChartData by viewModel.seasonalChartData.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val predictiveRecommendations by viewModel.predictiveRecommendations.collectAsState()

    // Filter states
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val selectedBudgetRange by viewModel.selectedBudgetRange.collectAsState()
    val selectedTravelStyle by viewModel.selectedTravelStyle.collectAsState()

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
                title = { Text("Travel Insights") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Filters
                    FilterSection(
                        regions = viewModel.availableRegions,
                        selectedRegion = selectedRegion,
                        onRegionSelected = viewModel::updateRegionFilter,
                        budgetRanges = viewModel.availableBudgetRanges,
                        selectedBudgetRange = selectedBudgetRange,
                        onBudgetRangeSelected = viewModel::updateBudgetRangeFilter,
                        travelStyles = viewModel.availableTravelStyles,
                        selectedTravelStyle = selectedTravelStyle,
                        onTravelStyleSelected = viewModel::updateTravelStyleFilter
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Predictive recommendations
                    if (predictiveRecommendations.isNotEmpty()) {
                        PredictiveRecommendationsSection(
                            recommendations = predictiveRecommendations
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Personalized recommendations
                    TravelRecommendationsSection(
                        recommendations = recommendations
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Trending destinations
                    TrendingDestinationsSection(
                        destinations = trendingDestinations
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Seasonal trends chart
                    seasonalChartData?.let { chartData ->
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Seasonal Travel Trends",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = chartData.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            SeasonalTrendsChart(
                                chartData = chartData,
                                modifier = Modifier.height(200.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // User insights
                    PersonalizedInsightsSection(
                        travelPatterns = userTravelPatterns,
                        userPreferences = userPreferences
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Advanced insights button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Button(
                            onClick = onNavigateToAdvancedInsights,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text("View Advanced Insights Dashboard")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
