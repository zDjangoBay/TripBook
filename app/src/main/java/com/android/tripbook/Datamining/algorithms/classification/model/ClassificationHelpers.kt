package com.android.algorithms.classification.model

import com.android.algorithms.textmining.model.TextAnalysisResult
import kotlin.math.abs

// Extension functions for ClassificationServiceImpl

fun ClassificationServiceImpl.extractSentimentFeatures(text: String, textAnalysis: TextAnalysisResult?): SentimentFeatures {
    val sentimentScore = textAnalysis?.sentiment?.score ?: 0.0
    val sentimentMagnitude = textAnalysis?.sentiment?.magnitude ?: 0.0
    
    // Emotion scores (simplified)
    val emotionScores = mapOf(
        "joy" to text.lowercase().split(Regex("\\W+")).count { it in listOf("happy", "joy", "excited", "great") }.toDouble(),
        "anger" to text.lowercase().split(Regex("\\W+")).count { it in listOf("angry", "mad", "furious", "hate") }.toDouble(),
        "fear" to text.lowercase().split(Regex("\\W+")).count { it in listOf("scared", "afraid", "worried", "anxious") }.toDouble(),
        "sadness" to text.lowercase().split(Regex("\\W+")).count { it in listOf("sad", "depressed", "disappointed", "upset") }.toDouble()
    )
    
    val subjectivityScore = (emotionScores.values.sum() / text.split(Regex("\\W+")).size).coerceIn(0.0, 1.0)
    
    return SentimentFeatures(
        sentimentScore = sentimentScore,
        sentimentMagnitude = sentimentMagnitude,
        emotionScores = emotionScores,
        subjectivityScore = subjectivityScore
    )
}

fun ClassificationServiceImpl.extractTopicFeatures(text: String, textAnalysis: TextAnalysisResult?): TopicFeatures {
    val topics = textAnalysis?.topics ?: emptyList()
    val topicDistribution = topics.associate { it.name to it.confidence }
    
    // Domain-specific terms
    val travelTerms = listOf("travel", "trip", "vacation", "hotel", "flight")
    val techTerms = listOf("technology", "software", "app", "digital", "computer")
    val businessTerms = listOf("business", "work", "company", "meeting", "project")
    
    val textLower = text.lowercase()
    val domainTerms = mapOf(
        "travel" to travelTerms.count { textLower.contains(it) }.toDouble(),
        "technology" to techTerms.count { textLower.contains(it) }.toDouble(),
        "business" to businessTerms.count { textLower.contains(it) }.toDouble()
    )
    
    val conceptDensity = (domainTerms.values.sum() / text.split(Regex("\\W+")).size).coerceIn(0.0, 1.0)
    
    return TopicFeatures(
        topicDistribution = topicDistribution,
        domainSpecificTerms = domainTerms,
        conceptDensity = conceptDensity
    )
}

fun ClassificationServiceImpl.extractStructuralFeatures(text: String): StructuralFeatures {
    val sentences = text.split(Regex("[.!?]+")).filter { it.isNotBlank() }
    val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
    
    return StructuralFeatures(
        textLength = text.length.toDouble(),
        sentenceCount = sentences.size.toDouble(),
        averageSentenceLength = if (sentences.isNotEmpty()) words.size.toDouble() / sentences.size else 0.0,
        punctuationDensity = text.count { it in ".,!?;:" }.toDouble() / text.length,
        capitalizationRatio = text.count { it.isUpperCase() }.toDouble() / text.length,
        questionMarkCount = text.count { it == '?' }.toDouble(),
        exclamationMarkCount = text.count { it == '!' }.toDouble()
    )
}

fun ClassificationServiceImpl.performClassification(text: String, features: FeatureVector, modelType: ClassificationModelType): List<Classification> {
    // Simplified classification logic - in production, use trained ML models
    return when (modelType) {
        ClassificationModelType.SENTIMENT_SPECIFIC -> classifySentiment(features)
        ClassificationModelType.TOPIC_SPECIFIC -> classifyTopic(features)
        ClassificationModelType.INTENT_CLASSIFICATION -> classifyIntent(text, features)
        ClassificationModelType.SPAM_DETECTION -> classifySpam(text, features)
        ClassificationModelType.CONTENT_MODERATION -> classifyContent(text, features)
        ClassificationModelType.GENERAL -> classifyGeneral(text, features)
    }
}

