package com.android.tripbook.data.model

data class RawTextData(
    val id: String,
    val userId: String,
    val content: String,
    val source: String,
    val timestamp: Long,
    val isProcessed: Boolean = false
)

data class ProcessedText(
    val id: String,
    val rawTextId: String,
    val tokens: List<String>,
    val entities: List<Entity>,
    val keywords: List<String>,
    val sentiment: Sentiment,
    val timestamp: Long,
    val isClassified: Boolean = false
)

data class Entity(
    val text: String,
    val type: EntityType,
    val confidence: Float
)

enum class EntityType {
    LOCATION, PERSON, ORGANIZATION, DATE, TRANSPORT, ACCOMMODATION, ACTIVITY, MISC
}

data class Sentiment(
    val score: Float, // -1.0 to 1.0
    val magnitude: Float // 0.0 to +inf
)


