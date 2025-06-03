package com.android.tripbook.algorithm.classification

import com.android.tripbook.data.model.Category
import com.android.tripbook.data.model.ProcessedText
import com.android.tripbook.data.model.TravelPreferences
import com.android.tripbook.data.model.TransportType
import com.android.tripbook.data.model.AccommodationType
import com.android.tripbook.data.model.BudgetLevel
import javax.inject.Inject

class CategoryClassifier @Inject constructor() {
    fun classifyCategories(processedText: ProcessedText): List<Category> {
        val categories = mutableListOf<Category>()
        
        // Simple category classification based on keywords
        val categoryKeywords = mapOf(
            "adventure" to listOf("hiking", "climbing", "safari", "trek", "adventure", "expedition"),
            "beach" to listOf("beach", "ocean", "sea", "sand", "surf", "coast", "island"),
            "city" to listOf("city", "museum", "restaurant", "shopping", "urban", "downtown"),
            "culture" to listOf("history", "museum", "art", "culture", "traditional", "heritage"),
            "food" to listOf("food", "restaurant", "cuisine", "eat", "dining", "culinary", "taste"),
            "nature" to listOf("nature", "park", "wildlife", "forest", "hiking", "camping", "outdoor"),
            "religious" to listOf("church", "mosque", "temple", "monastery", "religious", "spiritual"),
            "sports" to listOf("sports", "gym", "stadium", "fitness", "athletic", "training"),
            "wildlife" to listOf("wildlife", "zoo", "animal", "conservation", "nature", "ecology")
        )
        
        categoryKeywords.forEach { (categoryName, keywords) ->
            if (keywords.any { it in processedText.keywords }) {
                categories.add(Category(categoryName, 0.8f))
            }
        }
        
        // Add default category if none found
        if (categories.isEmpty()) {
            categories.add(Category("General Travel", 0.6f))
        }
        
        return categories
    }
}

class PreferenceClassifier @Inject constructor() {
    fun classifyPreferences(processedText: ProcessedText): TravelPreferences {
        // Transport preference
        val transportType = classifyTransportPreference(processedText)
        
        // Accommodation preference
        val accommodationType = classifyAccommodationPreference(processedText)
        
        // Budget level
        val budgetLevel = classifyBudgetLevel(processedText)
        
        // Interest tags
        val interestTags = extractInterestTags(processedText)
        
        return TravelPreferences(
            transportPreference = transportType,
            accommodationPreference = accommodationType,
            budgetLevel = budgetLevel,
            interestTags = interestTags,
            confidence = 0.7f // Simplified confidence score
        )
    }
    
    private fun classifyTransportPreference(processedText: ProcessedText): TransportType {
        // Instead of filtering by EntityType, check all entities or just use the tokens
        val content = processedText.tokens.joinToString(" ")
        
        return when {
            content.contains("bus") -> TransportType.PUBLIC_BUS
            content.contains("car") -> TransportType.PRIVATE_CAR
            content.contains("train") -> TransportType.TRAIN
            content.contains("flight") || content.contains("plane") -> TransportType.FLIGHT
            content.contains("boat") || content.contains("ferry") -> TransportType.BOAT
            else -> TransportType.UNKNOWN
        }
    }
    
    private fun classifyAccommodationPreference(processedText: ProcessedText): AccommodationType {
        val content = processedText.tokens.joinToString(" ")
        
        return when {
            content.contains("hotel") -> AccommodationType.HOTEL
            content.contains("hostel") -> AccommodationType.HOSTEL
            content.contains("airbnb") || content.contains("apartment") -> AccommodationType.AIRBNB
            content.contains("camp") || content.contains("tent") -> AccommodationType.CAMPING
            content.contains("resort") -> AccommodationType.RESORT
            else -> AccommodationType.UNKNOWN
        }
    }
    
    private fun classifyBudgetLevel(processedText: ProcessedText): BudgetLevel {
        val content = processedText.tokens.joinToString(" ")
        
        // Budget indicators
        val budgetKeywords = listOf("cheap", "affordable", "budget", "inexpensive", "economical")
        val midRangeKeywords = listOf("moderate", "reasonable", "mid-range", "standard")
        val luxuryKeywords = listOf("luxury", "expensive", "high-end", "premium", "exclusive")
        
        return when {
            budgetKeywords.any { content.contains(it) } -> BudgetLevel.BUDGET
            luxuryKeywords.any { content.contains(it) } -> BudgetLevel.LUXURY
            midRangeKeywords.any { content.contains(it) } -> BudgetLevel.MID_RANGE
            else -> BudgetLevel.UNKNOWN
        }
    }
    
    private fun extractInterestTags(processedText: ProcessedText): List<String> {
        // Simplified interest extraction
        val interestKeywords = mapOf(
            "nature" to listOf("nature", "wildlife", "park", "mountain", "lake", "beach"),
            "history" to listOf("history", "museum", "monument", "ancient", "ruins"),
            "food" to listOf("food", "cuisine", "restaurant", "dining", "culinary"),
            "adventure" to listOf("adventure", "hiking", "safari", "climbing", "diving"),
            "relaxation" to listOf("relax", "spa", "resort", "peaceful", "quiet")
        )
        
        val content = processedText.tokens.joinToString(" ")
        return interestKeywords.filter { (_, keywords) ->
            keywords.any { content.contains(it) }
        }.keys.toList()
    }
}


