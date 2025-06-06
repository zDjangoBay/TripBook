package com.TripBook.postmodule

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Analytics service for tracking post-related user interactions and metrics.
 * Provides insights into user behavior and post creation patterns.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostAnalytics(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "post_analytics", Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    companion object {
        private const val SESSION_KEY = "current_session"
        private const val METRICS_KEY = "analytics_metrics"
        private const val EVENTS_KEY_PREFIX = "event_"
        private const val MAX_EVENTS_STORED = 1000
    }
    
    /**
     * Tracks a post event for analytics
     */
    suspend fun trackEvent(event: PostEvent, sessionId: String = getCurrentSessionId()) = withContext(Dispatchers.IO) {
        val analyticsEvent = AnalyticsEvent(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            eventType = event::class.simpleName ?: "Unknown",
            timestamp = System.currentTimeMillis(),
            properties = extractEventProperties(event)
        )
        
        storeEvent(analyticsEvent)
        updateMetrics(event)
    }
    
    /**
     * Tracks form completion metrics
     */
    suspend fun trackFormCompletion(
        timeSpent: Long,
        fieldsCompleted: Int,
        totalFields: Int,
        wasSubmitted: Boolean
    ) = withContext(Dispatchers.IO) {
        val completionRate = (fieldsCompleted.toDouble() / totalFields * 100).toInt()
        
        val event = AnalyticsEvent(
            id = UUID.randomUUID().toString(),
            sessionId = getCurrentSessionId(),
            eventType = "FormCompletion",
            timestamp = System.currentTimeMillis(),
            properties = mapOf(
                "time_spent_ms" to timeSpent.toString(),
                "fields_completed" to fieldsCompleted.toString(),
                "total_fields" to totalFields.toString(),
                "completion_rate" to completionRate.toString(),
                "was_submitted" to wasSubmitted.toString()
            )
        )
        
        storeEvent(event)
    }
    
    /**
     * Tracks user engagement patterns
     */
    suspend fun trackEngagement(
        feature: String,
        action: String,
        value: String? = null
    ) = withContext(Dispatchers.IO) {
        val properties = mutableMapOf(
            "feature" to feature,
            "action" to action
        )
        
        value?.let { properties["value"] = it }
        
        val event = AnalyticsEvent(
            id = UUID.randomUUID().toString(),
            sessionId = getCurrentSessionId(),
            eventType = "UserEngagement",
            timestamp = System.currentTimeMillis(),
            properties = properties
        )
        
        storeEvent(event)
    }
    
    /**
     * Gets analytics metrics summary
     */
    suspend fun getMetrics(): AnalyticsMetrics = withContext(Dispatchers.IO) {
        val metricsJson = prefs.getString(METRICS_KEY, null)
        metricsJson?.let { 
            try {
                json.decodeFromString<AnalyticsMetrics>(it)
            } catch (e: Exception) {
                AnalyticsMetrics()
            }
        } ?: AnalyticsMetrics()
    }
    
    /**
     * Gets events for a specific time period
     */
    suspend fun getEvents(
        startTime: Long,
        endTime: Long = System.currentTimeMillis()
    ): List<AnalyticsEvent> = withContext(Dispatchers.IO) {
        val events = mutableListOf<AnalyticsEvent>()
        
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(EVENTS_KEY_PREFIX) && value is String) {
                try {
                    val event = json.decodeFromString<AnalyticsEvent>(value)
                    if (event.timestamp in startTime..endTime) {
                        events.add(event)
                    }
                } catch (e: Exception) {
                    // Ignore corrupted events
                }
            }
        }
        
        events.sortedBy { it.timestamp }
    }
    
    /**
     * Gets the most popular categories
     */
    suspend fun getPopularCategories(limit: Int = 10): List<CategoryStats> = withContext(Dispatchers.IO) {
        val categoryCount = mutableMapOf<String, Int>()
        
        getEvents(0).forEach { event ->
            if (event.eventType == "CategoryChanged") {
                val category = event.properties["category"] ?: "Unknown"
                categoryCount[category] = categoryCount.getOrDefault(category, 0) + 1
            }
        }
        
        categoryCount.map { (category, count) ->
            CategoryStats(category, count)
        }.sortedByDescending { it.count }.take(limit)
    }
    
    /**
     * Gets the most used tags
     */
    suspend fun getPopularTags(limit: Int = 20): List<TagStats> = withContext(Dispatchers.IO) {
        val tagCount = mutableMapOf<String, Int>()
        
        getEvents(0).forEach { event ->
            if (event.eventType == "TagAdded") {
                val tag = event.properties["tag"] ?: "unknown"
                tagCount[tag] = tagCount.getOrDefault(tag, 0) + 1
            }
        }
        
        tagCount.map { (tag, count) ->
            TagStats(tag, count)
        }.sortedByDescending { it.count }.take(limit)
    }
    
    /**
     * Gets user behavior patterns
     */
    suspend fun getUserBehaviorPatterns(): UserBehaviorPatterns = withContext(Dispatchers.IO) {
        val events = getEvents(0)
        val sessions = events.groupBy { it.sessionId }
        
        val avgSessionDuration = sessions.values.map { sessionEvents ->
            if (sessionEvents.isNotEmpty()) {
                sessionEvents.maxOf { it.timestamp } - sessionEvents.minOf { it.timestamp }
            } else 0L
        }.average().toLong()
        
        val avgEventsPerSession = sessions.values.map { it.size }.average()
        
        val mostActiveHour = events.groupBy { 
            java.util.Calendar.getInstance().apply { 
                timeInMillis = it.timestamp 
            }.get(java.util.Calendar.HOUR_OF_DAY)
        }.maxByOrNull { it.value.size }?.key ?: 0
        
        UserBehaviorPatterns(
            averageSessionDuration = avgSessionDuration,
            averageEventsPerSession = avgEventsPerSession,
            mostActiveHour = mostActiveHour,
            totalSessions = sessions.size,
            totalEvents = events.size
        )
    }
    
    /**
     * Exports analytics data
     */
    suspend fun exportAnalytics(): String = withContext(Dispatchers.IO) {
        val metrics = getMetrics()
        val events = getEvents(0)
        val patterns = getUserBehaviorPatterns()
        
        val exportData = AnalyticsExport(
            metrics = metrics,
            events = events,
            patterns = patterns,
            exportTimestamp = System.currentTimeMillis()
        )
        
        json.encodeToString(exportData)
    }
    
    /**
     * Clears old analytics data
     */
    suspend fun cleanupOldData(olderThanDays: Int = 30) = withContext(Dispatchers.IO) {
        val cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
        val editor = prefs.edit()
        
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(EVENTS_KEY_PREFIX) && value is String) {
                try {
                    val event = json.decodeFromString<AnalyticsEvent>(value)
                    if (event.timestamp < cutoffTime) {
                        editor.remove(key)
                    }
                } catch (e: Exception) {
                    // Remove corrupted events
                    editor.remove(key)
                }
            }
        }
        
        editor.apply()
    }
    
    private fun getCurrentSessionId(): String {
        return prefs.getString(SESSION_KEY, null) ?: run {
            val newSessionId = "session_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
            prefs.edit().putString(SESSION_KEY, newSessionId).apply()
            newSessionId
        }
    }
    
    private fun storeEvent(event: AnalyticsEvent) {
        val eventJson = json.encodeToString(event)
        prefs.edit().putString("$EVENTS_KEY_PREFIX${event.id}", eventJson).apply()
        
        // Cleanup if too many events
        cleanupExcessEvents()
    }
    
    private fun updateMetrics(event: PostEvent) {
        val metrics = try {
            val metricsJson = prefs.getString(METRICS_KEY, null)
            metricsJson?.let { json.decodeFromString<AnalyticsMetrics>(it) } ?: AnalyticsMetrics()
        } catch (e: Exception) {
            AnalyticsMetrics()
        }
        
        val updatedMetrics = when (event) {
            is PostEvent.TitleChanged -> metrics.copy(titleChanges = metrics.titleChanges + 1)
            is PostEvent.DescriptionChanged -> metrics.copy(descriptionChanges = metrics.descriptionChanges + 1)
            is PostEvent.ImageAdded -> metrics.copy(imagesAdded = metrics.imagesAdded + 1)
            is PostEvent.LocationAdded -> metrics.copy(locationsAdded = metrics.locationsAdded + 1)
            is PostEvent.TagAdded -> metrics.copy(tagsAdded = metrics.tagsAdded + 1)
            is PostEvent.SubmitPost -> metrics.copy(postsSubmitted = metrics.postsSubmitted + 1)
            is PostEvent.SaveDraft -> metrics.copy(draftsSaved = metrics.draftsSaved + 1)
            else -> metrics
        }
        
        val metricsJson = json.encodeToString(updatedMetrics)
        prefs.edit().putString(METRICS_KEY, metricsJson).apply()
    }
    
    private fun extractEventProperties(event: PostEvent): Map<String, String> {
        return when (event) {
            is PostEvent.TitleChanged -> mapOf("title_length" to event.newTitle.length.toString())
            is PostEvent.DescriptionChanged -> mapOf("description_length" to event.newDescription.length.toString())
            is PostEvent.LocationAdded -> mapOf(
                "latitude" to event.latitude.toString(),
                "longitude" to event.longitude.toString(),
                "has_name" to (event.locationName != null).toString()
            )
            is PostEvent.CategoryChanged -> mapOf("category" to event.category)
            is PostEvent.TagAdded -> mapOf("tag" to event.tag)
            is PostEvent.TagRemoved -> mapOf("tag" to event.tag)
            is PostEvent.VisibilityChanged -> mapOf("visibility" to event.visibility)
            is PostEvent.ShowError -> mapOf("error_message" to event.message)
            is PostEvent.PostCreated -> mapOf("post_id" to event.postId)
            else -> emptyMap()
        }
    }
    
    private fun cleanupExcessEvents() {
        val eventKeys = prefs.all.keys.filter { it.startsWith(EVENTS_KEY_PREFIX) }
        
        if (eventKeys.size > MAX_EVENTS_STORED) {
            val eventsToRemove = eventKeys.size - MAX_EVENTS_STORED
            val editor = prefs.edit()
            
            eventKeys.take(eventsToRemove).forEach { key ->
                editor.remove(key)
            }
            
            editor.apply()
        }
    }
}

@Serializable
data class AnalyticsEvent(
    val id: String,
    val sessionId: String,
    val eventType: String,
    val timestamp: Long,
    val properties: Map<String, String>
)

@Serializable
data class AnalyticsMetrics(
    val titleChanges: Int = 0,
    val descriptionChanges: Int = 0,
    val imagesAdded: Int = 0,
    val locationsAdded: Int = 0,
    val tagsAdded: Int = 0,
    val postsSubmitted: Int = 0,
    val draftsSaved: Int = 0
)

@Serializable
data class CategoryStats(
    val category: String,
    val count: Int
)

@Serializable
data class TagStats(
    val tag: String,
    val count: Int
)

@Serializable
data class UserBehaviorPatterns(
    val averageSessionDuration: Long,
    val averageEventsPerSession: Double,
    val mostActiveHour: Int,
    val totalSessions: Int,
    val totalEvents: Int
)

@Serializable
data class AnalyticsExport(
    val metrics: AnalyticsMetrics,
    val events: List<AnalyticsEvent>,
    val patterns: UserBehaviorPatterns,
    val exportTimestamp: Long
)
