package com.android.tripbook.datamining.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.tripbook.datamining.data.model.ChartDataPoint
import com.android.tripbook.datamining.data.model.ChartDataSet
import com.android.tripbook.datamining.data.model.DestinationInsight
import com.android.tripbook.datamining.data.model.TravelInsight
import com.android.tripbook.datamining.data.model.TravelRecommendation
import com.android.tripbook.datamining.data.model.UserInsight
import com.android.tripbook.datamining.data.repository.DataMiningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ViewModel for the data mining feature
 */
class DataMiningViewModel(private val repository: DataMiningRepository) : ViewModel() {

    // Current user ID - in a real app, this would come from authentication
    private val currentUserId = "user123"

    // UI state for trending destinations
    private val _trendingDestinations = MutableStateFlow<List<DestinationInsight>>(emptyList())
    val trendingDestinations: StateFlow<List<DestinationInsight>> = _trendingDestinations.asStateFlow()

    // UI state for user travel patterns
    private val _userTravelPatterns = MutableStateFlow<List<TravelInsight>>(emptyList())
    val userTravelPatterns: StateFlow<List<TravelInsight>> = _userTravelPatterns.asStateFlow()

    // UI state for global travel patterns
    private val _globalTravelPatterns = MutableStateFlow<List<TravelInsight>>(emptyList())
    val globalTravelPatterns: StateFlow<List<TravelInsight>> = _globalTravelPatterns.asStateFlow()

    // UI state for user preferences
    private val _userPreferences = MutableStateFlow<List<UserInsight>>(emptyList())
    val userPreferences: StateFlow<List<UserInsight>> = _userPreferences.asStateFlow()

    // UI state for seasonal chart data
    private val _seasonalChartData = MutableStateFlow<ChartDataSet?>(null)
    val seasonalChartData: StateFlow<ChartDataSet?> = _seasonalChartData.asStateFlow()

    // UI state for personalized recommendations
    private val _recommendations = MutableStateFlow<List<TravelRecommendation>>(emptyList())
    val recommendations: StateFlow<List<TravelRecommendation>> = _recommendations.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Populate sample data for demonstration
                repository.populateSampleData()

                // Load trending destinations
                repository.getTrendingDestinations(8).collectLatest { destinations ->
                    _trendingDestinations.value = destinations
                }
            } catch (e: Exception) {
                _error.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }

        // Load user travel patterns
        viewModelScope.launch {
            try {
                repository.getUserTravelPatterns(currentUserId).collectLatest { patterns ->
                    _userTravelPatterns.value = patterns
                }
            } catch (e: Exception) {
                _error.value = "Failed to load user patterns: ${e.message}"
            }
        }

        // Load global travel patterns
        viewModelScope.launch {
            try {
                repository.getGlobalTravelPatterns().collectLatest { patterns ->
                    _globalTravelPatterns.value = patterns

                    // Generate seasonal chart data from the first seasonal pattern
                    patterns.find { it.type == "seasonal" }?.let { pattern ->
                        generateSeasonalChartData(pattern)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load global patterns: ${e.message}"
            }
        }

        // Load user preferences
        viewModelScope.launch {
            try {
                repository.getUserPreferenceInsights(currentUserId).collectLatest { preferences ->
                    _userPreferences.value = preferences
                }
            } catch (e: Exception) {
                _error.value = "Failed to load user preferences: ${e.message}"
            }
        }

        // Generate recommendations based on user preferences and trending destinations
        viewModelScope.launch {
            try {
                // This would normally be a more complex algorithm
                // For demo purposes, we'll create some sample recommendations
                val recommendations = listOf(
                    TravelRecommendation(
                        id = 1,
                        title = "Safari Adventure",
                        description = "Based on your interest in wildlife and adventure activities, we recommend a safari trip to Serengeti National Park.",
                        destinationId = 2,
                        destinationName = "Serengeti National Park",
                        imageUrl = "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                        confidence = 0.92f,
                        tags = listOf("safari", "wildlife", "photography"),
                        relevanceScore = 0.95f
                    ),
                    TravelRecommendation(
                        id = 2,
                        title = "Beach Relaxation",
                        description = "Your preference for beach destinations suggests you would enjoy the pristine beaches of Zanzibar.",
                        destinationId = 1,
                        destinationName = "Zanzibar",
                        imageUrl = "https://images.unsplash.com/photo-1586500036706-41963de24d8c",
                        confidence = 0.88f,
                        tags = listOf("beach", "relaxation", "island"),
                        relevanceScore = 0.85f
                    ),
                    TravelRecommendation(
                        id = 3,
                        title = "Cultural Experience",
                        description = "Explore the rich cultural heritage of Marrakech with its vibrant markets and historic architecture.",
                        destinationId = 4,
                        destinationName = "Marrakech",
                        imageUrl = "https://images.unsplash.com/photo-1597212618440-806262de4f6b",
                        confidence = 0.75f,
                        tags = listOf("culture", "history", "market"),
                        relevanceScore = 0.70f
                    )
                )
                _recommendations.value = recommendations
            } catch (e: Exception) {
                _error.value = "Failed to generate recommendations: ${e.message}"
            }
        }
    }

    private fun generateSeasonalChartData(pattern: TravelInsight) {
        try {
            val patternData = JSONObject(pattern.data)
            val months = patternData.getJSONArray("months")
            val destinations = patternData.getJSONArray("destinations")

            // Create a description from the data
            val description = "Peak season for ${destinations.getString(0)} and " +
                    "${if (destinations.length() > 1) destinations.getString(1) else "other destinations"} " +
                    "during ${months.getString(0)}, ${months.getString(1)}, and ${months.getString(2)}."

            // Create chart data points for months of the year
            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val dataPoints = monthNames.map { month ->
                // Higher values for peak months mentioned in the pattern
                val value = if (monthContains(months, month)) {
                    (80..95).random().toFloat()
                } else {
                    (50..75).random().toFloat()
                }
                ChartDataPoint(month, value)
            }

            _seasonalChartData.value = ChartDataSet(
                title = pattern.name,
                description = description,
                dataPoints = dataPoints
            )
        } catch (e: Exception) {
            _error.value = "Failed to generate chart data: ${e.message}"
        }
    }

    private fun monthContains(monthsArray: JSONArray, shortMonth: String): Boolean {
        val monthMap = mapOf(
            "Jan" to "January", "Feb" to "February", "Mar" to "March",
            "Apr" to "April", "May" to "May", "Jun" to "June",
            "Jul" to "July", "Aug" to "August", "Sep" to "September",
            "Oct" to "October", "Nov" to "November", "Dec" to "December"
        )

        val fullMonth = monthMap[shortMonth] ?: return false

        for (i in 0 until monthsArray.length()) {
            if (monthsArray.getString(i).equals(fullMonth, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    /**
     * Factory for creating DataMiningViewModel with dependencies
     */
    class Factory(private val repository: DataMiningRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DataMiningViewModel::class.java)) {
                return DataMiningViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
