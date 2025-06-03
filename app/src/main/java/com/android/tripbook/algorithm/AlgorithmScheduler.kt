package com.android.tripbook.algorithm

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.tripbook.algorithm.worker.ClassificationWorker
import com.android.tripbook.algorithm.worker.InsightsWorker
import com.android.tripbook.algorithm.worker.TextMiningWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlgorithmScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val TAG = "AlgorithmScheduler"
        private const val TEXT_MINING_WORK_NAME = "text_mining_work"
        private const val CLASSIFICATION_WORK_NAME = "classification_work"
        private const val INSIGHTS_WORK_NAME = "insights_work"
    }
    
    fun scheduleAlgorithmWorkers() {
        try {
            // Schedule text mining worker (runs every 30 minutes)
            val textMiningRequest = PeriodicWorkRequestBuilder<TextMiningWorker>(
                30, TimeUnit.MINUTES
            ).build()
            
            workManager.enqueueUniquePeriodicWork(
                TEXT_MINING_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                textMiningRequest
            )
            
            // Schedule classification worker (runs every 45 minutes)
            val classificationRequest = PeriodicWorkRequestBuilder<ClassificationWorker>(
                45, TimeUnit.MINUTES
            ).build()
            
            workManager.enqueueUniquePeriodicWork(
                CLASSIFICATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                classificationRequest
            )
            
            // Schedule insights worker (runs every hour)
            val insightsRequest = PeriodicWorkRequestBuilder<InsightsWorker>(
                1, TimeUnit.HOURS
            ).build()
            
            workManager.enqueueUniquePeriodicWork(
                INSIGHTS_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                insightsRequest
            )
            
            Log.d(TAG, "Algorithm workers scheduled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling algorithm workers", e)
            // Don't crash the app if worker scheduling fails
        }
    }
}


