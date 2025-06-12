package com.android.algorithms.insights.model

import kotlinx.serialization.Serializable

@Serializable
data class InsightRequest(
    val userId: String? = null,
    val timeframe: String = "7d", // 1h, 24h, 7d, 30d, 90d
    val categories: List<String> = emptyList(),
    val insightTypes: List<InsightType> = emptyList(),
    val priority: InsightPriority = InsightPriority.NORMAL
)

@Serializable
enum class InsightType {
    SENTIMENT_TREND,
    TOPIC_EMERGENCE,
    CLASSIFICATION_PATTERN,
    USER_BEHAVIOR,
    CONTENT_QUALITY,
    ENGAGEMENT_PREDICTION,
    ANOMALY_DETECTION,
    RECOMMENDATION
}

@Serializable
enum class InsightPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

@Serializable
data class Insight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val category: String,
    val priority: InsightPriority,
    val confidence: Double, // 0.0 to 1.0
    val impact: InsightImpact,
    val timeframe: String,
    val generatedAt: String,
    val expiresAt: String? = null,
    val data: InsightData,
    val actionableRecommendations: List<Recommendation>,
    val relatedInsights: List<String> = emptyList(),
    val userId: String? = null,
    val isRead: Boolean = false,
    val isActionTaken: Boolean = false
)

@Serializable
enum class InsightImpact {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Serializable
data class InsightData(
    val metrics: Map<String, Double>,
    val trends: Map<String, List<DataPoint>>,
    val comparisons: Map<String, ComparisonData>,
    val predictions: Map<String, PredictionData>,
    val anomalies: List<AnomalyData> = emptyList()
)

@Serializable
data class DataPoint(
    val timestamp: String,
    val value: Double,
    val label: String? = null
)

@Serializable
data class ComparisonData(
    val current: Double,
    val previous: Double,
    val changePercentage: Double,
    val trend: String // "increasing", "decreasing", "stable"
)

@Serializable
data class PredictionData(
    val predictedValue: Double,
    val confidence: Double,
    val timeHorizon: String,
    val factors: List<String>
)

@Serializable
data class AnomalyData(
    val timestamp: String,
    val value: Double,
    val expectedValue: Double,
    val severity: String, // "low", "medium", "high"
    val description: String
)

@Serializable
data class Recommendation(
    val id: String,
    val title: String,
    val description: String,
    val actionType: ActionType,
    val priority: InsightPriority,
    val estimatedImpact: String,
    val implementationEffort: String, // "low", "medium", "high"
    val category: String,
    val parameters: Map<String, String> = emptyMap()
)

@Serializable
enum class ActionType {
    CONTENT_OPTIMIZATION,
    USER_ENGAGEMENT,
    MODERATION_ACTION,
    ALGORITHM_TUNING,
    NOTIFICATION_TRIGGER,
    REPORTING,
    INVESTIGATION
}

@Serializable
data class NotificationRequest(
    val userId: String,
    val insightId: String,
    val channel: NotificationChannel = NotificationChannel.IN_APP,
    val priority: InsightPriority = InsightPriority.NORMAL,
    val customMessage: String? = null
)

@Serializable
enum class NotificationChannel {
    IN_APP,
    EMAIL,
    PUSH,
    SMS,
    WEBHOOK
}

@Serializable
data class NotificationResult(
    val id: String,
    val insightId: String,
    val userId: String,
    val channel: NotificationChannel,
    val status: NotificationStatus,
    val sentAt: String,
    val deliveredAt: String? = null,
    val readAt: String? = null,
    val message: String,
    val error: String? = null
)

@Serializable
enum class NotificationStatus {
    PENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

@Serializable
data class InsightStats(
    val totalInsights: Long,
    val insightsByType: Map<String, Int>, // Changed from InsightType to String
    val insightsByPriority: Map<String, Int>, // Changed from InsightPriority to String
    val insightsByImpact: Map<String, Int>, // Changed from InsightImpact to String
    val averageConfidence: Double,
    val actionTakenRate: Double,
    val readRate: Double,
    val topCategories: List<CategoryCount>, // Changed from Pair to custom class
    val recentTrends: Map<String, String>
)

@Serializable
data class CategoryCount(
    val category: String,
    val count: Int
)

// Response classes
@Serializable
data class InsightResponse(
    val success: Boolean,
    val message: String,
    val data: Insight? = null,
    val error: String? = null
)

@Serializable
data class InsightListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Insight>,
    val totalCount: Int? = null,
    val error: String? = null
)

@Serializable
data class InsightStatsResponse(
    val success: Boolean,
    val message: String,
    val data: InsightStats,
    val error: String? = null
)

@Serializable
data class NotificationResponse(
    val success: Boolean,
    val message: String,
    val data: InsightNotification,
    val error: String? = null
)

@Serializable
data class NotificationHistoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<InsightNotification>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val error: String? = null
)

