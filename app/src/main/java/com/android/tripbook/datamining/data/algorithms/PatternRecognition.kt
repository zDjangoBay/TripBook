package com.android.tripbook.datamining.data.algorithms

import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.TravelPattern
import com.android.tripbook.datamining.data.database.entities.UserPreference
import com.android.tripbook.datamining.data.model.TravelPatternCorrelation
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Advanced pattern recognition algorithms for travel data mining
 */
object PatternRecognition {

    /**
     * Performs time series analysis to detect seasonal patterns in destination popularity
     * 
     * @param destinations List of destinations with visit data
     * @param timeWindow Number of months to analyze
     * @return List of detected seasonal patterns
     */
    fun detectSeasonalPatterns(
        destinations: List<Destination>,
        timeWindow: Int = 24
    ): List<TravelPattern> {
        val patterns = mutableListOf<TravelPattern>()
        
        // Group destinations by region
        val regionDestinations = destinations.groupBy { it.region }
        
        regionDestinations.forEach { (region, dests) ->
            // Analyze monthly patterns for each region
            val monthlyData = analyzeMonthlyVisits(dests)
            
            // Find peak months (months with visits > average + standard deviation)
            val avgVisits = monthlyData.values.average()
            val stdDev = calculateStandardDeviation(monthlyData.values.toList(), avgVisits)
            val peakMonths = monthlyData.filter { it.value > avgVisits + stdDev }.keys.toList()
            
            // Find shoulder months (months with visits > average but < average + standard deviation)
            val shoulderMonths = monthlyData.filter { 
                it.value > avgVisits && it.value <= avgVisits + stdDev 
            }.keys.toList()
            
            // Create pattern data
            val patternData = JSONObject().apply {
                put("peak_months", JSONArray(peakMonths))
                put("shoulder_months", JSONArray(shoulderMonths))
                put("avg_visits", avgVisits)
                put("std_dev", stdDev)
                put("monthly_data", JSONObject().apply {
                    monthlyData.forEach { (month, visits) ->
                        put(month, visits)
                    }
                })
            }
            
            // Create travel pattern
            patterns.add(
                TravelPattern(
                    userId = "global", // Global pattern
                    patternType = "seasonal",
                    patternName = "Seasonal Pattern for $region",
                    patternValue = stdDev.toFloat() / avgVisits.toFloat(), // Seasonality strength
                    patternData = patternData.toString(),
                    startDate = Date(),
                    endDate = Date(System.currentTimeMillis() + (timeWindow * 30L * 24 * 60 * 60 * 1000)),
                    confidence = calculateConfidence(dests.size, stdDev / avgVisits),
                    sampleSize = dests.size
                )
            )
        }
        
        return patterns
    }
    
    /**
     * Discovers associations between destinations that are frequently visited together
     * 
     * @param userTrips Map of user IDs to lists of destinations they've visited
     * @param minSupport Minimum support threshold for association rules
     * @param minConfidence Minimum confidence threshold for association rules
     * @return List of destination associations
     */
    fun discoverDestinationAssociations(
        userTrips: Map<String, List<Destination>>,
        minSupport: Double = 0.1,
        minConfidence: Double = 0.5
    ): List<TravelPatternCorrelation> {
        val associations = mutableListOf<TravelPatternCorrelation>()
        val totalUsers = userTrips.size
        
        // Count individual destination frequencies
        val destinationCounts = mutableMapOf<String, Int>()
        userTrips.values.forEach { destinations ->
            destinations.forEach { destination ->
                val destName = destination.name
                destinationCounts[destName] = destinationCounts.getOrDefault(destName, 0) + 1
            }
        }
        
        // Count co-occurrences of destination pairs
        val coOccurrences = mutableMapOf<Pair<String, String>, Int>()
        userTrips.values.forEach { destinations ->
            val destNames = destinations.map { it.name }.distinct()
            
            // For each pair of destinations
            for (i in destNames.indices) {
                for (j in i + 1 until destNames.size) {
                    val pair = if (destNames[i] < destNames[j]) {
                        Pair(destNames[i], destNames[j])
                    } else {
                        Pair(destNames[j], destNames[i])
                    }
                    
                    coOccurrences[pair] = coOccurrences.getOrDefault(pair, 0) + 1
                }
            }
        }
        
        // Calculate association rules
        coOccurrences.forEach { (pair, count) ->
            val support = count.toDouble() / totalUsers
            
            if (support >= minSupport) {
                val (dest1, dest2) = pair
                val count1 = destinationCounts[dest1] ?: 0
                val count2 = destinationCounts[dest2] ?: 0
                
                // Calculate confidence in both directions
                val confidence1to2 = count.toDouble() / count1
                val confidence2to1 = count.toDouble() / count2
                
                // Calculate lift (correlation strength)
                val lift = (count.toDouble() * totalUsers) / (count1 * count2)
                
                // If confidence meets threshold in either direction, add association
                if (confidence1to2 >= minConfidence) {
                    associations.add(
                        TravelPatternCorrelation(
                            pattern1 = dest1,
                            pattern2 = dest2,
                            correlationStrength = lift.toFloat(),
                            confidence = confidence1to2.toFloat(),
                            description = "Travelers who visit $dest1 often also visit $dest2"
                        )
                    )
                }
                
                if (confidence2to1 >= minConfidence) {
                    associations.add(
                        TravelPatternCorrelation(
                            pattern1 = dest2,
                            pattern2 = dest1,
                            correlationStrength = lift.toFloat(),
                            confidence = confidence2to1.toFloat(),
                            description = "Travelers who visit $dest2 often also visit $dest1"
                        )
                    )
                }
            }
        }
        
        return associations.sortedByDescending { it.correlationStrength }
    }
    
    /**
     * Analyzes monthly visit patterns for a list of destinations
     */
    private fun analyzeMonthlyVisits(destinations: List<Destination>): Map<String, Double> {
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        
        val monthlyVisits = months.associateWith { 0.0 }.toMutableMap()
        
        // Parse seasonality data from destinations
        destinations.forEach { destination ->
            destination.seasonalityData?.let { seasonalityData ->
                try {
                    val jsonData = JSONObject(seasonalityData)
                    months.forEach { month ->
                        if (jsonData.has(month)) {
                            val currentValue = monthlyVisits[month] ?: 0.0
                            monthlyVisits[month] = currentValue + jsonData.getDouble(month)
                        }
                    }
                } catch (e: Exception) {
                    // Skip invalid data
                }
            }
        }
        
        return monthlyVisits
    }
    
    /**
     * Calculates standard deviation for a list of values
     */
    private fun calculateStandardDeviation(values: List<Double>, mean: Double): Double {
        if (values.isEmpty()) return 0.0
        
        val variance = values.sumOf { (it - mean) * (it - mean) } / values.size
        return sqrt(variance)
    }
    
    /**
     * Calculates confidence in a pattern based on sample size and pattern strength
     */
    private fun calculateConfidence(sampleSize: Int, patternStrength: Double): Float {
        // Confidence increases with sample size and pattern strength
        val sampleFactor = min(1.0, sampleSize / 20.0) // Max out at 20 samples
        val strengthFactor = min(1.0, patternStrength * 2) // Stronger patterns have higher confidence
        
        return (0.5 + (sampleFactor * 0.3) + (strengthFactor * 0.2)).toFloat()
    }
}
