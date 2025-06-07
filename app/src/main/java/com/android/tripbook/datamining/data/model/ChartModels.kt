package com.android.tripbook.datamining.data.model

import androidx.compose.ui.graphics.Path

/**
 * Model representing a data point for a chart
 */
data class ChartDataPoint(
    val label: String,
    val value: Float,
    val metadata: String = "" // Additional information about this data point
)

/**
 * Model representing a complete chart dataset
 */
data class ChartDataSet(
    val title: String,
    val description: String = "",
    val dataPoints: List<ChartDataPoint>,
    val maxValue: Float = dataPoints.maxOfOrNull { it.value } ?: 0f,
    val minValue: Float = dataPoints.minOfOrNull { it.value } ?: 0f
)

/**
 * Model representing a data point for a heat map
 */
data class HeatMapDataPoint(
    val label: String,
    val x: Float, // Normalized position (0-1) on the x-axis
    val y: Float, // Normalized position (0-1) on the y-axis
    val intensity: Float, // Value representing the intensity at this point
    val metadata: String = "" // Additional information about this location
)

/**
 * Model representing a complete heat map dataset
 */
data class HeatMapDataSet(
    val title: String,
    val description: String = "",
    val dataPoints: List<HeatMapDataPoint>,
    val mapOutline: Path? = null, // Optional path for drawing a map outline
    val maxIntensity: Float = dataPoints.maxOfOrNull { it.intensity } ?: 0f,
    val minIntensity: Float = dataPoints.minOfOrNull { it.intensity } ?: 0f
)

/**
 * Model representing a single data series for a radar chart
 */
data class RadarChartData(
    val label: String,
    val values: List<Float> // Values for each axis
)

/**
 * Model representing a complete radar chart dataset
 */
data class RadarChartDataSet(
    val title: String,
    val description: String = "",
    val axes: List<String>, // Labels for each axis
    val data: List<RadarChartData>, // Multiple data series to compare
    val maxValue: Float = data.flatMap { it.values }.maxOrNull() ?: 0f,
    val minValue: Float = data.flatMap { it.values }.minOrNull() ?: 0f
)

/**
 * Model representing a comparison between two destinations
 */
data class DestinationComparison(
    val destination1: String,
    val destination2: String,
    val categories: List<String>, // Categories being compared
    val values1: List<Float>, // Values for destination 1
    val values2: List<Float> // Values for destination 2
)

/**
 * Model representing a travel pattern correlation
 */
data class TravelPatternCorrelation(
    val pattern1: String,
    val pattern2: String,
    val correlationStrength: Float, // -1 to 1 value indicating correlation strength
    val confidence: Float, // 0 to 1 value indicating confidence in the correlation
    val description: String = "" // Description of the correlation
)

/**
 * Model representing a budget distribution for a destination
 */
data class BudgetDistribution(
    val destination: String,
    val categories: List<String>, // Budget categories (accommodation, food, transportation, etc.)
    val values: List<Float>, // Amount spent in each category
    val currency: String = "USD"
)

/**
 * Model representing seasonal popularity data for a calendar heat map
 */
data class SeasonalPopularity(
    val destination: String,
    val monthlyValues: List<Float>, // 12 values representing popularity for each month
    val peakSeason: List<Int>, // Indices of months in peak season
    val shoulderSeason: List<Int>, // Indices of months in shoulder season
    val offSeason: List<Int> // Indices of months in off season
)
