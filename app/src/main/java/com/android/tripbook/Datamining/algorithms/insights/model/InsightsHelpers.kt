package com.android.algorithms.insights.model

import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.*
import kotlin.random.Random

// Helper functions for InsightsServiceImpl

fun InsightsServiceImpl.generateSentimentTrendInsights(request: InsightRequest): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    // Simulated sentiment trend analysis
    val sentimentTrend = Random.nextDouble(-0.5, 0.5)
    val confidence = Random.nextDouble(0.7, 0.95)
    
    val priority = when {
        abs(sentimentTrend) > 0.3 -> InsightPriority.HIGH
        abs(sentimentTrend) > 0.1 -> InsightPriority.NORMAL
        else -> InsightPriority.LOW
    }
    
    val impact = when {
        abs(sentimentTrend) > 0.3 -> InsightImpact.HIGH
        abs(sentimentTrend) > 0.1 -> InsightImpact.MEDIUM
        else -> InsightImpact.LOW
    }
    
    val trendDirection = if (sentimentTrend > 0) "improving" else "declining"
    
    insights.add(
        Insight(
            id = UUID.randomUUID().toString(),
            type = InsightType.SENTIMENT_TREND,
            title = "Sentiment Trend Analysis",
            description = "Overall sentiment is $trendDirection by ${abs(sentimentTrend * 100).toInt()}% over the ${request.timeframe}",
            category = "Sentiment",
            priority = priority,
            confidence = confidence,
            impact = impact,
            timeframe = request.timeframe,
            generatedAt = LocalDateTime.now().toString(),
            expiresAt = LocalDateTime.now().plusDays(7).toString(),
            data = InsightData(
                metrics = mapOf(
                    "sentiment_change" to sentimentTrend,
                    "confidence" to confidence,
                    "sample_size" to Random.nextDouble(100.0, 1000.0)
                ),
                trends = mapOf(
                    "sentiment_over_time" to generateMockDataPoints(request.timeframe)
                ),
                comparisons = mapOf(
                    "vs_previous_period" to ComparisonData(
                        current = sentimentTrend,
                        previous = sentimentTrend - Random.nextDouble(-0.2, 0.2),
                        changePercentage = sentimentTrend * 100,
                        trend = trendDirection
                    )
                ),
                predictions = mapOf(
                    "next_week" to PredictionData(
                        predictedValue = sentimentTrend + Random.nextDouble(-0.1, 0.1),
                        confidence = confidence * 0.8,
                        timeHorizon = "7d",
                        factors = listOf("content_quality", "user_engagement", "external_events")
                    )
                )
            ),
            actionableRecommendations = generateSentimentRecommendations(sentimentTrend),
            userId = request.userId
        )
    )
    
    return insights
}

fun InsightsServiceImpl.generateTopicEmergenceInsights(request: InsightRequest): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    val emergingTopics = listOf("AI Technology", "Sustainable Travel", "Remote Work", "Digital Health", "Climate Change")
    val selectedTopic = emergingTopics.random()
    val emergenceScore = Random.nextDouble(0.6, 0.95)
    
    insights.add(
        Insight(
            id = UUID.randomUUID().toString(),
            type = InsightType.TOPIC_EMERGENCE,
            title = "Emerging Topic Detected",
            description = "Topic '$selectedTopic' is gaining significant traction with ${(emergenceScore * 100).toInt()}% growth",
            category = "Topics",
            priority = InsightPriority.NORMAL,
            confidence = emergenceScore,
            impact = InsightImpact.MEDIUM,
            timeframe = request.timeframe,
            generatedAt = LocalDateTime.now().toString(),
            data = InsightData(
                metrics = mapOf(
                    "emergence_score" to emergenceScore,
                    "mention_count" to Random.nextDouble(50.0, 500.0),
                    "growth_rate" to Random.nextDouble(0.2, 0.8)
                ),
                trends = mapOf(
                    "topic_mentions" to generateMockDataPoints(request.timeframe)
                ),
                comparisons = emptyMap(),
                predictions = emptyMap()
            ),
            actionableRecommendations = generateTopicRecommendations(selectedTopic),
            userId = request.userId
        )
    )
    
    return insights
}

fun InsightsServiceImpl.generateClassificationPatternInsights(request: InsightRequest): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    val patterns = listOf("Increased spam detection", "Content quality improvement", "User intent clarity")
    val selectedPattern = patterns.random()
    val patternStrength = Random.nextDouble(0.5, 0.9)
    
    insights.add(
        Insight(
            id = UUID.randomUUID().toString(),
            type = InsightType.CLASSIFICATION_PATTERN,
            title = "Classification Pattern Identified",
            description = selectedPattern + " detected with ${(patternStrength * 100).toInt()}% confidence",
            category = "Classification",
            priority = InsightPriority.NORMAL,
            confidence = patternStrength,
            impact = InsightImpact.MEDIUM,
            timeframe = request.timeframe,
            generatedAt = LocalDateTime.now().toString(),
            data = InsightData(
                metrics = mapOf(
                    "pattern_strength" to patternStrength,
                    "classification_accuracy" to Random.nextDouble(0.8, 0.95),
                    "sample_size" to Random.nextDouble(200.0, 2000.0)
                ),
                trends = mapOf(
                    "classification_accuracy" to generateMockDataPoints(request.timeframe)
                ),
                comparisons = emptyMap(),
                predictions = emptyMap()
            ),
            actionableRecommendations = generateClassificationRecommendations(selectedPattern),
            userId = request.userId
        )
    )
    
    return insights
}

