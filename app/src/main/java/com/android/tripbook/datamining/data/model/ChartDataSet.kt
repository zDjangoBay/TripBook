package com.android.tripbook.datamining.data.model

/**
 * Model class for chart data sets
 */
data class ChartDataSet(
    val title: String,
    val description: String,
    val dataPoints: List<ChartDataPoint>
) {
    val maxValue: Float
        get() = dataPoints.maxOfOrNull { it.value } ?: 0f
}
