package com.android.tripbook.algorithm.classification

import com.android.tripbook.data.model.ClassifiedData
import com.android.tripbook.data.repository.ClassificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ClassificationService @Inject constructor(
    private val repository: ClassificationRepository,
    private val categoryClassifier: CategoryClassifier,
    private val preferenceClassifier: PreferenceClassifier
) {
    suspend fun classifyBatch(batchSize: Int = 50) = withContext(Dispatchers.Default) {
        val unclassifiedTexts = repository.getUnclassifiedProcessedText(batchSize)
        
        unclassifiedTexts.forEach { processedText ->
            // Classify categories
            val categories = categoryClassifier.classifyCategories(processedText)
            
            // Classify preferences
            val preferences = preferenceClassifier.classifyPreferences(processedText)
            
            // Save classified data
            repository.saveClassifiedData(
                ClassifiedData(
                    id = UUID.randomUUID().toString(),
                    processedTextId = processedText.id,
                    categories = categories,
                    travelPreferences = preferences,
                    timestamp = System.currentTimeMillis()
                )
            )
            
            // Mark processed text as classified
            repository.markProcessedTextAsClassified(processedText.id)
        }
    }
}



