package com.android.algorithms.textmining.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.descending
import redis.clients.jedis.Jedis
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.*

class TextMiningServiceImpl(
    private val mongoDb: CoroutineDatabase,
    private val redis: Jedis,
    private val jsonMapper: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = false }
) : TextMiningService {

    private val textAnalysisCollection: CoroutineCollection<TextAnalysisResult> = mongoDb.getCollection("TextAnalyses")

    override suspend fun analyzeText(request: TextAnalysisRequest): TextAnalysisResult? {
        try {
            val analysisId = UUID.randomUUID().toString()

            // Perform NLP analysis (simulated - in real implementation, use ML Kit or TensorFlow Lite)
            val sentiment = analyzeSentiment(request.text)
            val entities = extractNamedEntities(request.text)
            val keywords = extractKeywords(request.text)
            val topics = extractTopics(request.text)
            val statistics = calculateTextStatistics(request.text)

            val result = TextAnalysisResult(
                id = analysisId,
                originalText = request.text,
                source = request.source,
                sourceId = request.sourceId,
                userId = request.userId,
                language = request.language,
                processedAt = LocalDateTime.now().toString(),
                sentiment = sentiment,
                entities = entities,
                keywords = keywords,
                topics = topics,
                textStatistics = statistics,
                isProcessed = true
            )

            // Store in MongoDB
            val insertResult = textAnalysisCollection.insertOne(result)
            if (!insertResult.wasAcknowledged()) {
                println("Failed to store text analysis result")
                return null
            }

            // Cache in Redis
            val resultJson = jsonMapper.encodeToString(result)
            redis.setex("text_analysis:$analysisId", 3600, resultJson)

            return result
        } catch (e: Exception) {
            println("Error analyzing text: ${e.message}")
            return null
        }
    }

    override suspend fun batchAnalyzeTexts(request: BatchTextAnalysisRequest): List<TextAnalysisResult> {
        val results = mutableListOf<TextAnalysisResult>()

        for (textRequest in request.texts) {
            val result = analyzeText(textRequest)
            if (result != null) {
                results.add(result)
            }
        }

        return results
    }

    override suspend fun getAnalysisById(analysisId: String): TextAnalysisResult? {
        try {
            // Check cache first
            val cachedResult = redis.get("text_analysis:$analysisId")
            if (cachedResult != null) {
                return jsonMapper.decodeFromString<TextAnalysisResult>(cachedResult)
            }

            // Get from database
            val result = textAnalysisCollection.findOne(TextAnalysisResult::id eq analysisId)

            if (result != null) {
                // Cache the result
                val resultJson = jsonMapper.encodeToString(result)
                redis.setex("text_analysis:$analysisId", 3600, resultJson)
            }

            return result
        } catch (e: Exception) {
            println("Error getting analysis $analysisId: ${e.message}")
            return null
        }
    }

    override suspend fun getAnalysesBySource(source: String, sourceId: String): List<TextAnalysisResult> {
        try {
            return textAnalysisCollection.find(
                org.litote.kmongo.and(
                    TextAnalysisResult::source eq source,
                    TextAnalysisResult::sourceId eq sourceId
                )
            ).sort(descending(TextAnalysisResult::processedAt)).toList()
        } catch (e: Exception) {
            println("Error getting analyses by source: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getAnalysesByUser(userId: String, page: Int, pageSize: Int): List<TextAnalysisResult> {
        try {
            val skip = (page - 1) * pageSize
            return textAnalysisCollection.find(TextAnalysisResult::userId eq userId)
                .sort(descending(TextAnalysisResult::processedAt))
                .skip(skip)
                .limit(pageSize)
                .toList()
        } catch (e: Exception) {
            println("Error getting analyses by user: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getAnalysesBySentiment(sentiment: SentimentLabel, page: Int, pageSize: Int): List<TextAnalysisResult> {
        try {
            val skip = (page - 1) * pageSize
            return textAnalysisCollection.find()
                .sort(descending(TextAnalysisResult::processedAt))
                .skip(skip)
                .limit(pageSize)
                .toList()
                .filter { it.sentiment.label == sentiment }
        } catch (e: Exception) {
            println("Error getting analyses by sentiment: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun searchAnalysesByKeywords(keywords: List<String>, page: Int, pageSize: Int): List<TextAnalysisResult> {
        try {
            val skip = (page - 1) * pageSize
            // This is a simplified search - in production, use text indexing
            return textAnalysisCollection.find()
                .sort(descending(TextAnalysisResult::processedAt))
                .skip(skip)
                .limit(pageSize)
                .toList()
                .filter { analysis ->
                    keywords.any { keyword ->
                        analysis.originalText.contains(keyword, ignoreCase = true) ||
                        analysis.keywords.any { it.word.contains(keyword, ignoreCase = true) }
                    }
                }
        } catch (e: Exception) {
            println("Error searching analyses by keywords: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getTextMiningStats(): TextMiningStats {
        try {
            val allAnalyses = textAnalysisCollection.find().toList()

            // Handle empty database case
            if (allAnalyses.isEmpty()) {
                return TextMiningStats(
                    totalTextsProcessed = 0,
                    averageProcessingTime = 150.0,
                    sentimentDistribution = mapOf(
                        "POSITIVE" to 0,
                        "NEGATIVE" to 0,
                        "NEUTRAL" to 0
                    ),
                    topEntities = emptyList(),
                    topKeywords = emptyList(),
                    languageDistribution = mapOf("en" to 0)
                )
            }

            val sentimentDistribution = allAnalyses.groupBy { it.sentiment.label.name }
                .mapValues { it.value.size }

            val topEntities = try {
                allAnalyses.flatMap { it.entities }
                    .groupBy { "${it.text}:${it.type}" }
                    .map { (key, entities) ->
                        val parts = key.split(":")
                        EntityFrequency(
                            entity = parts[0],
                            type = EntityType.valueOf(parts[1]),
                            frequency = entities.size
                        )
                    }
                    .sortedByDescending { it.frequency }
                    .take(10)
            } catch (e: Exception) {
                emptyList()
            }

            val topKeywords = try {
                allAnalyses.flatMap { it.keywords }
                    .groupBy { it.word }
                    .map { (word, keywords) ->
                        KeywordFrequency(
                            keyword = word,
                            frequency = keywords.sumOf { it.frequency },
                            averageRelevance = keywords.map { it.relevance }.average()
                        )
                    }
                    .sortedByDescending { it.frequency }
                    .take(10)
            } catch (e: Exception) {
                emptyList()
            }

            val languageDistribution = allAnalyses.groupBy { it.language }
                .mapValues { it.value.size }

            return TextMiningStats(
                totalTextsProcessed = allAnalyses.size.toLong(),
                averageProcessingTime = 150.0, // Simulated
                sentimentDistribution = sentimentDistribution,
                topEntities = topEntities,
                topKeywords = topKeywords,
                languageDistribution = languageDistribution
            )
        } catch (e: Exception) {
            println("Error getting text mining stats: ${e.message}")
            return TextMiningStats(
                totalTextsProcessed = 0,
                averageProcessingTime = 150.0,
                sentimentDistribution = mapOf(
                    "POSITIVE" to 0,
                    "NEGATIVE" to 0,
                    "NEUTRAL" to 0
                ),
                topEntities = emptyList(),
                topKeywords = emptyList(),
                languageDistribution = mapOf("en" to 0)
            )
        }
    }

    override suspend fun deleteAnalysis(analysisId: String): Boolean {
        try {
            val deleteResult = textAnalysisCollection.deleteOne(TextAnalysisResult::id eq analysisId)
            if (deleteResult.deletedCount > 0) {
                redis.del("text_analysis:$analysisId")
                return true
            }
            return false
        } catch (e: Exception) {
            println("Error deleting analysis: ${e.message}")
            return false
        }
    }

    override suspend fun getTrendingTopics(timeframe: String, limit: Int): List<Topic> {
        try {
            // Simplified implementation - in production, filter by timeframe
            val allAnalyses = textAnalysisCollection.find()
                .sort(descending(TextAnalysisResult::processedAt))
                .limit(1000) // Recent analyses
                .toList()

            return allAnalyses.flatMap { it.topics }
                .groupBy { it.name }
                .map { (name, topics) ->
                    Topic(
                        name = name,
                        confidence = topics.map { it.confidence }.average(),
                        keywords = topics.flatMap { it.keywords }.distinct()
                    )
                }
                .sortedByDescending { it.confidence }
                .take(limit)
        } catch (e: Exception) {
            println("Error getting trending topics: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getSentimentTrends(timeframe: String): Map<String, Map<SentimentLabel, Int>> {
        try {
            // Simplified implementation - group by day
            val allAnalyses = textAnalysisCollection.find()
                .sort(descending(TextAnalysisResult::processedAt))
                .limit(1000)
                .toList()

            return allAnalyses.groupBy {
                it.processedAt.substring(0, 10) // Group by date (YYYY-MM-DD)
            }.mapValues { (_, analyses) ->
                analyses.groupBy { it.sentiment.label }
                    .mapValues { it.value.size }
            }
        } catch (e: Exception) {
            println("Error getting sentiment trends: ${e.message}")
            return emptyMap()
        }
    }

    // NLP Processing Methods (Simulated - replace with actual ML Kit/TensorFlow Lite implementation)

    private fun analyzeSentiment(text: String): SentimentAnalysis {
        // Simplified sentiment analysis - in production, use ML Kit or TensorFlow Lite
        val positiveWords = listOf("good", "great", "excellent", "amazing", "wonderful", "fantastic", "love", "like", "happy", "joy")
        val negativeWords = listOf("bad", "terrible", "awful", "hate", "dislike", "sad", "angry", "disappointed", "horrible", "worst")

        val words = text.lowercase().split(Regex("\\W+"))
        val positiveCount = words.count { it in positiveWords }
        val negativeCount = words.count { it in negativeWords }

        val score = when {
            positiveCount > negativeCount -> (positiveCount - negativeCount).toDouble() / words.size
            negativeCount > positiveCount -> -((negativeCount - positiveCount).toDouble() / words.size)
            else -> 0.0
        }.coerceIn(-1.0, 1.0)

        val magnitude = (positiveCount + negativeCount).toDouble() / words.size

        val label = when {
            score <= -0.6 -> SentimentLabel.VERY_NEGATIVE
            score <= -0.2 -> SentimentLabel.NEGATIVE
            score < 0.2 -> SentimentLabel.NEUTRAL
            score < 0.6 -> SentimentLabel.POSITIVE
            else -> SentimentLabel.VERY_POSITIVE
        }

        val confidence = abs(score).coerceIn(0.0, 1.0)

        return SentimentAnalysis(score, magnitude, label, confidence)
    }

    private fun extractNamedEntities(text: String): List<NamedEntity> {
        // Simplified named entity recognition - in production, use ML Kit or TensorFlow Lite
        val entities = mutableListOf<NamedEntity>()

        // Simple patterns for demonstration
        val emailPattern = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")
        val phonePattern = Regex("\\b\\d{3}-\\d{3}-\\d{4}\\b|\\b\\(\\d{3}\\)\\s*\\d{3}-\\d{4}\\b")
        val datePattern = Regex("\\b\\d{1,2}/\\d{1,2}/\\d{4}\\b|\\b\\d{4}-\\d{2}-\\d{2}\\b")
        val pricePattern = Regex("\\$\\d+(?:\\.\\d{2})?")

        // Find emails
        emailPattern.findAll(text).forEach { match ->
            entities.add(NamedEntity(
                text = match.value,
                type = EntityType.OTHER,
                confidence = 0.9,
                startOffset = match.range.first,
                endOffset = match.range.last
            ))
        }

        // Find phone numbers
        phonePattern.findAll(text).forEach { match ->
            entities.add(NamedEntity(
                text = match.value,
                type = EntityType.PHONE_NUMBER,
                confidence = 0.95,
                startOffset = match.range.first,
                endOffset = match.range.last
            ))
        }

        // Find dates
        datePattern.findAll(text).forEach { match ->
            entities.add(NamedEntity(
                text = match.value,
                type = EntityType.DATE,
                confidence = 0.8,
                startOffset = match.range.first,
                endOffset = match.range.last
            ))
        }

        // Find prices
        pricePattern.findAll(text).forEach { match ->
            entities.add(NamedEntity(
                text = match.value,
                type = EntityType.PRICE,
                confidence = 0.9,
                startOffset = match.range.first,
                endOffset = match.range.last
            ))
        }

        return entities
    }

    private fun extractKeywords(text: String): List<Keyword> {
        // Simplified keyword extraction - in production, use TF-IDF or more advanced methods
        val stopWords = setOf("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by", "is", "are", "was", "were", "be", "been", "have", "has", "had", "do", "does", "did", "will", "would", "could", "should", "may", "might", "can", "this", "that", "these", "those", "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them")

        val words = text.lowercase()
            .split(Regex("\\W+"))
            .filter { it.length > 2 && it !in stopWords }

        val wordFrequency = words.groupBy { it }.mapValues { it.value.size }
        val totalWords = words.size

        return wordFrequency.map { (word, frequency) ->
            Keyword(
                word = word,
                frequency = frequency,
                relevance = frequency.toDouble() / totalWords,
                partOfSpeech = "UNKNOWN" // In production, use POS tagging
            )
        }.sortedByDescending { it.relevance }.take(10)
    }

    private fun extractTopics(text: String): List<Topic> {
        // Simplified topic extraction - in production, use LDA or other topic modeling
        val travelKeywords = listOf("travel", "trip", "vacation", "hotel", "flight", "destination", "tourism", "journey")
        val foodKeywords = listOf("food", "restaurant", "meal", "cuisine", "dining", "recipe", "cooking", "eat")
        val techKeywords = listOf("technology", "software", "app", "digital", "computer", "internet", "mobile", "tech")
        val businessKeywords = listOf("business", "work", "company", "meeting", "project", "team", "office", "professional")

        val textLower = text.lowercase()
        val topics = mutableListOf<Topic>()

        val travelScore = travelKeywords.count { textLower.contains(it) }.toDouble() / travelKeywords.size
        if (travelScore > 0) {
            topics.add(Topic("Travel", travelScore, travelKeywords.filter { textLower.contains(it) }))
        }

        val foodScore = foodKeywords.count { textLower.contains(it) }.toDouble() / foodKeywords.size
        if (foodScore > 0) {
            topics.add(Topic("Food", foodScore, foodKeywords.filter { textLower.contains(it) }))
        }

        val techScore = techKeywords.count { textLower.contains(it) }.toDouble() / techKeywords.size
        if (techScore > 0) {
            topics.add(Topic("Technology", techScore, techKeywords.filter { textLower.contains(it) }))
        }

        val businessScore = businessKeywords.count { textLower.contains(it) }.toDouble() / businessKeywords.size
        if (businessScore > 0) {
            topics.add(Topic("Business", businessScore, businessKeywords.filter { textLower.contains(it) }))
        }

        return topics.sortedByDescending { it.confidence }
    }

    private fun calculateTextStatistics(text: String): TextStatistics {
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val sentences = text.split(Regex("[.!?]+")).filter { it.isNotBlank() }
        val characters = text.length

        val averageWordsPerSentence = if (sentences.isNotEmpty()) words.size.toDouble() / sentences.size else 0.0

        // Simplified Flesch Reading Ease Score
        val averageSentenceLength = averageWordsPerSentence
        val averageSyllablesPerWord = words.map { countSyllables(it) }.average()
        val readabilityScore = 206.835 - (1.015 * averageSentenceLength) - (84.6 * averageSyllablesPerWord)

        return TextStatistics(
            wordCount = words.size,
            characterCount = characters,
            sentenceCount = sentences.size,
            averageWordsPerSentence = averageWordsPerSentence,
            readabilityScore = readabilityScore.coerceIn(0.0, 100.0),
            languageDetected = "en" // Simplified - in production, use language detection
        )
    }

    private fun countSyllables(word: String): Double {
        // Simplified syllable counting
        val vowels = "aeiouy"
        var count = 0
        var previousWasVowel = false

        for (char in word.lowercase()) {
            val isVowel = char in vowels
            if (isVowel && !previousWasVowel) {
                count++
            }
            previousWasVowel = isVowel
        }

        // Handle silent 'e'
        if (word.lowercase().endsWith("e") && count > 1) {
            count--
        }

        return maxOf(1.0, count.toDouble())
    }
}