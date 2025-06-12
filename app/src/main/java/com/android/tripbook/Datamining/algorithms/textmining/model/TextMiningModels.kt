package com.android.algorithms.textmining.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TextAnalysisRequest(
    val text: String,
    val source: String, // "post", "comment", "review", etc.
    val sourceId: String,
    val userId: String? = null,
    val language: String = "en"
)

@Serializable
data class TextAnalysisResult(
    val id: String,
    val originalText: String,
    val source: String,
    val sourceId: String,
    val userId: String?,
    val language: String,
    val processedAt: String,
    val sentiment: SentimentAnalysis,
    val entities: List<NamedEntity>,
    val keywords: List<Keyword>,
    val topics: List<Topic>,
    val textStatistics: TextStatistics,
    val isProcessed: Boolean = true
)

@Serializable
data class SentimentAnalysis(
    val score: Double, // -1.0 to 1.0 (negative to positive)
    val magnitude: Double, // 0.0 to infinity (intensity)
    val label: SentimentLabel,
    val confidence: Double // 0.0 to 1.0
)

@Serializable
enum class SentimentLabel {
    VERY_NEGATIVE,
    NEGATIVE,
    NEUTRAL,
    POSITIVE,
    VERY_POSITIVE
}

@Serializable
data class NamedEntity(
    val text: String,
    val type: EntityType,
    val confidence: Double,
    val startOffset: Int,
    val endOffset: Int
)

@Serializable
enum class EntityType {
    PERSON,
    LOCATION,
    ORGANIZATION,
    EVENT,
    WORK_OF_ART,
    CONSUMER_GOOD,
    OTHER,
    PHONE_NUMBER,
    ADDRESS,
    DATE,
    NUMBER,
    PRICE
}

@Serializable
data class Keyword(
    val word: String,
    val frequency: Int,
    val relevance: Double, // 0.0 to 1.0
    val partOfSpeech: String? = null
)

@Serializable
data class Topic(
    val name: String,
    val confidence: Double,
    val keywords: List<String>
)

@Serializable
data class TextStatistics(
    val wordCount: Int,
    val characterCount: Int,
    val sentenceCount: Int,
    val averageWordsPerSentence: Double,
    val readabilityScore: Double, // Flesch Reading Ease Score
    val languageDetected: String
)

@Serializable
data class BatchTextAnalysisRequest(
    val texts: List<TextAnalysisRequest>,
    val priority: ProcessingPriority = ProcessingPriority.NORMAL
)

@Serializable
enum class ProcessingPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

@Serializable
data class TextMiningStats(
    val totalTextsProcessed: Long,
    val averageProcessingTime: Double, // in milliseconds
    val sentimentDistribution: Map<String, Int>, // Changed from SentimentLabel to String
    val topEntities: List<EntityFrequency>,
    val topKeywords: List<KeywordFrequency>,
    val languageDistribution: Map<String, Int>
)

@Serializable
data class EntityFrequency(
    val entity: String,
    val type: EntityType,
    val frequency: Int
)

@Serializable
data class KeywordFrequency(
    val keyword: String,
    val frequency: Int,
    val averageRelevance: Double
)

// Response classes
@Serializable
data class TextMiningResponse(
    val success: Boolean,
    val message: String,
    val data: TextAnalysisResult? = null,
    val error: String? = null
)

@Serializable
data class TextMiningListResponse(
    val success: Boolean,
    val message: String,
    val data: List<TextAnalysisResult>,
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalCount: Int? = null,
    val hasMore: Boolean? = null,
    val error: String? = null
)

@Serializable
data class TextMiningStatsResponse(
    val success: Boolean,
    val message: String,
    val data: TextMiningStats,
    val error: String? = null
)

@Serializable
data class TrendingTopicsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Topic>,
    val timeframe: String,
    val limit: Int,
    val error: String? = null
)

@Serializable
data class SentimentTrendsResponse(
    val success: Boolean,
    val message: String,
    val data: Map<String, Map<String, Int>>, // Changed from SentimentLabel to String
    val timeframe: String,
    val error: String? = null
)