fun InsightsServiceImpl.generateUserBehaviorInsights(request: InsightRequest): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    if (request.userId != null) {
        val behaviors = listOf("Increased posting frequency", "Changed content preferences", "Engagement pattern shift")
        val selectedBehavior = behaviors.random()
        val behaviorScore = Random.nextDouble(0.6, 0.9)
        
        insights.add(
            Insight(
                id = UUID.randomUUID().toString(),
                type = InsightType.USER_BEHAVIOR,
                title = "User Behavior Pattern",
                description = selectedBehavior + " observed for user ${request.userId}",
                category = "User Behavior",
                priority = InsightPriority.LOW,
                confidence = behaviorScore,
                impact = InsightImpact.LOW,
                timeframe = request.timeframe,
                generatedAt = LocalDateTime.now().toString(),
                data = InsightData(
                    metrics = mapOf(
                        "behavior_score" to behaviorScore,
                        "frequency_change" to Random.nextDouble(-0.5, 0.5),
                        "engagement_change" to Random.nextDouble(-0.3, 0.3)
                    ),
                    trends = mapOf(
                        "user_activity" to generateMockDataPoints(request.timeframe)
                    ),
                    comparisons = emptyMap(),
                    predictions = emptyMap()
                ),
                actionableRecommendations = generateUserBehaviorRecommendations(selectedBehavior),
                userId = request.userId
            )
        )
    }
    
    return insights
}

fun InsightsServiceImpl.generateContentQualityInsights(request: InsightRequest): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    val qualityScore = Random.nextDouble(0.6, 0.95)
    val qualityTrend = Random.nextDouble(-0.2, 0.3)
    
    val priority = when {
        qualityScore < 0.7 -> InsightPriority.HIGH
        qualityScore < 0.8 -> InsightPriority.NORMAL
        else -> InsightPriority.LOW
    }
    
    insights.add(
        Insight(
            id = UUID.randomUUID().toString(),
            type = InsightType.CONTENT_QUALITY,
            title = "Content Quality Analysis",
            description = "Overall content quality score: ${(qualityScore * 100).toInt()}%",
            category = "Content Quality",
            priority = priority,
            confidence = 0.85,
            impact = if (qualityScore < 0.7) InsightImpact.HIGH else InsightImpact.MEDIUM,
            timeframe = request.timeframe,
            generatedAt = LocalDateTime.now().toString(),
            data = InsightData(
                metrics = mapOf(
                    "quality_score" to qualityScore,
                    "quality_trend" to qualityTrend,
                    "readability_score" to Random.nextDouble(0.6, 0.9),
                    "engagement_correlation" to Random.nextDouble(0.4, 0.8)
                ),
                trends = mapOf(
                    "quality_over_time" to generateMockDataPoints(request.timeframe)
                ),
                comparisons = emptyMap(),
                predictions = emptyMap()
            ),
            actionableRecommendations = generateContentQualityRecommendations(qualityScore),
            userId = request.userId
        )
    )
    
    return insights
}

fun InsightsServiceImpl.generateAnomalyDetectionInsights(request: InsightRequest): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    // Simulate anomaly detection
    if (Random.nextDouble() < 0.3) { // 30% chance of anomaly
        val anomalyTypes = listOf("Unusual posting pattern", "Spam spike detected", "Engagement anomaly")
        val selectedAnomaly = anomalyTypes.random()
        val severity = listOf("low", "medium", "high").random()
        
        val priority = when (severity) {
            "high" -> InsightPriority.URGENT
            "medium" -> InsightPriority.HIGH
            else -> InsightPriority.NORMAL
        }
        
        insights.add(
            Insight(
                id = UUID.randomUUID().toString(),
                type = InsightType.ANOMALY_DETECTION,
                title = "Anomaly Detected",
                description = "$selectedAnomaly with $severity severity",
                category = "Anomalies",
                priority = priority,
                confidence = Random.nextDouble(0.7, 0.95),
                impact = when (severity) {
                    "high" -> InsightImpact.CRITICAL
                    "medium" -> InsightImpact.HIGH
                    else -> InsightImpact.MEDIUM
                },
                timeframe = request.timeframe,
                generatedAt = LocalDateTime.now().toString(),
                data = InsightData(
                    metrics = mapOf(
                        "anomaly_score" to Random.nextDouble(0.7, 1.0),
                        "deviation" to Random.nextDouble(2.0, 5.0),
                        "affected_users" to Random.nextDouble(1.0, 100.0)
                    ),
                    trends = mapOf(
                        "anomaly_timeline" to generateMockDataPoints("24h")
                    ),
                    comparisons = emptyMap(),
                    predictions = emptyMap(),
                    anomalies = listOf(
                        AnomalyData(
                            timestamp = LocalDateTime.now().toString(),
                            value = Random.nextDouble(50.0, 200.0),
                            expectedValue = Random.nextDouble(10.0, 50.0),
                            severity = severity,
                            description = selectedAnomaly
                        )
                    )
                ),
                actionableRecommendations = generateAnomalyRecommendations(selectedAnomaly, severity),
                userId = request.userId
            )
        )
    }
    
    return insights
}

