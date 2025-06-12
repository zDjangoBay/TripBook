package com.android.algorithms.textmining.model

interface TextMiningService {
    
    /**
     * Analyze a single text for sentiment, entities, keywords, and topics
     */
    suspend fun analyzeText(request: TextAnalysisRequest): TextAnalysisResult?
    
    /**
     * Batch process multiple texts
     */
    suspend fun batchAnalyzeTexts(request: BatchTextAnalysisRequest): List<TextAnalysisResult>
    
    /**
     * Get analysis result by ID
     */
    suspend fun getAnalysisById(analysisId: String): TextAnalysisResult?
    
    /**
     * Get all analyses for a specific source (e.g., all analyses for a post)
     */
    suspend fun getAnalysesBySource(source: String, sourceId: String): List<TextAnalysisResult>
    
    /**
     * Get analyses by user ID
     */
    suspend fun getAnalysesByUser(userId: String, page: Int = 1, pageSize: Int = 20): List<TextAnalysisResult>
    
    /**
     * Get analyses by sentiment
     */
    suspend fun getAnalysesBySentiment(sentiment: SentimentLabel, page: Int = 1, pageSize: Int = 20): List<TextAnalysisResult>
    
    /**
     * Search analyses by keywords
     */
    suspend fun searchAnalysesByKeywords(keywords: List<String>, page: Int = 1, pageSize: Int = 20): List<TextAnalysisResult>
    
    /**
     * Get text mining statistics
     */
    suspend fun getTextMiningStats(): TextMiningStats
    
    /**
     * Delete analysis by ID
     */
    suspend fun deleteAnalysis(analysisId: String): Boolean
    
    /**
     * Get trending topics from recent analyses
     */
    suspend fun getTrendingTopics(timeframe: String = "24h", limit: Int = 10): List<Topic>
    
    /**
     * Get sentiment trends over time
     */
    suspend fun getSentimentTrends(timeframe: String = "7d"): Map<String, Map<SentimentLabel, Int>>
}
