package com.android.tripbook.algorithm.textmining

import com.android.tripbook.data.model.ProcessedText
import com.android.tripbook.data.repository.TextMiningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class TextMiningService @Inject constructor(
    private val repository: TextMiningRepository,
    private val tokenizer: Tokenizer,
    private val entityExtractor: EntityExtractor,
    private val sentimentAnalyzer: SentimentAnalyzer,
    private val keywordExtractor: KeywordExtractor
) {
    suspend fun processTextBatch(batchSize: Int = 50) = withContext(Dispatchers.Default) {
        val unprocessedTexts = repository.getUnprocessedTextBatch(batchSize)
        
        unprocessedTexts.forEach { rawTextData ->
            val processedText = processText(rawTextData.content)
            repository.saveProcessedText(
                ProcessedText(
                    id = UUID.randomUUID().toString(),
                    rawTextId = rawTextData.id,
                    tokens = processedText.tokens,
                    entities = processedText.entities,
                    keywords = processedText.keywords,
                    sentiment = processedText.sentiment,
                    timestamp = System.currentTimeMillis(),
                    isClassified = false
                )
            )
            repository.markRawTextAsProcessed(rawTextData.id)
        }
    }
    
    private fun processText(text: String): ProcessedTextResult {
        // Tokenize text
        val tokens = tokenizer.tokenize(text)
        
        // Extract entities
        val entities = entityExtractor.extractEntities(text)
        
        // Analyze sentiment
        val sentiment = sentimentAnalyzer.analyzeSentiment(text)
        
        // Extract keywords
        val keywords = keywordExtractor.extractKeywords(text, tokens)
        
        return ProcessedTextResult(tokens, entities, keywords, sentiment)
    }
    
    data class ProcessedTextResult(
        val tokens: List<String>,
        val entities: List<com.android.tripbook.data.model.Entity>,
        val keywords: List<String>,
        val sentiment: com.android.tripbook.data.model.Sentiment
    )
}