fun generateMockDataPoints(timeframe: String): List<DataPoint> {
    val points = when (timeframe) {
        "1h" -> 12 // 5-minute intervals
        "24h" -> 24 // hourly
        "7d" -> 7 // daily
        "30d" -> 30 // daily
        else -> 10
    }
    
    return (1..points).map { i ->
        DataPoint(
            timestamp = LocalDateTime.now().minusHours((points - i).toLong()).toString(),
            value = Random.nextDouble(0.0, 100.0),
            label = "Point $i"
        )
    }
}

fun generateSentimentRecommendations(sentimentTrend: Double): List<Recommendation> {
    return if (sentimentTrend < -0.2) {
        listOf(
            Recommendation(
                id = UUID.randomUUID().toString(),
                title = "Improve Content Moderation",
                description = "Increase moderation efforts to address negative sentiment",
                actionType = ActionType.MODERATION_ACTION,
                priority = InsightPriority.HIGH,
                estimatedImpact = "High",
                implementationEffort = "Medium",
                category = "Content Management"
            )
        )
    } else {
        listOf(
            Recommendation(
                id = UUID.randomUUID().toString(),
                title = "Maintain Current Strategy",
                description = "Continue current content strategy as sentiment is stable/positive",
                actionType = ActionType.CONTENT_OPTIMIZATION,
                priority = InsightPriority.LOW,
                estimatedImpact = "Medium",
                implementationEffort = "Low",
                category = "Content Management"
            )
        )
    }
}

fun generateTopicRecommendations(topic: String): List<Recommendation> {
    return listOf(
        Recommendation(
            id = UUID.randomUUID().toString(),
            title = "Capitalize on Trending Topic",
            description = "Create more content around '$topic' to leverage its popularity",
            actionType = ActionType.CONTENT_OPTIMIZATION,
            priority = InsightPriority.NORMAL,
            estimatedImpact = "High",
            implementationEffort = "Medium",
            category = "Content Strategy"
        )
    )
}

fun generateClassificationRecommendations(pattern: String): List<Recommendation> {
    return listOf(
        Recommendation(
            id = UUID.randomUUID().toString(),
            title = "Optimize Classification Model",
            description = "Fine-tune classification algorithms based on detected pattern: $pattern",
            actionType = ActionType.ALGORITHM_TUNING,
            priority = InsightPriority.NORMAL,
            estimatedImpact = "Medium",
            implementationEffort = "High",
            category = "Algorithm Optimization"
        )
    )
}

fun generateUserBehaviorRecommendations(behavior: String): List<Recommendation> {
    return listOf(
        Recommendation(
            id = UUID.randomUUID().toString(),
            title = "Personalize User Experience",
            description = "Adjust user experience based on behavior pattern: $behavior",
            actionType = ActionType.USER_ENGAGEMENT,
            priority = InsightPriority.LOW,
            estimatedImpact = "Medium",
            implementationEffort = "Medium",
            category = "User Experience"
        )
    )
}

fun generateContentQualityRecommendations(qualityScore: Double): List<Recommendation> {
    return if (qualityScore < 0.7) {
        listOf(
            Recommendation(
                id = UUID.randomUUID().toString(),
                title = "Improve Content Guidelines",
                description = "Update content guidelines and provide user education to improve quality",
                actionType = ActionType.CONTENT_OPTIMIZATION,
                priority = InsightPriority.HIGH,
                estimatedImpact = "High",
                implementationEffort = "Medium",
                category = "Content Quality"
            )
        )
    } else {
        listOf(
            Recommendation(
                id = UUID.randomUUID().toString(),
                title = "Maintain Quality Standards",
                description = "Continue current quality assurance processes",
                actionType = ActionType.CONTENT_OPTIMIZATION,
                priority = InsightPriority.LOW,
                estimatedImpact = "Medium",
                implementationEffort = "Low",
                category = "Content Quality"
            )
        )
    }
}

fun generateAnomalyRecommendations(anomaly: String, severity: String): List<Recommendation> {
    val priority = when (severity) {
        "high" -> InsightPriority.URGENT
        "medium" -> InsightPriority.HIGH
        else -> InsightPriority.NORMAL
    }
    
    return listOf(
        Recommendation(
            id = UUID.randomUUID().toString(),
            title = "Investigate Anomaly",
            description = "Immediate investigation required for: $anomaly",
            actionType = ActionType.INVESTIGATION,
            priority = priority,
            estimatedImpact = "Critical",
            implementationEffort = "High",
            category = "Security & Monitoring"
        )
    )
}
