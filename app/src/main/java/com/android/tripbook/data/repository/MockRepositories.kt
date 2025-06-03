package com.android.tripbook.data.repository

import com.android.tripbook.data.model.ClassifiedData
import com.android.tripbook.data.model.ProcessedText
import com.android.tripbook.data.model.RawTextData
import com.android.tripbook.data.model.TravelInsight
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockTextMiningRepository @Inject constructor() : TextMiningRepository {
    private val rawTextData = mutableListOf<RawTextData>()
    private val processedTexts = mutableListOf<ProcessedText>()
    
    override suspend fun getUnprocessedTextBatch(batchSize: Int): List<RawTextData> {
        return rawTextData.filter { !it.isProcessed }.take(batchSize)
    }
    
    override suspend fun saveProcessedText(processedText: ProcessedText) {
        processedTexts.add(processedText)
    }
    
    override suspend fun markRawTextAsProcessed(rawTextId: String) {
        val index = rawTextData.indexOfFirst { it.id == rawTextId }
        if (index != -1) {
            val item = rawTextData[index]
            rawTextData[index] = item.copy(isProcessed = true)
        }
    }
}

@Singleton
class MockClassificationRepository @Inject constructor() : ClassificationRepository {
    private val processedTexts = mutableListOf<ProcessedText>()
    private val classifiedData = mutableListOf<ClassifiedData>()
    
    override suspend fun getUnclassifiedProcessedText(batchSize: Int): List<ProcessedText> {
        return processedTexts.filter { !it.isClassified }.take(batchSize)
    }
    
    override suspend fun saveClassifiedData(data: ClassifiedData) {
        classifiedData.add(data)
    }
    
    override suspend fun markProcessedTextAsClassified(processedTextId: String) {
        val index = processedTexts.indexOfFirst { it.id == processedTextId }
        if (index != -1) {
            val item = processedTexts[index]
            processedTexts[index] = item.copy(isClassified = true)
        }
    }
}

@Singleton
class MockInsightsRepository @Inject constructor() : InsightsRepository {
    private val classifiedData = mutableListOf<ClassifiedData>()
    private val insights = mutableListOf<TravelInsight>()
    private val processedClassifiedDataIds = mutableSetOf<String>()
    
    override suspend fun getUnprocessedClassifiedData(batchSize: Int): List<ClassifiedData> {
        return classifiedData.filter { it.id !in processedClassifiedDataIds }.take(batchSize)
    }
    
    override suspend fun saveInsight(insight: TravelInsight) {
        insights.add(insight)
    }
    
    override suspend fun markClassifiedDataAsProcessed(classifiedDataId: String) {
        processedClassifiedDataIds.add(classifiedDataId)
    }
}