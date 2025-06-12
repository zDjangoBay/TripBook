package com.android.algorithms.classification.model

interface ClassificationService {
    
    /**
     * Classify a single text or text analysis result
     */
    suspend fun classifyText(request: ClassificationRequest): ClassificationResult?
    
    /**
     * Batch classify multiple texts
     */
    suspend fun batchClassifyTexts(request: BatchClassificationRequest): List<ClassificationResult>
    
    /**
     * Get classification result by ID
     */
    suspend fun getClassificationById(classificationId: String): ClassificationResult?
    
    /**
     * Get classifications by text analysis ID
     */
    suspend fun getClassificationsByTextAnalysis(textAnalysisId: String): List<ClassificationResult>
    
    /**
     * Get classifications by user ID
     */
    suspend fun getClassificationsByUser(userId: String, page: Int = 1, pageSize: Int = 20): List<ClassificationResult>
    
    /**
     * Get classifications by category
     */
    suspend fun getClassificationsByCategory(category: String, page: Int = 1, pageSize: Int = 20): List<ClassificationResult>
    
    /**
     * Get classifications by model type
     */
    suspend fun getClassificationsByModelType(modelType: ClassificationModelType, page: Int = 1, pageSize: Int = 20): List<ClassificationResult>
    
    /**
     * Search classifications by confidence threshold
     */
    suspend fun getClassificationsByConfidence(minConfidence: Double, maxConfidence: Double = 1.0, page: Int = 1, pageSize: Int = 20): List<ClassificationResult>
    
    /**
     * Get classification statistics
     */
    suspend fun getClassificationStats(): ClassificationStats
    
    /**
     * Get feature importance for a specific model
     */
    suspend fun getFeatureImportance(modelType: ClassificationModelType): List<FeatureImportance>
    
    /**
     * Get classification insights and trends
     */
    suspend fun getClassificationInsights(timeframe: String = "7d"): List<ClassificationInsight>
    
    /**
     * Train a new classification model
     */
    suspend fun trainModel(request: ModelTrainingRequest): ModelPerformance?
    
    /**
     * Get model performance metrics
     */
    suspend fun getModelPerformance(modelType: ClassificationModelType): ModelPerformance?
    
    /**
     * Delete classification by ID
     */
    suspend fun deleteClassification(classificationId: String): Boolean
    
    /**
     * Update classification result (for feedback/correction)
     */
    suspend fun updateClassification(classificationId: String, corrections: List<Classification>): ClassificationResult?
    
    /**
     * Get trending categories
     */
    suspend fun getTrendingCategories(timeframe: String = "24h", limit: Int = 10): List<CategoryTrend>
    
    /**
     * Predict category for new text without storing result
     */
    suspend fun predictCategory(text: String, modelType: ClassificationModelType = ClassificationModelType.GENERAL): List<Classification>
}
