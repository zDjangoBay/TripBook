package com.android.tripbook.algorithm.textmining

import com.android.tripbook.data.model.Entity
import com.android.tripbook.data.model.EntityType
import com.android.tripbook.data.model.Sentiment
import javax.inject.Inject

class Tokenizer @Inject constructor() {
    fun tokenize(text: String): List<String> {
        // Simple tokenization by splitting on whitespace and removing punctuation
        return text.lowercase()
            .replace("[^\\p{L}\\p{Nd}\\s]".toRegex(), " ")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
    }
}

class EntityExtractor @Inject constructor() {
    fun extractEntities(text: String): List<Entity> {
        val entities = mutableListOf<Entity>()
        
        // Simple location detection (cities, countries)
        val locations = listOf("paris", "london", "tokyo", "new york", "rome", "bali", "thailand", "japan", "usa", "italy")
        locations.forEach { location ->
            if (text.lowercase().contains(location)) {
                entities.add(Entity(location, EntityType.LOCATION, 0.8f))
            }
        }
        
        // Simple transport detection
        val transports = mapOf(
            "bus" to EntityType.TRANSPORT,
            "car" to EntityType.TRANSPORT,
            "train" to EntityType.TRANSPORT,
            "plane" to EntityType.TRANSPORT,
            "flight" to EntityType.TRANSPORT,
            "boat" to EntityType.TRANSPORT
        )
        transports.forEach { (transport, type) ->
            if (text.lowercase().contains(transport)) {
                entities.add(Entity(transport, type, 0.8f))
            }
        }
        
        // Simple accommodation detection
        val accommodations = mapOf(
            "hotel" to EntityType.ACCOMMODATION,
            "hostel" to EntityType.ACCOMMODATION,
            "airbnb" to EntityType.ACCOMMODATION,
            "resort" to EntityType.ACCOMMODATION,
            "camping" to EntityType.ACCOMMODATION
        )
        accommodations.forEach { (accommodation, type) ->
            if (text.lowercase().contains(accommodation)) {
                entities.add(Entity(accommodation, type, 0.8f))
            }
        }
        
        return entities
    }
}

class SentimentAnalyzer @Inject constructor() {
    fun analyzeSentiment(text: String): Sentiment {
        // Simple sentiment analysis based on positive and negative word counts
        val positiveWords = listOf("good", "great", "excellent", "amazing", "wonderful", "beautiful", "enjoy", "love", "happy", "best")
        val negativeWords = listOf("bad", "terrible", "awful", "horrible", "poor", "worst", "hate", "dislike", "disappointed", "negative")
        
        val words = text.lowercase().split("\\s+".toRegex())
        
        val positiveCount = words.count { it in positiveWords }
        val negativeCount = words.count { it in negativeWords }
        
        val totalWords = words.size.toFloat()
        val score = if (totalWords > 0) {
            (positiveCount - negativeCount) / totalWords
        } else {
            0f
        }
        
        // Magnitude is a simple measure of how many sentiment words were found
        val magnitude = (positiveCount + negativeCount) / if (totalWords > 0) totalWords else 1f
        
        return Sentiment(score, magnitude)
    }
}

class KeywordExtractor @Inject constructor() {
    fun extractKeywords(text: String, tokens: List<String>): List<String> {
        // Simple keyword extraction based on frequency and stopword filtering
        val stopwords = listOf("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "with", "by", "about", "like", "through", "over", "before", "after", "since", "during", "above", "below", "from", "up", "down", "of", "is", "am", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did", "will", "would", "shall", "should", "may", "might", "must", "can", "could")
        
        // Filter out stopwords
        val filteredTokens = tokens.filter { it !in stopwords && it.length > 2 }
        
        // Count token frequencies
        val tokenFrequencies = filteredTokens.groupingBy { it }.eachCount()
        
        // Get top keywords (up to 10)
        return tokenFrequencies.entries
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key }
    }
}
