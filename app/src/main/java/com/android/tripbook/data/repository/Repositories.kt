package com.android.tripbook.data.repository

import com.android.tripbook.data.model.ClassifiedData
import com.android.tripbook.data.model.ProcessedText
import com.android.tripbook.data.model.RawTextData
import com.android.tripbook.data.model.TravelInsight
import kotlinx.coroutines.flow.Flow

interface TextMiningRepository {
    suspend fun getUnprocessedTextBatch(batchSize: Int): List<RawTextData>
    suspend fun saveProcessedText(processedText: ProcessedText)
    suspend fun markTextAsProcessed(textId: String)
}

interface ClassificationRepository {
    suspend fun getUnclassifiedProcessedText(batchSize: Int): List<ProcessedText>
    suspend fun saveClassifiedData(classifiedData: ClassifiedData)
    suspend fun markProcessedTextAsClassified(processedTextId: String)
    fun getClassifiedDataByProcessedTextId(processedTextId: String): Flow<ClassifiedData?>
    fun getAllClassifiedData(): Flow<List<ClassifiedData>>
}

interface InsightsRepository {
    suspend fun getUnprocessedClassifiedData(batchSize: Int): List<ClassifiedData>
    suspend fun saveInsight(insight: TravelInsight)
    suspend fun markInsightAsDelivered(insightId: String)
    fun getUndeliveredInsights(): Flow<List<TravelInsight>>
}
