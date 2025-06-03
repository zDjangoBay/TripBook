package com.android.tripbook.data.repository

import com.android.tripbook.data.model.ClassifiedData
import com.android.tripbook.data.model.ProcessedText
import com.android.tripbook.data.model.RawTextData
import com.android.tripbook.data.model.TravelInsight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextMiningRepositoryImpl @Inject constructor() : TextMiningRepository {
    // In-memory storage for demo purposes
    private val rawTextData = MutableStateFlow<List<RawTextData>>(emptyList())
    
    init {
        // Add some sample data
        val sampleData = listOf(
            RawTextData(
                id = "1",
                userId = "user1",
                content = "I loved my safari trip to Kenya. The wildlife was amazing!",
                source = "app_post",
                timestamp = System.currentTimeMillis(),
                isProcessed = false
            ),
            RawTextData(
                id = "2",
                userId = "user2",
                content = "Planning to visit Tanzania next month. Any hotel recommendations?",
                source = "app_comment",
                timestamp = System.currentTimeMillis(),
                isProcessed = false
            )
        )
        rawTextData.value = sampleData
    }
    
    override suspend fun getUnprocessedTextBatch(batchSize: Int): List<RawTextData> {
        return rawTextData.value
            .filter { !it.isProcessed }
            .take(batchSize)
    }
    
    override suspend fun saveProcessedText(processedText: ProcessedText) {
        // In a real app, this would save to a database
        // For demo purposes, we're just logging
    }
    
    override suspend fun markRawTextAsProcessed(rawTextId: String) {
        val updatedList = rawTextData.value.map { 
            if (it.id == rawTextId) it.copy(isProcessed = true) else it 
        }
        rawTextData.value = updatedList
    }
}

@Singleton
class ClassificationRepositoryImpl @Inject constructor() : ClassificationRepository {
    // In-memory storage for demo purposes
    private val processedTexts = MutableStateFlow<List<ProcessedText>>(emptyList())
    private val classifiedData = MutableStateFlow<List<ClassifiedData>>(emptyList())
    
    override suspend fun getUnclassifiedProcessedText(batchSize: Int): List<ProcessedText> {
        return processedTexts.value
            .filter { !it.isClassified }
            .take(batchSize)
    }
    
    override suspend fun saveClassifiedData(classifiedData: ClassifiedData) {
        val updatedList = this.classifiedData.value + classifiedData
        this.classifiedData.value = updatedList
    }
    
    override suspend fun markProcessedTextAsClassified(processedTextId: String) {
        val updatedList = processedTexts.value.map { 
            if (it.id == processedTextId) it.copy(isClassified = true) else it 
        }
        processedTexts.value = updatedList
    }
}

@Singleton
class InsightsRepositoryImpl @Inject constructor() : InsightsRepository {
    // In-memory storage for demo purposes
    private val classifiedData = MutableStateFlow<List<ClassifiedData>>(emptyList())
    private val insights = MutableStateFlow<List<TravelInsight>>(emptyList())
    private val processedClassifiedDataIds = MutableStateFlow<Set<String>>(emptySet())
    
    override suspend fun getUnprocessedClassifiedData(batchSize: Int): List<ClassifiedData> {
        return classifiedData.value
            .filter { it.id !in processedClassifiedDataIds.value }
            .take(batchSize)
    }
    
    override suspend fun saveInsight(insight: TravelInsight) {
        val updatedList = insights.value + insight
        insights.value = updatedList
    }
    
    override suspend fun markClassifiedDataAsProcessed(classifiedDataId: String) {
        val updatedSet = processedClassifiedDataIds.value + classifiedDataId
        processedClassifiedDataIds.value = updatedSet
    }
}

