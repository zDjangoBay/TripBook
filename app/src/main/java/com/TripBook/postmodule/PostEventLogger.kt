package com.TripBook.postmodule

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Logger for PostEvent instances to track user interactions and debug issues.
 * Provides both console logging and file-based logging capabilities.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostEventLogger(private val context: Context) {
    
    companion object {
        private const val TAG = "PostEventLogger"
        private const val LOG_FILE_NAME = "post_events.log"
        private const val MAX_LOG_FILE_SIZE = 5 * 1024 * 1024 // 5MB
        private const val MAX_LOG_FILES = 3
    }
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val logFile = File(context.filesDir, LOG_FILE_NAME)
    
    /**
     * Logs a PostEvent with detailed information
     */
    suspend fun logEvent(
        event: PostEvent,
        sessionId: String? = null,
        userId: String? = null,
        additionalData: Map<String, Any>? = null
    ) = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val formattedTime = dateFormatter.format(Date(timestamp))
        
        val logEntry = createLogEntry(event, formattedTime, sessionId, userId, additionalData)
        
        // Console logging
        logToConsole(event, logEntry)
        
        // File logging
        if (PostConstants.FeatureFlags.ENABLE_ANALYTICS) {
            logToFile(logEntry)
        }
    }
    
    /**
     * Logs an event with performance metrics
     */
    suspend fun logEventWithPerformance(
        event: PostEvent,
        processingTimeMs: Long,
        memoryUsageMB: Double? = null
    ) = withContext(Dispatchers.IO) {
        val performanceData = mutableMapOf<String, Any>(
            "processing_time_ms" to processingTimeMs
        )
        
        memoryUsageMB?.let { performanceData["memory_usage_mb"] = it }
        
        logEvent(event, additionalData = performanceData)
    }
    
    /**
     * Logs an error that occurred during event processing
     */
    suspend fun logEventError(
        event: PostEvent,
        error: Throwable,
        context: String? = null
    ) = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val formattedTime = dateFormatter.format(Date(timestamp))
        
        val errorData = mapOf(
            "error_type" to error::class.simpleName,
            "error_message" to error.message,
            "error_context" to context,
            "stack_trace" to error.stackTraceToString()
        )
        
        val logEntry = createLogEntry(event, formattedTime, additionalData = errorData)
        
        // Log error to console
        Log.e(TAG, "Error processing event: ${event::class.simpleName}", error)
        
        // Log to file
        logToFile("ERROR: $logEntry")
    }
    
    /**
     * Logs event validation results
     */
    suspend fun logValidationResult(
        event: PostEvent,
        validationResult: FieldValidationResult,
        field: String
    ) = withContext(Dispatchers.IO) {
        val validationData = mapOf(
            "field" to field,
            "is_valid" to validationResult.isValid,
            "is_warning" to validationResult.isWarning,
            "validation_message" to validationResult.message
        )
        
        logEvent(event, additionalData = validationData)
    }
    
    /**
     * Logs a batch of events for bulk processing
     */
    suspend fun logEventBatch(
        events: List<PostEvent>,
        batchId: String = UUID.randomUUID().toString()
    ) = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val formattedTime = dateFormatter.format(Date(timestamp))
        
        val batchHeader = "BATCH_START: $batchId at $formattedTime (${events.size} events)"
        logToFile(batchHeader)
        
        events.forEachIndexed { index, event ->
            val batchData = mapOf(
                "batch_id" to batchId,
                "batch_index" to index,
                "batch_size" to events.size
            )
            logEvent(event, additionalData = batchData)
        }
        
        val batchFooter = "BATCH_END: $batchId"
        logToFile(batchFooter)
    }
    
    /**
     * Gets recent log entries
     */
    suspend fun getRecentLogs(maxLines: Int = 100): List<String> = withContext(Dispatchers.IO) {
        if (!logFile.exists()) return@withContext emptyList()
        
        try {
            logFile.readLines().takeLast(maxLines)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading log file", e)
            emptyList()
        }
    }
    
    /**
     * Clears old log entries
     */
    suspend fun clearOldLogs(olderThanDays: Int = 7) = withContext(Dispatchers.IO) {
        if (!logFile.exists()) return@withContext
        
        try {
            val cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
            val lines = logFile.readLines()
            val filteredLines = lines.filter { line ->
                extractTimestampFromLogLine(line)?.let { it > cutoffTime } ?: true
            }
            
            logFile.writeText(filteredLines.joinToString("\n"))
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing old logs", e)
        }
    }
    
    /**
     * Exports logs for debugging or support
     */
    suspend fun exportLogs(): String = withContext(Dispatchers.IO) {
        if (!logFile.exists()) return@withContext ""
        
        try {
            logFile.readText()
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting logs", e)
            ""
        }
    }
    
    /**
     * Gets log file statistics
     */
    suspend fun getLogStats(): LogStats = withContext(Dispatchers.IO) {
        if (!logFile.exists()) {
            return@withContext LogStats(0, 0, 0, null)
        }
        
        try {
            val lines = logFile.readLines()
            val fileSize = logFile.length()
            val oldestTimestamp = lines.firstOrNull()?.let { extractTimestampFromLogLine(it) }
            val newestTimestamp = lines.lastOrNull()?.let { extractTimestampFromLogLine(it) }
            
            LogStats(
                totalLines = lines.size,
                fileSizeBytes = fileSize,
                oldestEntryTimestamp = oldestTimestamp,
                newestEntryTimestamp = newestTimestamp
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting log stats", e)
            LogStats(0, 0, 0, null)
        }
    }
    
    private fun createLogEntry(
        event: PostEvent,
        formattedTime: String,
        sessionId: String? = null,
        userId: String? = null,
        additionalData: Map<String, Any>? = null
    ): String {
        val eventData = event.toMap().toMutableMap()
        
        sessionId?.let { eventData["session_id"] = it }
        userId?.let { eventData["user_id"] = it }
        additionalData?.let { eventData.putAll(it) }
        
        val dataString = eventData.entries.joinToString(", ") { "${it.key}=${it.value}" }
        
        return "[$formattedTime] ${event::class.simpleName}: $dataString"
    }
    
    private fun logToConsole(event: PostEvent, logEntry: String) {
        when (event.getPriority()) {
            EventPriority.HIGH -> Log.i(TAG, logEntry)
            EventPriority.MEDIUM -> Log.d(TAG, logEntry)
            EventPriority.LOW -> Log.v(TAG, logEntry)
        }
    }
    
    private suspend fun logToFile(logEntry: String) = withContext(Dispatchers.IO) {
        try {
            // Check file size and rotate if necessary
            if (logFile.exists() && logFile.length() > MAX_LOG_FILE_SIZE) {
                rotateLogFiles()
            }
            
            FileWriter(logFile, true).use { writer ->
                writer.appendLine(logEntry)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error writing to log file", e)
        }
    }
    
    private fun rotateLogFiles() {
        try {
            // Move current log to backup
            for (i in MAX_LOG_FILES - 1 downTo 1) {
                val oldFile = File(context.filesDir, "$LOG_FILE_NAME.$i")
                val newFile = File(context.filesDir, "$LOG_FILE_NAME.${i + 1}")
                if (oldFile.exists()) {
                    oldFile.renameTo(newFile)
                }
            }
            
            // Move current log to .1
            val backupFile = File(context.filesDir, "$LOG_FILE_NAME.1")
            logFile.renameTo(backupFile)
            
            // Delete oldest log if it exists
            val oldestFile = File(context.filesDir, "$LOG_FILE_NAME.${MAX_LOG_FILES + 1}")
            if (oldestFile.exists()) {
                oldestFile.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error rotating log files", e)
        }
    }
    
    private fun extractTimestampFromLogLine(line: String): Long? {
        return try {
            val timestampPattern = "\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\]".toRegex()
            val match = timestampPattern.find(line)
            match?.groupValues?.get(1)?.let { dateFormatter.parse(it)?.time }
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Statistics about the log file
 */
data class LogStats(
    val totalLines: Int,
    val fileSizeBytes: Long,
    val oldestEntryTimestamp: Long?,
    val newestEntryTimestamp: Long?
)
