package com.android.tripbook.datamining.data.realtime

import com.android.tripbook.datamining.data.database.dao.DestinationDao
import com.android.tripbook.datamining.data.database.dao.TravelPatternDao
import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.TravelPattern
import com.android.tripbook.datamining.data.model.TrendingDestination
import com.android.tripbook.datamining.data.model.TrendingTopic
import com.android.tripbook.datamining.data.realtime.UserInteractionTracker.InteractionType
import com.android.tripbook.datamining.data.realtime.UserInteractionTracker.UserInteraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.exp
import kotlin.math.ln

/**
 * Analyzes real-time trends from user interactions
 */
class TrendAnalyzer(
    private val userInteractionTracker: UserInteractionTracker,
    private val destinationDao: DestinationDao,
    private val travelPatternDao: TravelPatternDao,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // Time windows for trend analysis
    enum class TimeWindow {
        HOUR,
        DAY,
        WEEK,
        MONTH
    }
    
    // Interaction counts for destinations
    private val destinationCounts = ConcurrentHashMap<String, ConcurrentHashMap<TimeWindow, AtomicInteger>>()
    
    // Interaction counts for topics (tags, regions, etc.)
    private val topicCounts = ConcurrentHashMap<Pair<String, String>, ConcurrentHashMap<TimeWindow, AtomicInteger>>()
    
    // Trending destinations
    private val _trendingDestinations = MutableStateFlow<List<TrendingDestination>>(emptyList())
    val trendingDestinations: StateFlow<List<TrendingDestination>> = _trendingDestinations.asStateFlow()
    
    // Trending topics
    private val _trendingTopics = MutableStateFlow<List<TrendingTopic>>(emptyList())
    val trendingTopics: StateFlow<List<TrendingTopic>> = _trendingTopics.asStateFlow()
    
    // Last update timestamps
    private var lastDestinationUpdate = 0L
    private var lastTopicUpdate = 0L
    private var lastPatternUpdate = 0L
    
    // Update intervals
    private val destinationUpdateInterval = 5 * 60 * 1000 // 5 minutes
    private val topicUpdateInterval = 10 * 60 * 1000 // 10 minutes
    private val patternUpdateInterval = 60 * 60 * 1000 // 1 hour
    
    init {
        // Set up interaction processing
        coroutineScope.launch {
            userInteractionTracker.interactionEvents.collect { interaction ->
                processInteractionForTrends(interaction)
            }
        }
    }
    
    /**
     * Processes an interaction for trend analysis
     */
    private fun processInteractionForTrends(interaction: UserInteraction) {
        when (interaction.type) {
            InteractionType.VIEW, 
            InteractionType.CLICK, 
            InteractionType.BOOKMARK,
            InteractionType.RATE,
            InteractionType.SHARE -> {
                // Process destination interaction
                if (interaction.targetType == "destination" && interaction.targetId != null) {
                    incrementDestinationCount(interaction.targetId)
                }
                
                // Process metadata for topic trends
                interaction.metadata.forEach { (key, value) ->
                    incrementTopicCount(key, value)
                }
            }
            InteractionType.SEARCH -> {
                // Process search terms
                val searchQuery = interaction.metadata["query"] ?: return
                val searchTerms = searchQuery.split(" ", ",", ";")
                    .map { it.trim().lowercase() }
                    .filter { it.length > 3 }
                
                searchTerms.forEach { term ->
                    incrementTopicCount("search_term", term)
                }
            }
            InteractionType.FILTER -> {
                // Process filter selections
                val filterType = interaction.metadata["filter_type"] ?: return
                val filterValue = interaction.metadata["filter_value"] ?: return
                
                incrementTopicCount(filterType, filterValue)
            }
        }
        
        // Check if it's time to update trending lists
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastDestinationUpdate > destinationUpdateInterval) {
            updateTrendingDestinations()
            lastDestinationUpdate = currentTime
        }
        
        if (currentTime - lastTopicUpdate > topicUpdateInterval) {
            updateTrendingTopics()
            lastTopicUpdate = currentTime
        }
        
        if (currentTime - lastPatternUpdate > patternUpdateInterval) {
            updateTravelPatterns()
            lastPatternUpdate = currentTime
        }
    }
    
    /**
     * Increments the count for a destination across all time windows
     */
    private fun incrementDestinationCount(destinationId: String) {
        val counts = destinationCounts.getOrPut(destinationId) {
            ConcurrentHashMap<TimeWindow, AtomicInteger>().apply {
                TimeWindow.values().forEach { window ->
                    this[window] = AtomicInteger(0)
                }
            }
        }
        
        // Increment counts for all time windows
        TimeWindow.values().forEach { window ->
            counts[window]?.incrementAndGet()
        }
    }
    
    /**
     * Increments the count for a topic across all time windows
     */
    private fun incrementTopicCount(topicType: String, topicValue: String) {
        val key = Pair(topicType, topicValue)
        val counts = topicCounts.getOrPut(key) {
            ConcurrentHashMap<TimeWindow, AtomicInteger>().apply {
                TimeWindow.values().forEach { window ->
                    this[window] = AtomicInteger(0)
                }
            }
        }
        
        // Increment counts for all time windows
        TimeWindow.values().forEach { window ->
            counts[window]?.incrementAndGet()
        }
    }
    
    /**
     * Updates the list of trending destinations
     */
    private fun updateTrendingDestinations() {
        coroutineScope.launch {
            try {
                // Get all destinations
                val allDestinations = destinationDao.getAllDestinations()
                val destinationMap = allDestinations.associateBy { it.id.toString() }
                
                // Calculate trending score for each destination
                val trendingScores = destinationCounts.mapNotNull { (destId, counts) ->
                    val destination = destinationMap[destId] ?: return@mapNotNull null
                    
                    // Calculate trending score using time-decayed counts
                    val hourCount = counts[TimeWindow.HOUR]?.get() ?: 0
                    val dayCount = counts[TimeWindow.DAY]?.get() ?: 0
                    val weekCount = counts[TimeWindow.WEEK]?.get() ?: 0
                    
                    // Weight recent interactions more heavily
                    val trendingScore = (hourCount * 10) + (dayCount * 3) + weekCount
                    
                    TrendingDestination(
                        id = destination.id,
                        name = destination.name,
                        region = destination.region,
                        imageUrl = destination.imageUrl,
                        trendingScore = trendingScore,
                        hourlyInteractions = hourCount,
                        dailyInteractions = dayCount,
                        weeklyInteractions = weekCount
                    )
                }
                
                // Sort by trending score and update the flow
                _trendingDestinations.value = trendingScores
                    .sortedByDescending { it.trendingScore }
                    .take(20) // Limit to top 20
                
                // Age the counts (reduce older time windows)
                ageInteractionCounts()
                
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Updates the list of trending topics
     */
    private fun updateTrendingTopics() {
        coroutineScope.launch {
            try {
                // Calculate trending score for each topic
                val trendingScores = topicCounts.map { (topic, counts) ->
                    val (topicType, topicValue) = topic
                    
                    // Calculate trending score using time-decayed counts
                    val hourCount = counts[TimeWindow.HOUR]?.get() ?: 0
                    val dayCount = counts[TimeWindow.DAY]?.get() ?: 0
                    val weekCount = counts[TimeWindow.WEEK]?.get() ?: 0
                    
                    // Weight recent interactions more heavily
                    val trendingScore = (hourCount * 10) + (dayCount * 3) + weekCount
                    
                    TrendingTopic(
                        type = topicType,
                        value = topicValue,
                        trendingScore = trendingScore,
                        hourlyInteractions = hourCount,
                        dailyInteractions = dayCount,
                        weeklyInteractions = weekCount
                    )
                }
                
                // Sort by trending score and update the flow
                _trendingTopics.value = trendingScores
                    .sortedByDescending { it.trendingScore }
                    .take(20) // Limit to top 20
                
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Updates travel patterns based on trending data
     */
    private fun updateTravelPatterns() {
        coroutineScope.launch {
            try {
                // Create patterns from trending destinations
                val trendingDests = _trendingDestinations.value
                if (trendingDests.isNotEmpty()) {
                    // Create a trending destinations pattern
                    val patternData = JSONObject().apply {
                        put("trending_destinations", trendingDests.take(5).map { it.name })
                        put("trending_regions", trendingDests
                            .groupBy { it.region }
                            .mapValues { (_, dests) -> dests.sumOf { it.trendingScore } }
                            .entries
                            .sortedByDescending { it.value }
                            .take(3)
                            .map { it.key }
                        )
                        put("timestamp", System.currentTimeMillis())
                    }
                    
                    val trendPattern = TravelPattern(
                        userId = "global",
                        patternType = "trending",
                        patternName = "Current Trending Destinations",
                        patternValue = trendingDests.first().trendingScore.toFloat(),
                        patternData = patternData.toString(),
                        startDate = Date(),
                        endDate = Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)), // 1 week
                        confidence = 0.9f,
                        sampleSize = trendingDests.size
                    )
                    
                    // Save the pattern
                    travelPatternDao.insert(trendPattern)
                }
                
                // Create patterns from trending topics
                val trendingTopicsList = _trendingTopics.value
                if (trendingTopicsList.isNotEmpty()) {
                    // Group topics by type
                    val topicsByType = trendingTopicsList.groupBy { it.type }
                    
                    topicsByType.forEach { (topicType, topics) ->
                        if (topics.isNotEmpty()) {
                            val patternData = JSONObject().apply {
                                put("trending_topics", topics.take(5).map { it.value })
                                put("topic_type", topicType)
                                put("timestamp", System.currentTimeMillis())
                            }
                            
                            val trendPattern = TravelPattern(
                                userId = "global",
                                patternType = "trending_topic",
                                patternName = "Trending $topicType",
                                patternValue = topics.first().trendingScore.toFloat(),
                                patternData = patternData.toString(),
                                startDate = Date(),
                                endDate = Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)), // 1 week
                                confidence = 0.85f,
                                sampleSize = topics.size
                            )
                            
                            // Save the pattern
                            travelPatternDao.insert(trendPattern)
                        }
                    }
                }
                
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Ages interaction counts to give more weight to recent interactions
     */
    private fun ageInteractionCounts() {
        // Age destination counts
        destinationCounts.forEach { (_, counts) ->
            // Reduce hour counts by 50%
            counts[TimeWindow.HOUR]?.updateAndGet { it / 2 }
            
            // Reduce day counts by 20%
            counts[TimeWindow.DAY]?.updateAndGet { (it * 0.8).toInt() }
            
            // Reduce week counts by 10%
            counts[TimeWindow.WEEK]?.updateAndGet { (it * 0.9).toInt() }
        }
        
        // Age topic counts
        topicCounts.forEach { (_, counts) ->
            // Reduce hour counts by 50%
            counts[TimeWindow.HOUR]?.updateAndGet { it / 2 }
            
            // Reduce day counts by 20%
            counts[TimeWindow.DAY]?.updateAndGet { (it * 0.8).toInt() }
            
            // Reduce week counts by 10%
            counts[TimeWindow.WEEK]?.updateAndGet { (it * 0.9).toInt() }
        }
    }
}
