package com.android.algorithms.insights.model

interface InsightsService {
    
    /**
     * Generate insights based on request parameters
     */
    suspend fun generateInsights(request: InsightRequest): List<Insight>
    
    /**
     * Get insight by ID
     */
    suspend fun getInsightById(insightId: String): Insight?
    
    /**
     * Get insights for a specific user
     */
    suspend fun getInsightsByUser(userId: String, page: Int = 1, pageSize: Int = 20): List<Insight>
    
    /**
     * Get insights by type
     */
    suspend fun getInsightsByType(type: InsightType, page: Int = 1, pageSize: Int = 20): List<Insight>
    
    /**
     * Get insights by priority
     */
    suspend fun getInsightsByPriority(priority: InsightPriority, page: Int = 1, pageSize: Int = 20): List<Insight>
    
    /**
     * Get insights by category
     */
    suspend fun getInsightsByCategory(category: String, page: Int = 1, pageSize: Int = 20): List<Insight>
    
    /**
     * Get unread insights for a user
     */
    suspend fun getUnreadInsights(userId: String): List<Insight>
    
    /**
     * Get actionable insights (not yet acted upon)
     */
    suspend fun getActionableInsights(userId: String? = null): List<Insight>
    
    /**
     * Mark insight as read
     */
    suspend fun markInsightAsRead(insightId: String, userId: String): Boolean
    
    /**
     * Mark insight as action taken
     */
    suspend fun markActionTaken(insightId: String, userId: String, actionDescription: String): Boolean
    
    /**
     * Get insight statistics
     */
    suspend fun getInsightStats(userId: String? = null): InsightStats
    
    /**
     * Send notification for an insight
     */
    suspend fun sendNotification(request: NotificationRequest): InsightNotification?
    
    /**
     * Get notification history
     */
    suspend fun getNotificationHistory(userId: String, page: Int = 1, pageSize: Int = 20): List<InsightNotification>
    
    /**
     * Submit feedback for an insight
     */
    suspend fun submitFeedback(feedback: InsightFeedback): Boolean
    
    /**
     * Get user's insight configuration
     */
    suspend fun getInsightConfiguration(userId: String): InsightConfiguration?
    
    /**
     * Update user's insight configuration
     */
    suspend fun updateInsightConfiguration(userId: String, config: InsightConfiguration): Boolean
    
    /**
     * Analyze trends for a specific metric
     */
    suspend fun analyzeTrends(metric: String, timeframe: String, userId: String? = null): TrendAnalysis?
    
    /**
     * Get user behavior insights
     */
    suspend fun getUserBehaviorInsights(userId: String): List<UserBehaviorInsight>
    
    /**
     * Detect anomalies in data
     */
    suspend fun detectAnomalies(metric: String, timeframe: String): List<AnomalyDetection>
    
    /**
     * Get recommendations based on insights
     */
    suspend fun getRecommendations(userId: String? = null, category: String? = null): List<Recommendation>
    
    /**
     * Delete insight by ID
     */
    suspend fun deleteInsight(insightId: String): Boolean
    
    /**
     * Get insights dashboard data
     */
    suspend fun getDashboardData(userId: String): DashboardData
    
    /**
     * Process real-time data for immediate insights
     */
    suspend fun processRealTimeData(data: Map<String, Any>): List<Insight>
    
    /**
     * Get trending topics and patterns
     */
    suspend fun getTrendingPatterns(timeframe: String = "24h"): List<TrendingPattern>
    
    /**
     * Predict future trends
     */
    suspend fun predictTrends(metric: String, timeHorizon: String): TrendPrediction?
    
    /**
     * Get content quality insights
     */
    suspend fun getContentQualityInsights(timeframe: String = "7d"): List<Insight>
    
    /**
     * Get engagement prediction insights
     */
    suspend fun getEngagementPredictions(userId: String? = null): List<Insight>
}
