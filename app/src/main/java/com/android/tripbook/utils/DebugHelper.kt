package com.android.tripbook.utils

import android.util.Log

/**
 * Helper class for debugging the application
 */
object DebugHelper {
    private const val TAG = "TripBookDebug"
    
    /**
     * Log a debug message
     */
    fun debug(message: String) {
        Log.d(TAG, message)
    }
    
    /**
     * Log an error message with exception
     */
    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }
    
    /**
     * Log an info message
     */
    fun info(message: String) {
        Log.i(TAG, message)
    }
    
    /**
     * Log a warning message
     */
    fun warn(message: String) {
        Log.w(TAG, message)
    }
    
    /**
     * Log a method entry
     */
    fun enterMethod(className: String, methodName: String) {
        debug("→ $className.$methodName()")
    }
    
    /**
     * Log a method exit
     */
    fun exitMethod(className: String, methodName: String) {
        debug("← $className.$methodName()")
    }
}
