package com.android.tripbook.datamining.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.datamining.data.model.ChartDataPoint
import com.android.tripbook.datamining.data.model.ChartDataSet
import com.android.tripbook.datamining.data.model.HeatMapDataPoint
import com.android.tripbook.datamining.data.model.HeatMapDataSet
import com.android.tripbook.datamining.data.model.RadarChartData
import com.android.tripbook.datamining.data.model.RadarChartDataSet
import com.android.tripbook.datamining.ui.components.charts.HeatMapChart
import com.android.tripbook.datamining.ui.components.charts.LineChart
import com.android.tripbook.datamining.ui.components.charts.PieChart
import com.android.tripbook.datamining.ui.components.charts.RadarChart
import com.android.tripbook.datamining.ui.components.charts.SeasonalTrendsChart

/**
 * A dashboard component that showcases various advanced data visualizations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedInsightsDashboard(
    seasonalData: ChartDataSet,
    destinationPopularity: ChartDataSet,
    budgetDistribution: ChartDataSet,
    travelStylePreferences: ChartDataSet,
    destinationHeatMap: HeatMapDataSet,
    destinationComparison: RadarChartDataSet,
    modifier: Modifier = Modifier
) {
    var selectedTimeRange by remember { mutableStateOf("Last 12 Months") }
    val timeRanges = listOf("Last 3 Months", "Last 6 Months", "Last 12 Months", "All Time")
    
    var selectedChartType by remember { mutableStateOf<ChartType>(ChartType.Line) }
    
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Dashboard header
        Text(
            text = "Travel Insights Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Discover patterns and trends in your travel data",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Time range filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Time Range:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Box {
                var expanded by remember { mutableStateOf(false) }
                
                TextButton(
                    onClick = { expanded = true }
                ) {
                    Text(selectedTimeRange)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select time range"
                    )
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    timeRanges.forEach { range ->
                        DropdownMenuItem(
                            text = { Text(range) },
                            onClick = {
                                selectedTimeRange = range
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Chart type selector
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(ChartType.values()) { chartType ->
                FilterChip(
                    selected = selectedChartType == chartType,
                    onClick = { selectedChartType = chartType },
                    label = { Text(chartType.label) },
                    leadingIcon = {
                        Icon(
                            imageVector = chartType.icon,
                            contentDescription = null,
                            modifier = Modifier.width(18.dp)
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Seasonal trends chart
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Seasonal Travel Trends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when (selectedChartType) {
                    ChartType.Line -> LineChart(
                        chartData = seasonalData,
                        modifier = Modifier.height(250.dp)
                    )
                    ChartType.Bar -> SeasonalTrendsChart(
                        chartData = seasonalData,
                        modifier = Modifier.height(250.dp)
                    )
                    else -> LineChart(
                        chartData = seasonalData,
                        modifier = Modifier.height(250.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Destination popularity chart
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Destination Popularity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when (selectedChartType) {
                    ChartType.Pie -> PieChart(
                        chartData = destinationPopularity,
                        modifier = Modifier.height(300.dp)
                    )
                    ChartType.Bar -> SeasonalTrendsChart(
                        chartData = destinationPopularity,
                        modifier = Modifier.height(250.dp)
                    )
                    else -> PieChart(
                        chartData = destinationPopularity,
                        modifier = Modifier.height(300.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Budget distribution chart
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Budget Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PieChart(
                    chartData = budgetDistribution,
                    modifier = Modifier.height(300.dp),
                    donutHolePercentage = 0.6f
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Travel style preferences chart
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Travel Style Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                RadarChart(
                    dataSet = destinationComparison,
                    modifier = Modifier.height(300.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Destination heat map
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Destination Popularity Heat Map",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                HeatMapChart(
                    dataSet = destinationHeatMap,
                    modifier = Modifier.height(300.dp)
                )
            }
        }
    }
}

enum class ChartType(val label: String, val icon: ImageVector) {
    Line("Line", Icons.Default.ShowChart),
    Bar("Bar", Icons.Default.BarChart),
    Pie("Pie", Icons.Default.PieChart),
    Donut("Donut", Icons.Default.DonutLarge),
    Map("Map", Icons.Default.Map)
}
