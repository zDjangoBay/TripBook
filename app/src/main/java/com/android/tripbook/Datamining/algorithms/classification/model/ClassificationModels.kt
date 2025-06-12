package com.android.algorithms.classification.model

import kotlinx.serialization.Serializable

@Serializable
data class ClassificationRequest(
    val textAnalysisId: String = "", // Reference to text analysis result (optional when text is provided)
    val text: String? = null, // Optional: direct text input
    val features: Map<String, Double> = emptyMap(), // Custom features
    val modelType: ClassificationModelType = ClassificationModelType.GENERAL,
    val userId: String? = null
)

@Serializable
enum class ClassificationModelType {
    GENERAL,
    SENTIMENT_SPECIFIC,
    TOPIC_SPECIFIC,
    INTENT_CLASSIFICATION,
    SPAM_DETECTION,
    CONTENT_MODERATION
}

@Serializable
data class ClassificationResult(
    val id: String,
    val textAnalysisId: String?,
    val originalText: String,
    val modelType: ClassificationModelType,
    val userId: String?,
    val processedAt: String,
    val classifications: List<Classification>,
    val features: FeatureVector,
    val confidence: Double,
    val isProcessed: Boolean = true
)

@Serializable
data class Classification(
    val category: String,
    val subcategory: String? = null,
    val confidence: Double,
    val probability: Double,
    val reasoning: String? = null
)

@Serializable
data class FeatureVector(
    val textFeatures: TextFeatures,
    val sentimentFeatures: SentimentFeatures,
    val topicFeatures: TopicFeatures,
    val structuralFeatures: StructuralFeatures,
    val customFeatures: Map<String, Double> = emptyMap()
)

@Serializable
data class TextFeatures(
    val tfidfVector: Map<String, Double>,
    val ngramFeatures: Map<String, Double>,
    val posTagDistribution: Map<String, Double>,
    val lexicalDiversity: Double,
    val averageWordLength: Double
)

@Serializable
data class SentimentFeatures(
    val sentimentScore: Double,
    val sentimentMagnitude: Double,
    val emotionScores: Map<String, Double>, // joy, anger, fear, sadness, etc.
    val subjectivityScore: Double
)

@Serializable
data class TopicFeatures(
    val topicDistribution: Map<String, Double>,
    val domainSpecificTerms: Map<String, Double>,
    val conceptDensity: Double
)

@Serializable
data class StructuralFeatures(
    val textLength: Double,
    val sentenceCount: Double,
    val averageSentenceLength: Double,
    val punctuationDensity: Double,
    val capitalizationRatio: Double,
    val questionMarkCount: Double,
    val exclamationMarkCount: Double
)

@Serializable
data class BatchClassificationRequest(
    val requests: List<ClassificationRequest>,
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
data class ModelTrainingRequest(
    val modelType: ClassificationModelType,
    val trainingData: List<TrainingExample>,
    val validationSplit: Double = 0.2,
    val hyperparameters: Map<String, String> = emptyMap()
)

@Serializable
data class TrainingExample(
    val text: String,
    val label: String,
    val features: Map<String, Double> = emptyMap()
)

@Serializable
data class ModelPerformance(
    val modelType: ClassificationModelType,
    val accuracy: Double,
    val precision: Map<String, Double>,
    val recall: Map<String, Double>,
    val f1Score: Map<String, Double>,
    val confusionMatrix: Map<String, Map<String, Int>>,
    val trainingDate: String,
    val sampleCount: Int
)

@Serializable
data class ClassificationStats(
    val totalClassifications: Long,
    val averageConfidence: Double,
    val categoryDistribution: Map<String, Int>,
    val modelTypeUsage: Map<String, Int>, // Changed from ClassificationModelType to String
    val averageProcessingTime: Double,
    val accuracyMetrics: Map<String, Double> // Changed from ClassificationModelType to String
)

@Serializable
data class FeatureImportance(
    val feature: String,
    val importance: Double,
    val category: String // "text", "sentiment", "topic", "structural"
)

@Serializable
data class ClassificationInsight(
    val category: String,
    val trend: String, // "increasing", "decreasing", "stable"
    val changePercentage: Double,
    val timeframe: String,
    val significance: Double,
    val description: String
)

@Serializable
data class CategoryTrend(
    val category: String,
    val count: Int
)

// Response classes
@Serializable
data class ClassificationResponse(
    val success: Boolean,
    val message: String,
    val data: ClassificationResult? = null,
    val error: String? = null
)

@Serializable
data class ClassificationListResponse(
    val success: Boolean,
    val message: String,
    val data: List<ClassificationResult>,
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalCount: Int? = null,
    val error: String? = null
)

@Serializable
data class ClassificationStatsResponse(
    val success: Boolean,
    val message: String,
    val data: ClassificationStats,
    val error: String? = null
)

@Serializable
data class FeatureImportanceResponse(
    val success: Boolean,
    val message: String,
    val data: List<FeatureImportance>,
    val error: String? = null
)

@Serializable
data class PredictionResponse(
    val success: Boolean,
    val message: String,
    val data: List<Classification>,
    val modelType: String,
    val error: String? = null
)

@Serializable
data class ClassificationInsightsResponse(
    val success: Boolean,
    val message: String,
    val data: List<ClassificationInsight>,
    val timeframe: String,
    val error: String? = null
)

@Serializable
data class TrendingCategoriesResponse(
    val success: Boolean,
    val message: String,
    val data: List<CategoryTrend>,
    val timeframe: String,
    val limit: Int,
    val error: String? = null
)

@Serializable
data class ModelTrainingResponse(
    val success: Boolean,
    val message: String,
    val data: ModelPerformance,
    val error: String? = null
)

@Serializable
data class ModelPerformanceResponse(
    val success: Boolean,
    val message: String,
    val data: ModelPerformance,
    val error: String? = null
)