fun ClassificationServiceImpl.classifySentiment(features: FeatureVector): List<Classification> {
    val sentimentScore = features.sentimentFeatures.sentimentScore
    val confidence = abs(sentimentScore).coerceIn(0.5, 1.0)
    
    val category = when {
        sentimentScore > 0.2 -> "Positive"
        sentimentScore < -0.2 -> "Negative"
        else -> "Neutral"
    }
    
    return listOf(
        Classification(
            category = category,
            confidence = confidence,
            probability = confidence,
            reasoning = "Based on sentiment analysis score: $sentimentScore"
        )
    )
}

fun ClassificationServiceImpl.classifyTopic(features: FeatureVector): List<Classification> {
    val topicDistribution = features.topicFeatures.topicDistribution
    
    return topicDistribution.map { (topic, confidence) ->
        Classification(
            category = topic,
            confidence = confidence,
            probability = confidence,
            reasoning = "Topic modeling confidence: $confidence"
        )
    }.sortedByDescending { it.confidence }
}

fun ClassificationServiceImpl.classifyIntent(text: String, features: FeatureVector): List<Classification> {
    val textLower = text.lowercase()
    
    val intent = when {
        textLower.contains("book") || textLower.contains("reserve") -> "Booking"
        textLower.contains("cancel") || textLower.contains("refund") -> "Cancellation"
        textLower.contains("help") || textLower.contains("support") -> "Support"
        textLower.contains("information") || textLower.contains("details") -> "Information"
        else -> "General"
    }
    
    val confidence = when (intent) {
        "General" -> 0.6
        else -> 0.8
    }
    
    return listOf(
        Classification(
            category = intent,
            confidence = confidence,
            probability = confidence,
            reasoning = "Intent classification based on keyword analysis"
        )
    )
}

fun ClassificationServiceImpl.classifySpam(text: String, features: FeatureVector): List<Classification> {
    val spamIndicators = listOf("free", "win", "prize", "urgent", "limited time", "click now")
    val spamScore = spamIndicators.count { text.lowercase().contains(it) }.toDouble() / spamIndicators.size
    
    val isSpam = spamScore > 0.3 || features.structuralFeatures.exclamationMarkCount > 3
    val confidence = if (isSpam) 0.8 + spamScore * 0.2 else 0.9 - spamScore * 0.3
    
    return listOf(
        Classification(
            category = if (isSpam) "Spam" else "Not Spam",
            confidence = confidence.coerceIn(0.5, 1.0),
            probability = if (isSpam) spamScore else 1.0 - spamScore,
            reasoning = "Spam detection based on keyword analysis and structural features"
        )
    )
}

fun ClassificationServiceImpl.classifyContent(text: String, features: FeatureVector): List<Classification> {
    val inappropriateWords = listOf("hate", "violence", "inappropriate") // Simplified list
    val inappropriateScore = inappropriateWords.count { text.lowercase().contains(it) }.toDouble()
    
    val isAppropriate = inappropriateScore == 0.0
    val confidence = if (isAppropriate) 0.9 else 0.8
    
    return listOf(
        Classification(
            category = if (isAppropriate) "Appropriate" else "Inappropriate",
            confidence = confidence,
            probability = if (isAppropriate) 0.9 else inappropriateScore / inappropriateWords.size,
            reasoning = "Content moderation based on keyword filtering"
        )
    )
}

fun ClassificationServiceImpl.classifyGeneral(text: String, features: FeatureVector): List<Classification> {
    // General classification combining multiple aspects
    val categories = mutableListOf<Classification>()
    
    // Add sentiment classification
    categories.addAll(classifySentiment(features))
    
    // Add topic classification if available
    if (features.topicFeatures.topicDistribution.isNotEmpty()) {
        categories.addAll(classifyTopic(features).take(2)) // Top 2 topics
    }
    
    return categories.sortedByDescending { it.confidence }.take(3)
}
