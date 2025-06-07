package com.android.tripbook.datamining.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.datamining.ui.components.DataMiningTopAppBar
import com.android.tripbook.datamining.ui.components.PersonalizedInsightsSection
import com.android.tripbook.datamining.ui.components.SeasonalTrendsChart
import com.android.tripbook.datamining.ui.components.TravelRecommendationsSection
import com.android.tripbook.datamining.ui.components.TrendingDestinationsSection
import com.android.tripbook.datamining.ui.viewmodel.DataMiningViewModel

/**
 * Main screen for the Data Mining feature
 */
@Composable
fun DataMiningScreen(
    viewModel: DataMiningViewModel,
    onNavigateBack: () -> Unit
) {
    val trendingDestinations by viewModel.trendingDestinations.collectAsState()
    val userTravelPatterns by viewModel.userTravelPatterns.collectAsState()
    val globalTravelPatterns by viewModel.globalTravelPatterns.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val seasonalChartData by viewModel.seasonalChartData.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error in snackbar if any
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        topBar = { 
            DataMiningTopAppBar(
                title = "Travel Insights",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && trendingDestinations.isEmpty()) {
                // Show loading indicator only during initial load
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = "Discover Travel Insights",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Personalized analytics to enhance your travel experience",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Personalized recommendations section
                    TravelRecommendationsSection(
                        recommendations = recommendations,
                        onRecommendationClick = { /* Handle recommendation click */ }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Trending destinations section
                    TrendingDestinationsSection(
                        destinations = trendingDestinations,
                        onDestinationClick = { /* Handle destination click */ }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Seasonal trends chart
                    seasonalChartData?.let { chartData ->
                        Text(
                            text = "Seasonal Travel Trends",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = chartData.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        SeasonalTrendsChart(
                            chartData = chartData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(vertical = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    // Personalized insights section
                    PersonalizedInsightsSection(
                        travelPatterns = userTravelPatterns,
                        userPreferences = userPreferences
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
