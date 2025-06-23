package com.android.tripbook.database

import android.util.Log
import kotlin.system.measureTimeMillis

/**
 * 📊 PERFORMANCE MONITOR
 * 
 * Monitors database performance for team development.
 * Helps identify slow queries and optimization opportunities.
 */
object PerformanceMonitor {
    
    private const val TAG = "DatabasePerformance"
    
    /**
     * Monitor database operation performance
     */
    suspend fun <T> measureDatabaseOperation(
        operationName: String,
        operation: suspend () -> T
    ): T {
        val result: T
        val timeMs = measureTimeMillis {
            result = operation()
        }
        
        logPerformance(operationName, timeMs)
        return result
    }
    
    /**
     * Log performance metrics
     */
    private fun logPerformance(operationName: String, timeMs: Long) {
        val performanceLevel = when {
            timeMs < 10 -> "⚡ EXCELLENT"
            timeMs < 50 -> "✅ GOOD"
            timeMs < 100 -> "⚠️ ACCEPTABLE"
            timeMs < 500 -> "🐌 SLOW"
            else -> "❌ VERY SLOW"
        }
        
        Log.d(TAG, "$performanceLevel: $operationName took ${timeMs}ms")
        
        // Alert for slow operations
        if (timeMs > 100) {
            Log.w(TAG, "⚠️ PERFORMANCE WARNING: $operationName is slow (${timeMs}ms)")
        }
    }
    
    /**
     * Monitor trip loading performance
     */
    suspend fun <T> monitorTripOperation(
        operation: String,
        block: suspend () -> T
    ): T {
        return measureDatabaseOperation("Trip $operation", block)
    }
    
    /**
     * Monitor review loading performance
     */
    suspend fun <T> monitorReviewOperation(
        operation: String,
        block: suspend () -> T
    ): T {
        return measureDatabaseOperation("Review $operation", block)
    }
    
    /**
     * Monitor search performance
     */
    suspend fun <T> monitorSearchOperation(
        query: String,
        block: suspend () -> T
    ): T {
        return measureDatabaseOperation("Search '$query'", block)
    }
    
    /**
     * Performance summary for team
     */
    fun logPerformanceSummary() {
        Log.i(TAG, "📊 DATABASE PERFORMANCE SUMMARY:")
        Log.i(TAG, "⚡ EXCELLENT: < 10ms")
        Log.i(TAG, "✅ GOOD: 10-50ms")
        Log.i(TAG, "⚠️ ACCEPTABLE: 50-100ms")
        Log.i(TAG, "🐌 SLOW: 100-500ms")
        Log.i(TAG, "❌ VERY SLOW: > 500ms")
        Log.i(TAG, "")
        Log.i(TAG, "💡 Tips for team:")
        Log.i(TAG, "- Use Flow for reactive UI updates")
        Log.i(TAG, "- Batch operations when possible")
        Log.i(TAG, "- Monitor logs for slow queries")
        Log.i(TAG, "- Report performance issues to team")
    }
}
