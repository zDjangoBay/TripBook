package com.android.tripbook.datamining.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.tripbook.datamining.data.feedback.RecommendationFeedback
import com.android.tripbook.datamining.data.feedback.RecommendationFeedback.FeedbackType
import com.android.tripbook.datamining.data.model.ChartDataPoint
import com.android.tripbook.datamining.data.model.ChartDataSet
import com.android.tripbook.datamining.data.model.DestinationInsight
import com.android.tripbook.datamining.data.model.HeatMapDataSet
import com.android.tripbook.datamining.data.model.RadarChartDataSet
import com.android.tripbook.datamining.data.model.TravelInsight
import com.android.tripbook.datamining.data.model.TravelRecommendation
import com.android.tripbook.datamining.data.model.UserInsight
import com.android.tripbook.datamining.data.realtime.DynamicRecommender
import com.android.tripbook.datamining.data.realtime.TrendAnalyzer
import com.android.tripbook.datamining.data.realtime.UserInteractionTracker
import com.android.tripbook.datamining.data.repository.ChartDataGenerator
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
    val availableRegions = listOf("All", "Central Africa", "East Africa", "North Africa", "West Africa", "Southern Africa")

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

    // Advanced visualization data
    private val _destinationPopularityData = MutableStateFlow<ChartDataSet?>(null)
    val destinationPopularityData: StateFlow<ChartDataSet?> = _destinationPopularityData.asStateFlow()

    private val _budgetDistributionData = MutableStateFlow<ChartDataSet?>(null)
    val budgetDistributionData: StateFlow<ChartDataSet?> = _budgetDistributionData.asStateFlow()

    private val _travelStylePreferencesData = MutableStateFlow<ChartDataSet?>(null)
    val travelStylePreferencesData: StateFlow<ChartDataSet?> = _travelStylePreferencesData.asStateFlow()

    private val _destinationHeatMapData = MutableStateFlow<HeatMapDataSet?>(null)
    val destinationHeatMapData: StateFlow<HeatMapDataSet?> = _destinationHeatMapData.asStateFlow()

    private val _destinationComparisonData = MutableStateFlow<RadarChartDataSet?>(null)
    val destinationComparisonData: StateFlow<RadarChartDataSet?> = _destinationComparisonData.asStateFlow()

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
        generateAdvancedVisualizationData()
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
                    // Cameroon recommendations
                    TravelRecommendation(
                        id = 1,
                        title = "Mount Cameroon Adventure",
                        description = "Climb the highest mountain in West and Central Africa with breathtaking views of volcanic landscapes and diverse ecosystems.",
                        destinationId = 18,
                        destinationName = "Mount Cameroon",
                        imageUrl = "https://images.unsplash.com/photo-1454496522488-7a8e488e8606",
                        confidence = 0.94f,
                        tags = listOf("mountain", "hiking", "adventure", "volcano"),
                        relevanceScore = 0.92f,
                        region = "Central Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Adventure"
                    ),
                    TravelRecommendation(
                        id = 2,
                        title = "Kribi Beach Getaway",
                        description = "Relax on the beautiful white sand beaches of Kribi with fresh seafood and a peaceful atmosphere along the Atlantic coast.",
                        destinationId = 17,
                        destinationName = "Kribi Beach",
                        imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
                        confidence = 0.88f,
                        tags = listOf("beach", "seafood", "relaxation", "ocean"),
                        relevanceScore = 0.85f,
                        region = "Central Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Beach"
                    ),
                    TravelRecommendation(
                        id = 3,
                        title = "Wildlife Safari at Waza",
                        description = "Experience Cameroon's most visited national park with lions, elephants, giraffes and diverse wildlife in their natural habitat.",
                        destinationId = 12,
                        destinationName = "Waza National Park",
                        imageUrl = "https://images.unsplash.com/photo-1549366021-9f761d450615",
                        confidence = 0.90f,
                        tags = listOf("safari", "wildlife", "nature", "photography"),
                        relevanceScore = 0.88f,
                        region = "Central Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Safari"
                    ),
                    TravelRecommendation(
                        id = 4,
                        title = "Cultural Heritage Tour",
                        description = "Explore the UNESCO World Heritage site of Bafut Palace and discover the rich cultural heritage of Cameroon's Northwest Region.",
                        destinationId = 16,
                        destinationName = "Bafut Palace",
                        imageUrl = "https://images.unsplash.com/photo-1566419808810-658178380987",
                        confidence = 0.86f,
                        tags = listOf("culture", "history", "heritage", "architecture"),
                        relevanceScore = 0.82f,
                        region = "Central Africa",
                        budgetCategory = "Budget",
                        travelStyle = "Cultural"
                    ),
                    TravelRecommendation(
                        id = 5,
                        title = "Rainforest Expedition",
                        description = "Discover the exceptional biodiversity of Dja Faunal Reserve, a UNESCO World Heritage site with pristine rainforest ecosystems.",
                        destinationId = 11,
                        destinationName = "Dja Faunal Reserve",
                        imageUrl = "https://images.unsplash.com/photo-1552083974-186346191183",
                        confidence = 0.84f,
                        tags = listOf("rainforest", "wildlife", "biodiversity", "conservation"),
                        relevanceScore = 0.80f,
                        region = "Central Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Eco-tourism"
                    ),
                    TravelRecommendation(
                        id = 6,
                        title = "Ekom-Nkam Waterfalls",
                        description = "Visit the impressive waterfalls featured in Tarzan films, surrounded by lush rainforest in Cameroon's Littoral Region.",
                        destinationId = 13,
                        destinationName = "Ekom-Nkam Waterfalls",
                        imageUrl = "https://images.unsplash.com/photo-1564982752979-3f7c5f4a8b3b",
                        confidence = 0.87f,
                        tags = listOf("waterfall", "nature", "film", "photography"),
                        relevanceScore = 0.83f,
                        region = "Central Africa",
                        budgetCategory = "Budget",
                        travelStyle = "Adventure"
                    ),
                    TravelRecommendation(
                        id = 7,
                        title = "Mefou Primate Sanctuary",
                        description = "Meet rescued chimpanzees, gorillas and other primates at this conservation center near Yaoundé.",
                        destinationId = 10,
                        destinationName = "Mefou National Park",
                        imageUrl = "https://images.unsplash.com/photo-1584844115436-473887b1e327",
                        confidence = 0.89f,
                        tags = listOf("wildlife", "primates", "conservation", "nature"),
                        relevanceScore = 0.86f,
                        region = "Central Africa",
                        budgetCategory = "Budget",
                        travelStyle = "Eco-tourism"
                    ),
                    // Original recommendations
                    TravelRecommendation(
                        id = 8,
                        title = "Safari Adventure",
                        description = "Based on your interest in wildlife and adventure activities, we recommend a safari trip to Serengeti National Park.",
                        destinationId = 2,
                        destinationName = "Serengeti National Park",
                        imageUrl = "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                        confidence = 0.92f,
                        tags = listOf("safari", "wildlife", "photography"),
                        relevanceScore = 0.78f,
                        region = "East Africa",
                        budgetCategory = "Luxury",
                        travelStyle = "Safari"
                    ),
                    TravelRecommendation(
                        id = 9,
                        title = "Beach Relaxation",
                        description = "Your preference for beach destinations suggests you would enjoy the pristine beaches of Zanzibar.",
                        destinationId = 1,
                        destinationName = "Zanzibar",
                        imageUrl = "https://images.unsplash.com/photo-1586500036706-41963de24d8c",
                        confidence = 0.88f,
                        tags = listOf("beach", "relaxation", "island"),
                        relevanceScore = 0.75f,
                        region = "East Africa",
                        budgetCategory = "Mid-range",
                        travelStyle = "Beach"
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
                            title = "Cameroon Dry Season Safari",
                            description = "Winter is the perfect time to visit Waza National Park in Cameroon with excellent wildlife viewing conditions.",
                            destinationId = 12,
                            destinationName = "Waza National Park",
                            imageUrl = "https://images.unsplash.com/photo-1549366021-9f761d450615",
                            confidence = 0.94f,
                            tags = listOf("safari", "winter", "wildlife", "dry season"),
                            relevanceScore = 0.91f,
                            region = "Central Africa",
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
                            title = "Cameroon Cultural Festival",
                            description = "Experience the vibrant spring cultural festivals in Cameroon's Northwest Region with traditional music, dance, and food.",
                            destinationId = 16,
                            destinationName = "Bafut Palace",
                            imageUrl = "https://images.unsplash.com/photo-1566419808810-658178380987",
                            confidence = 0.89f,
                            tags = listOf("culture", "festival", "spring", "tradition"),
                            relevanceScore = 0.86f,
                            region = "Central Africa",
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
                            title = "Kribi Beach Summer Escape",
                            description = "Enjoy the perfect summer weather at the beautiful beaches of Kribi in Cameroon with fresh seafood and water activities.",
                            destinationId = 17,
                            destinationName = "Kribi Beach",
                            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
                            confidence = 0.96f,
                            tags = listOf("beach", "summer", "seafood", "swimming"),
                            relevanceScore = 0.93f,
                            region = "Central Africa",
                            budgetCategory = "Mid-range",
                            travelStyle = "Beach",
                            isPredictive = true
                        )
                    )
                }
                "Fall" -> {
                    predictiveRecs.add(
                        TravelRecommendation(
                            id = 104,
                            title = "Mount Cameroon Hiking Adventure",
                            description = "Fall is the perfect time to climb Mount Cameroon with mild temperatures and clear views of the surrounding landscapes.",
                            destinationId = 18,
                            destinationName = "Mount Cameroon",
                            imageUrl = "https://images.unsplash.com/photo-1454496522488-7a8e488e8606",
                            confidence = 0.88f,
                            tags = listOf("hiking", "mountain", "fall", "volcano"),
                            relevanceScore = 0.85f,
                            region = "Central Africa",
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
                                title = "Exclusive Waza Wildlife Photography Tour",
                                description = "Based on your interest in wildlife, we've found a special photography tour in Cameroon's Waza National Park.",
                                destinationId = 12,
                                destinationName = "Waza National Park",
                                imageUrl = "https://images.unsplash.com/photo-1549366021-9f761d450615",
                                confidence = 0.92f,
                                tags = listOf("wildlife", "photography", "exclusive", "safari"),
                                relevanceScore = 0.90f,
                                region = "Central Africa",
                                budgetCategory = "Mid-range",
                                travelStyle = "Safari",
                                isPredictive = true
                            )
                        )
                    }
                    "Beach" -> {
                        predictiveRecs.add(
                            TravelRecommendation(
                                id = 106,
                                title = "Hidden Kribi Beach Paradise",
                                description = "Discover the secluded beaches near Kribi in Cameroon that match your preference for quiet beach destinations.",
                                destinationId = 17,
                                destinationName = "Kribi Beach",
                                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
                                confidence = 0.90f,
                                tags = listOf("beach", "secluded", "paradise", "seafood"),
                                relevanceScore = 0.87f,
                                region = "Central Africa",
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
                                title = "Authentic Cameroon Cultural Immersion",
                                description = "Experience authentic cultural traditions with a stay at the Bafut Palace in Cameroon's Northwest Region.",
                                destinationId = 16,
                                destinationName = "Bafut Palace",
                                imageUrl = "https://images.unsplash.com/photo-1566419808810-658178380987",
                                confidence = 0.87f,
                                tags = listOf("culture", "immersion", "authentic", "heritage"),
                                relevanceScore = 0.84f,
                                region = "Central Africa",
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
     * Generate advanced visualization data for the dashboard
     */
    private fun generateAdvancedVisualizationData() {
        viewModelScope.launch {
            try {
                // Generate destination popularity data
                _destinationPopularityData.value = ChartDataGenerator.generateDestinationPopularityData()

                // Generate budget distribution data
                _budgetDistributionData.value = ChartDataGenerator.generateBudgetDistributionData()

                // Generate travel style preferences data
                _travelStylePreferencesData.value = ChartDataGenerator.generateTravelStylePreferenceData()

                // Generate destination heat map data
                _destinationHeatMapData.value = ChartDataGenerator.generateDestinationHeatMapData()

                // Generate destination comparison data
                _destinationComparisonData.value = ChartDataGenerator.generateDestinationComparisonData()
            } catch (e: Exception) {
                _error.value = "Failed to generate advanced visualization data: ${e.message}"
            }
        }
    }

    /**
     * Handle feedback on a recommendation
     */
    fun handleRecommendationFeedback(
        recommendation: TravelRecommendation,
        feedbackType: FeedbackType,
        rating: Float? = null
    ) {
        viewModelScope.launch {
            try {
                // If we have a feedback system, record the feedback
                recommendationFeedback?.let { feedback ->
                    feedback.recordFeedback(
                        RecommendationFeedback.Feedback(
                            userId = currentUserId,
                            recommendationId = recommendation.id,
                            destinationId = recommendation.destinationId,
                            type = feedbackType,
                            rating = rating
                        )
                    )
                }

                // If we have an interaction tracker, track the interaction
                userInteractionTracker?.let { tracker ->
                    val interactionType = when (feedbackType) {
                        FeedbackType.LIKE -> UserInteractionTracker.InteractionType.RATE
                        FeedbackType.DISLIKE -> UserInteractionTracker.InteractionType.RATE
                        FeedbackType.SAVE -> UserInteractionTracker.InteractionType.BOOKMARK
                        FeedbackType.DISMISS -> UserInteractionTracker.InteractionType.DISMISS
                        FeedbackType.CLICK -> UserInteractionTracker.InteractionType.CLICK
                        FeedbackType.RATE -> UserInteractionTracker.InteractionType.RATE
                    }

                    tracker.trackInteraction(
                        UserInteractionTracker.UserInteraction(
                            userId = currentUserId,
                            type = interactionType,
                            targetId = recommendation.destinationId?.toString(),
                            targetType = "destination",
                            value = when (feedbackType) {
                                FeedbackType.LIKE -> 5f
                                FeedbackType.DISLIKE -> 1f
                                FeedbackType.RATE -> rating
                                else -> null
                            },
                            metadata = mapOf(
                                "recommendation_id" to recommendation.id.toString(),
                                "recommendation_type" to if (recommendation.isPredictive) "predictive" else "standard",
                                "region" to recommendation.region,
                                "budget_category" to recommendation.budgetCategory,
                                "travel_style" to recommendation.travelStyle
                            )
                        )
                    )
                }

                // Update UI based on feedback
                when (feedbackType) {
                    FeedbackType.DISMISS -> {
                        // Remove the recommendation from the list
                        _recommendations.value = _recommendations.value.filter { it.id != recommendation.id }
                        _predictiveRecommendations.value = _predictiveRecommendations.value.filter { it.id != recommendation.id }
                    }
                    else -> {
                        // For other feedback types, we might want to update the UI in some way
                        // For example, we could show a confirmation message
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to process feedback: ${e.message}"
            }
        }
    }

    /**
     * Factory for creating DataMiningViewModel with dependencies
     */
    class Factory(
        private val repository: DataMiningRepository,
        private val recommendationFeedback: RecommendationFeedback? = null,
        private val userInteractionTracker: UserInteractionTracker? = null,
        private val dynamicRecommender: DynamicRecommender? = null,
        private val trendAnalyzer: TrendAnalyzer? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DataMiningViewModel::class.java)) {
                return DataMiningViewModel(
                    repository = repository,
                    recommendationFeedback = recommendationFeedback,
                    userInteractionTracker = userInteractionTracker,
                    dynamicRecommender = dynamicRecommender,
                    trendAnalyzer = trendAnalyzer
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
