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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.min

/**
 * ViewModel for the data mining feature
 */
class DataMiningViewModel(private val repository: DataMiningRepository) : ViewModel() {

    // Current user ID - in a real app, this would come from authentication
    private val currentUserId = "user123"

    // Available regions for filtering
    val availableRegions = listOf("All", "East Africa", "North Africa", "West Africa", "Southern Africa", "Central Africa")

    // Available budget ranges for filtering
    val availableBudgetRanges = listOf("All", "Budget", "Mid-range", "Luxury")

    // Available travel styles for filtering
    val availableTravelStyles = listOf("All", "Adventure", "Cultural", "Beach", "Safari", "Urban", "Eco-tourism")

    // Filter states
    private val _selectedRegion = MutableStateFlow("All")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _selectedBudgetRange = MutableStateFlow("All")
    val selectedBudgetRange: StateFlow<String> = _selectedBudgetRange.asStateFlow()

    private val _selectedTravelStyle = MutableStateFlow("All")
    val selectedTravelStyle: StateFlow<String> = _selectedTravelStyle.asStateFlow()

    // UI state for trending destinations (unfiltered)
    private val _allTrendingDestinations = MutableStateFlow<List<DestinationInsight>>(emptyList())

    // UI state for trending destinations (filtered)
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

    // UI state for personalized recommendations (unfiltered)
    private val _allRecommendations = MutableStateFlow<List<TravelRecommendation>>(emptyList())

    // UI state for personalized recommendations (filtered)
    private val _recommendations = MutableStateFlow<List<TravelRecommendation>>(emptyList())
    val recommendations: StateFlow<List<TravelRecommendation>> = _recommendations.asStateFlow()