@Serializable
data class ConfigurationResponse(
    val success: Boolean,
    val message: String,
    val data: InsightConfiguration,
    val error: String? = null
)

@Serializable
data class TrendAnalysisResponse(
    val success: Boolean,
    val message: String,
    val data: TrendAnalysis,
    val error: String? = null
)

@Serializable
data class UserBehaviorResponse(
    val success: Boolean,
    val message: String,
    val data: List<UserBehaviorInsight>,
    val error: String? = null
)

@Serializable
data class AnomalyDetectionResponse(
    val success: Boolean,
    val message: String,
    val data: List<AnomalyDetection>,
    val metric: String,
    val timeframe: String,
    val error: String? = null
)

@Serializable
data class RecommendationsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Recommendation>,
    val error: String? = null
)

@Serializable
data class DashboardResponse(
    val success: Boolean,
    val message: String,
    val data: DashboardData,
    val error: String? = null
)

@Serializable
data class TrendingPatternsResponse(
    val success: Boolean,
    val message: String,
    val data: List<TrendingPattern>,
    val error: String? = null
)

@Serializable
data class TrendPredictionResponse(
    val success: Boolean,
    val message: String,
    val data: TrendPrediction,
    val error: String? = null
)

@Serializable
data class InsightFeedback(
    val insightId: String,
    val userId: String,
    val rating: Int, // 1-5 stars
    val isUseful: Boolean,
    val isAccurate: Boolean,
    val comments: String? = null,
    val actionTaken: String? = null,
    val submittedAt: String
)

@Serializable
data class InsightConfiguration(
    val userId: String? = null,
    val enabledInsightTypes: List<InsightType>,
    val notificationChannels: List<NotificationChannel>,
    val minimumPriority: InsightPriority,
    val minimumConfidence: Double,
    val updateFrequency: String, // "realtime", "hourly", "daily"
    val customFilters: Map<String, String> = emptyMap()
)

@Serializable
data class TrendAnalysis(
    val metric: String,
    val timeframe: String,
    val dataPoints: List<DataPoint>,
    val trend: String,
    val changeRate: Double,
    val seasonality: String? = null,
    val forecast: List<DataPoint> = emptyList()
)

@Serializable
data class UserBehaviorInsight(
    val userId: String,
    val behaviorPattern: String,
    val frequency: String,
    val lastOccurrence: String,
    val predictedNextOccurrence: String? = null,
    val confidence: Double,
    val relatedActions: List<String>
)

@Serializable
data class InsightNotification(
    val id: String,
    val insightId: String,
    val userId: String,
    val channel: NotificationChannel,
    val priority: InsightPriority,
    val message: String,
    val customMessage: String? = null,
    val sentAt: String,
    val isRead: Boolean = false,
    val readAt: String? = null
)

@Serializable
data class AnomalyDetection(
    val id: String,
    val metric: String,
    val detectedAt: String,
    val value: Double,
    val expectedValue: Double,
    val deviation: Double,
    val severity: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val description: String,
    val possibleCauses: List<String> = emptyList()
)

@Serializable
data class DashboardData(
    val userId: String,
    val totalInsights: Int,
    val unreadInsights: Int,
    val highPriorityInsights: Int,
    val recentInsights: List<Insight>,
    val insightsByType: Map<String, Int>,
    val insightsByPriority: Map<String, Int>,
    val trendingSentiment: String,
    val lastUpdated: String
)

@Serializable
data class TrendingPattern(
    val pattern: String,
    val frequency: Int,
    val trend: String, // "increasing", "decreasing", "stable"
    val timeframe: String,
    val confidence: Double,
    val description: String
)

@Serializable
data class TrendPrediction(
    val metric: String,
    val timeHorizon: String,
    val predictedValues: List<DataPoint>,
    val confidence: Double,
    val methodology: String,
    val factors: List<String>,
    val generatedAt: String
)
