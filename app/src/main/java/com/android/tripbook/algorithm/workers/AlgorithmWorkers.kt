package com.android.tripbook.algorithm.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.tripbook.algorithm.classification.ClassificationService
import com.android.tripbook.algorithm.insights.InsightsService
import com.android.tripbook.algorithm.textmining.TextMiningService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class TextMiningWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val textMiningService: TextMiningService
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "TextMiningWorker"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting text mining batch processing")
            textMiningService.processTextBatch()
            Log.d(TAG, "Text mining batch processing completed")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during text mining batch processing", e)
            Result.retry()
        }
    }
}

@HiltWorker
class ClassificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val classificationService: ClassificationService
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "ClassificationWorker"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting classification batch processing")
            classificationService.classifyBatch() // Changed from classifyProcessedTextBatch to classifyBatch
            Log.d(TAG, "Classification batch processing completed")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during classification batch processing", e)
            Result.retry()
        }
    }
}

@HiltWorker
class InsightsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val insightsService: InsightsService
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "InsightsWorker"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting insights generation")
            insightsService.generateInsights()
            Log.d(TAG, "Insights generation completed")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during insights generation", e)
            Result.retry()
        }
    }
}