    // UI state for predictive recommendations based on user behavior
    private val _predictiveRecommendations = MutableStateFlow<List<TravelRecommendation>>(emptyList())
    val predictiveRecommendations: StateFlow<List<TravelRecommendation>> = _predictiveRecommendations.asStateFlow()

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
                repository.getTrendingDestinations(12).collectLatest { destinations ->
                    _allTrendingDestinations.value = destinations
                    applyFilters() // Apply filters to update the filtered list
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
                    generatePredictiveRecommendations() // Generate predictive recommendations based on preferences
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
                        relevanceScore = 0.95f,
                        region = "East Africa",
                        budgetCategory = "Luxury",
                        travelStyle = "Safari"
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
                        relevanceScore = 0.85f,
                        region = "East Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Beach"
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
                        relevanceScore = 0.70f,
                        region = "North Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Cultural"
                    ),
                    TravelRecommendation(
                        id = 4,
                        title = "Desert Adventure",
                        description = "Experience the stunning landscapes of the Sahara Desert with a guided tour in Morocco.",
                        destinationId = 5,
                        destinationName = "Sahara Desert",
                        imageUrl = "https://images.unsplash.com/photo-1509042239860-f0b825a6dfcc",
                        confidence = 0.82f,
                        tags = listOf("desert", "adventure", "landscape"),
                        relevanceScore = 0.78f,
                        region = "North Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Adventure"
                    ),
                    TravelRecommendation(
                        id = 5,
                        title = "Wildlife Expedition",
                        description = "Discover the incredible wildlife of Kruger National Park with expert guides.",
                        destinationId = 6,
                        destinationName = "Kruger National Park",
                        imageUrl = "https://images.unsplash.com/photo-1551009175-15bdf9dcb580",
                        confidence = 0.89f,
                        tags = listOf("wildlife", "safari", "nature"),
                        relevanceScore = 0.84f,
                        region = "Southern Africa",
                        budgetCategory = "Luxury",
                        travelStyle = "Safari"
                    ),
                    TravelRecommendation(
                        id = 6,
                        title = "Island Paradise",
                        description = "Relax on the beautiful beaches of Seychelles with crystal clear waters and white sand.",
                        destinationId = 7,
                        destinationName = "Seychelles",
                        imageUrl = "https://images.unsplash.com/photo-1573843981267-be1999ff37cd",
                        confidence = 0.91f,
                        tags = listOf("beach", "island", "luxury"),
                        relevanceScore = 0.88f,
                        region = "East Africa",
                        budgetCategory = "Luxury",
                        travelStyle = "Beach"
                    ),
                    TravelRecommendation(
                        id = 7,
                        title = "Budget Safari",
                        description = "Experience an affordable safari adventure in Tanzania's Tarangire National Park.",
                        destinationId = 8,
                        destinationName = "Tarangire National Park",
                        imageUrl = "https://images.unsplash.com/photo-1547970810-dc1eac37d174",
                        confidence = 0.76f,
                        tags = listOf("safari", "budget", "wildlife"),
                        relevanceScore = 0.72f,
                        region = "East Africa",
                        budgetCategory = "Budget",
                        travelStyle = "Safari"
                    )
                )
                _allRecommendations.value = recommendations
                applyFilters() // Apply filters to update the filtered list
            } catch (e: Exception) {
                _error.value = "Failed to generate recommendations: ${e.message}"
            }
        }

        // Set up filter observers
        viewModelScope.launch {
            combine(
                _selectedRegion,
                _selectedBudgetRange,
                _selectedTravelStyle
            ) { _, _, _ ->
                // When any filter changes, apply all filters
                applyFilters()
            }.collect {}
        }
    }

    /**
     * Apply filters to destinations and recommendations
     */
    private fun applyFilters() {
        // Filter destinations
        val filteredDestinations = _allTrendingDestinations.value.filter { destination ->
            matchesFilter(
                region = destination.region,
                budgetCategory = destination.budgetCategory,
                travelStyle = destination.category
            )
        }
        _trendingDestinations.value = filteredDestinations

        // Filter recommendations
        val filteredRecommendations = _allRecommendations.value.filter { recommendation ->
            matchesFilter(
                region = recommendation.region,
                budgetCategory = recommendation.budgetCategory,
                travelStyle = recommendation.travelStyle
            )
        }
        _recommendations.value = filteredRecommendations
    }

    /**
     * Check if an item matches the current filters
     */
    private fun matchesFilter(region: String, budgetCategory: String, travelStyle: String): Boolean {
        val matchesRegion = _selectedRegion.value == "All" || region == _selectedRegion.value
        val matchesBudget = _selectedBudgetRange.value == "All" || budgetCategory == _selectedBudgetRange.value
        val matchesStyle = _selectedTravelStyle.value == "All" || travelStyle == _selectedTravelStyle.value
        return matchesRegion && matchesBudget && matchesStyle
    }

    /**
     * Update the selected region filter
     */
    fun updateRegionFilter(region: String) {
        _selectedRegion.value = region
    }

    /**
     * Update the selected budget range filter
     */
    fun updateBudgetRangeFilter(budgetRange: String) {
        _selectedBudgetRange.value = budgetRange
    }

    /**
     * Update the selected travel style filter
     */
    fun updateTravelStyleFilter(travelStyle: String) {
        _selectedTravelStyle.value = travelStyle
    }

    /**
     * Generate predictive recommendations based on user behavior and preferences
     */
    private fun generatePredictiveRecommendations() {
        try {
            // Get user preferences
            val preferences = _userPreferences.value

            // Get current season
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            val currentSeason = when (currentMonth) {
                Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> "Winter"
                Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> "Spring"
                Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> "Summer"
                else -> "Fall"
            }

            // Generate predictive recommendations based on preferences and season
            val predictiveRecs = mutableListOf<TravelRecommendation>()

            // Add season-specific recommendations
            when (currentSeason) {
                "Winter" -> {
                    predictiveRecs.add(
                        TravelRecommendation(
                            id = 101,
                            title = "Winter Safari Special",
                            description = "Winter is the perfect time for wildlife viewing in East Africa with fewer crowds.",
                            destinationId = 10,
                            destinationName = "Masai Mara",
                            imageUrl = "https://images.unsplash.com/photo-1547970810-dc1eac37d174",
                            confidence = 0.94f,
                            tags = listOf("safari", "winter", "wildlife"),
                            relevanceScore = 0.91f,
                            region = "East Africa",
                            budgetCategory = "Mid-range",
                            travelStyle = "Safari",
                            isPredictive = true
                        )
                    )
                }
                "Spring" -> {
                    predictiveRecs.add(
                        TravelRecommendation(
                            id = 102,
                            title = "Spring Cultural Festival",
                            description = "Experience the vibrant spring festivals in Morocco with traditional music and food.",
                            destinationId = 11,
                            destinationName = "Fes",
                            imageUrl = "https://images.unsplash.com/photo-1548019979-e49b7c0a0652",
                            confidence = 0.89f,
                            tags = listOf("culture", "festival", "spring"),
                            relevanceScore = 0.86f,
                            region = "North Africa",
                            budgetCategory = "Budget",
                            travelStyle = "Cultural",
                            isPredictive = true
                        )
                    )
                }
                "Summer" -> {
                    predictiveRecs.add(
                        TravelRecommendation(
                            id = 103,
                            title = "Summer Beach Getaway",
                            description = "Enjoy the perfect summer weather at the beaches of Mozambique.",
                            destinationId = 12,
                            destinationName = "Bazaruto Archipelago",
                            imageUrl = "https://images.unsplash.com/photo-1573843981267-be1999ff37cd",
                            confidence = 0.96f,
                            tags = listOf("beach", "summer", "island"),
                            relevanceScore = 0.93f,
                            region = "Southern Africa",
                            budgetCategory = "Luxury",
                            travelStyle = "Beach",
                            isPredictive = true
                        )
                    )
                }
                "Fall" -> {
                    predictiveRecs.add(
                        TravelRecommendation(
                            id = 104,
                            title = "Fall Adventure Trek",
                            description = "The perfect time to trek in the Atlas Mountains with mild temperatures and beautiful scenery.",
                            destinationId = 13,
                            destinationName = "Atlas Mountains",
                            imageUrl = "https://images.unsplash.com/photo-1489493585363-d69421e0edd3",
                            confidence = 0.88f,
                            tags = listOf("trekking", "mountains", "fall"),
                            relevanceScore = 0.85f,
                            region = "North Africa",
                            budgetCategory = "Mid-range",
                            travelStyle = "Adventure",
                            isPredictive = true
                        )
                    )
                }
            }

            // Add recommendations based on user preferences
            preferences.forEach { preference ->
                when (preference.category) {
                    "Wildlife" -> {
                        predictiveRecs.add(
                            TravelRecommendation(
                                id = 105,
                                title = "Exclusive Wildlife Photography Tour",
                                description = "Based on your interest in wildlife, we've found a special photography tour in Botswana.",
                                destinationId = 14,
                                destinationName = "Okavango Delta",
                                imageUrl = "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                                confidence = 0.92f,
                                tags = listOf("wildlife", "photography", "exclusive"),
                                relevanceScore = 0.90f,
                                region = "Southern Africa",
                                budgetCategory = "Luxury",
                                travelStyle = "Safari",
                                isPredictive = true
                            )
                        )
                    }
                    "Beach" -> {
                        predictiveRecs.add(
                            TravelRecommendation(
                                id = 106,
                                title = "Hidden Beach Paradise",
                                description = "Discover this secluded beach in Madagascar that matches your preference for quiet beach destinations.",
                                destinationId = 15,
                                destinationName = "Nosy Be",
                                imageUrl = "https://images.unsplash.com/photo-1586500036706-41963de24d8c",
                                confidence = 0.90f,
                                tags = listOf("beach", "secluded", "paradise"),
                                relevanceScore = 0.87f,
                                region = "East Africa",
                                budgetCategory = "Mid-range",
                                travelStyle = "Beach",
                                isPredictive = true
                            )
                        )
                    }
                    "Culture" -> {
                        predictiveRecs.add(
                            TravelRecommendation(
                                id = 107,
                                title = "Authentic Cultural Immersion",
                                description = "Live with a local family in Ethiopia and experience authentic cultural traditions.",
                                destinationId = 16,
                                destinationName = "Lalibela",
                                imageUrl = "https://images.unsplash.com/photo-1523805009345-7448845a9e53",
                                confidence = 0.87f,
                                tags = listOf("culture", "immersion", "authentic"),
                                relevanceScore = 0.84f,
                                region = "East Africa",
                                budgetCategory = "Budget",
                                travelStyle = "Cultural",
                                isPredictive = true
                            )
                        )
                    }
                }
            }

            // Take the top 3 recommendations based on relevance score
            _predictiveRecommendations.value = predictiveRecs
                .sortedByDescending { it.relevanceScore }
                .take(min(3, predictiveRecs.size))
        } catch (e: Exception) {
            _error.value = "Failed to generate predictive recommendations: ${e.message}"
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
