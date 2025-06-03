package com.android.tripbook.data.model

// Raw text data
data class RawTextData(
    val id: String,
    val userId: String,
    val content: String,
    val source: String,
    val timestamp: Long,
    val isProcessed: Boolean = false
)

// Processed text
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

// Classification
data class ClassifiedData(
    val id: String,
    val processedTextId: String,
    val categories: List<Category>,
    val travelPreferences: TravelPreferences,
    val timestamp: Long
)

data class Category(
    val name: String,
    val confidence: Float
)

data class TravelPreferences(
    val transportPreference: TransportType,
    val accommodationPreference: AccommodationType,
    val budgetLevel: BudgetLevel,
    val interestTags: List<String>,
    val confidence: Float
)

enum class TransportType {
    PUBLIC_BUS, PRIVATE_CAR, TRAIN, FLIGHT, BOAT, UNKNOWN
}

enum class AccommodationType {
    HOTEL, HOSTEL, RESORT, AIRBNB, CAMPING, UNKNOWN
}

enum class BudgetLevel {
    BUDGET, MID_RANGE, LUXURY, UNKNOWN
}

// Insights
data class TravelInsight(
    val id: String,
    val userId: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val relevanceScore: Float,
    val timestamp: Long,
    val isDelivered: Boolean = false
)

enum class InsightType {
    DESTINATION_RECOMMENDATION,
    TRIP_SCHEDULING_SUGGESTION,
    TRAVEL_TREND,
    BUDGET_TIP,
    SAFETY_ALERT
}




