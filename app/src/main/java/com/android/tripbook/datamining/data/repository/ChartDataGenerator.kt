package com.android.tripbook.datamining.data.repository

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.android.tripbook.datamining.data.model.ChartDataPoint
import com.android.tripbook.datamining.data.model.ChartDataSet
import com.android.tripbook.datamining.data.model.HeatMapDataPoint
import com.android.tripbook.datamining.data.model.HeatMapDataSet
import com.android.tripbook.datamining.data.model.RadarChartData
import com.android.tripbook.datamining.data.model.RadarChartDataSet
import kotlin.random.Random

/**
 * Utility class for generating sample chart data for demonstration purposes
 */
object ChartDataGenerator {
    
    /**
     * Generate seasonal trend data for a destination
     */
    fun generateSeasonalTrendData(destinationName: String): ChartDataSet {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        
        // Generate random seasonal pattern with higher values in certain months
        // For Cameroon, peak seasons are typically December-February (dry season) and June-August
        val dataPoints = monthNames.mapIndexed { index, month ->
            val baseValue = 50f
            val seasonalFactor = when (index) {
                // December-February (dry season)
                0, 1, 11 -> Random.nextFloat() * 40f + 40f
                // June-August (less rainy)
                5, 6, 7 -> Random.nextFloat() * 30f + 30f
                // Shoulder seasons
                2, 8, 9, 10 -> Random.nextFloat() * 20f + 20f
                // Low season (rainy season)
                else -> Random.nextFloat() * 15f + 15f
            }
            
            ChartDataPoint(month, baseValue + seasonalFactor)
        }
        
        return ChartDataSet(
            title = "Seasonal Popularity for $destinationName",
            description = "Monthly visitor trends showing peak seasons and low seasons",
            dataPoints = dataPoints
        )
    }
    
    /**
     * Generate destination popularity data
     */
    fun generateDestinationPopularityData(): ChartDataSet {
        val destinations = listOf(
            "Mount Cameroon" to 85f,
            "Kribi Beach" to 78f,
            "Waza National Park" to 72f,
            "Bafut Palace" to 65f,
            "Dja Faunal Reserve" to 58f,
            "Ekom-Nkam Waterfalls" to 52f,
            "Mefou National Park" to 48f
        )
        
        val dataPoints = destinations.map { (name, value) ->
            ChartDataPoint(name, value)
        }
        
        return ChartDataSet(
            title = "Destination Popularity in Cameroon",
            description = "Relative popularity of top destinations based on visitor numbers",
            dataPoints = dataPoints
        )
    }
    
    /**
     * Generate budget distribution data
     */
    fun generateBudgetDistributionData(): ChartDataSet {
        val categories = listOf(
            "Accommodation" to 35f,
            "Food & Dining" to 25f,
            "Transportation" to 15f,
            "Activities" to 15f,
            "Shopping" to 7f,
            "Miscellaneous" to 3f
        )
        
        val dataPoints = categories.map { (name, value) ->
            ChartDataPoint(name, value)
        }
        
        return ChartDataSet(
            title = "Average Travel Budget Distribution",
            description = "How travelers typically allocate their budget across different categories",
            dataPoints = dataPoints
        )
    }
    
    /**
     * Generate travel style preference data
     */
    fun generateTravelStylePreferenceData(): ChartDataSet {
        val styles = listOf(
            "Adventure" to 32f,
            "Beach" to 28f,
            "Cultural" to 22f,
            "Eco-tourism" to 18f,
            "Safari" to 15f,
            "Luxury" to 10f,
            "Budget" to 8f
        )
        
        val dataPoints = styles.map { (name, value) ->
            ChartDataPoint(name, value)
        }
        
        return ChartDataSet(
            title = "Travel Style Preferences",
            description = "Distribution of preferred travel styles among users",
            dataPoints = dataPoints
        )
    }
    
    /**
     * Generate destination heat map data
     */
    fun generateDestinationHeatMapData(): HeatMapDataSet {
        // Simplified coordinates for Cameroon regions
        val locations = listOf(
            HeatMapDataPoint("Mount Cameroon", 0.3f, 0.7f, 85f, "Southwest Region"),
            HeatMapDataPoint("Kribi Beach", 0.4f, 0.8f, 78f, "South Region"),
            HeatMapDataPoint("Waza National Park", 0.7f, 0.2f, 72f, "Far North Region"),
            HeatMapDataPoint("Bafut Palace", 0.25f, 0.3f, 65f, "Northwest Region"),
            HeatMapDataPoint("Dja Faunal Reserve", 0.6f, 0.7f, 58f, "East Region"),
            HeatMapDataPoint("Ekom-Nkam Waterfalls", 0.35f, 0.6f, 52f, "Littoral Region"),
            HeatMapDataPoint("Mefou National Park", 0.5f, 0.6f, 48f, "Centre Region"),
            HeatMapDataPoint("Limbe", 0.25f, 0.75f, 45f, "Southwest Region"),
            HeatMapDataPoint("Yaoundé", 0.5f, 0.55f, 42f, "Centre Region"),
            HeatMapDataPoint("Douala", 0.4f, 0.65f, 40f, "Littoral Region"),
            HeatMapDataPoint("Korup National Park", 0.2f, 0.6f, 38f, "Southwest Region"),
            HeatMapDataPoint("Rhumsiki", 0.8f, 0.15f, 35f, "Far North Region")
        )
        
        // Create a simplified outline of Cameroon
        val mapOutline = Path().apply {
            // This is a very simplified outline of Cameroon
            moveTo(0.2f, 0.2f)  // Northwest corner
            lineTo(0.8f, 0.1f)  // Northeast corner
            lineTo(0.9f, 0.4f)  // East bulge
            lineTo(0.7f, 0.8f)  // Southeast corner
            lineTo(0.3f, 0.9f)  // Southwest corner
            lineTo(0.2f, 0.6f)  // West coast
            close()
        }
        
        return HeatMapDataSet(
            title = "Destination Popularity Heat Map",
            description = "Geographic distribution of popular destinations in Cameroon",
            dataPoints = locations,
            mapOutline = mapOutline
        )
    }
    
    /**
     * Generate destination comparison data
     */
    fun generateDestinationComparisonData(): RadarChartDataSet {
        val categories = listOf(
            "Scenery",
            "Activities",
            "Accommodation",
            "Food",
            "Accessibility",
            "Value"
        )
        
        val destinations = listOf(
            RadarChartData(
                label = "Mount Cameroon",
                values = listOf(9.5f, 8.7f, 7.2f, 8.0f, 6.5f, 8.3f)
            ),
            RadarChartData(
                label = "Kribi Beach",
                values = listOf(8.8f, 7.5f, 8.5f, 8.7f, 7.8f, 7.9f)
            ),
            RadarChartData(
                label = "Waza National Park",
                values = listOf(9.2f, 9.0f, 6.8f, 7.2f, 5.5f, 8.0f)
            )
        )
        
        return RadarChartDataSet(
            title = "Destination Comparison",
            description = "Comparing key attributes across popular destinations",
            axes = categories,
            data = destinations,
            maxValue = 10f,
            minValue = 0f
        )
    }
}
