package com.tripbook.utils

import com.tripbook.data.model.NetworkResult
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility class for consistent error handling throughout the app
 */
object ErrorHandler {

    /**
     * Converts exceptions to user-friendly error messages
     * @param throwable The exception to convert
     * @return User-friendly error message
     */
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> "Bad request. Please check your input and try again."
                    401 -> "Authentication failed. Please log in again."
                    403 -> "You don't have permission to perform this action."
                    404 -> "The requested resource was not found."
                    408 -> "Request timeout. Please try again."
                    409 -> "Conflict occurred. The resource already exists."
                    422 -> "Invalid data provided. Please check your input."
                    429 -> "Too many requests. Please wait a moment and try again."
                    500 -> "Server error. Please try again later."
                    502 -> "Bad gateway. Please try again later."
                    503 -> "Service unavailable. Please try again later."
                    504 -> "Gateway timeout. Please try again later."
                    else -> "HTTP error ${throwable.code()}: ${throwable.message()}"
                }
            }
            is SocketTimeoutException -> "Connection timeout. Please check your internet connection and try again."
            is ConnectException -> "Could not connect to server. Please check your internet connection."
            is UnknownHostException -> "No internet connection. Please check your network settings."
            is IOException -> "Network error occurred. Please try again."
            else -> throwable.message ?: "An unexpected error occurred. Please try again."
        }
    }

    /**
     * Converts exceptions to NetworkResult.Error with appropriate messages
     * @param throwable The exception to convert
     * @return NetworkResult.Error with user-friendly message
     */
    fun <T> handleError(throwable: Throwable): NetworkResult<T> {
        return when (throwable) {
            is HttpException -> NetworkResult.Error(getErrorMessage(throwable), throwable.code())
            else -> NetworkResult.Error(getErrorMessage(throwable))
        }
    }

    /**
     * Determines if an error is retryable
     * @param throwable The exception to check
     * @return True if the operation can be retried, false otherwise
     */
    fun isRetryableError(throwable: Throwable): Boolean {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    408, 429, 500, 502, 503, 504 -> true
                    else -> false
                }
            }
            is SocketTimeoutException, is ConnectException, is UnknownHostException -> true
            is IOException -> true
            else -> false
        }
    }

    /**
     * Determines if an error is related to network connectivity
     * @param throwable The exception to check
     * @return True if it's a network-related error, false otherwise
     */
    fun isNetworkError(throwable: Throwable): Boolean {
        return when (throwable) {
            is SocketTimeoutException, is ConnectException, is UnknownHostException -> true
            is IOException -> true
            is HttpException -> throwable.code() >= 500
            else -> false
        }
    }

    /**
     * Gets a suggested retry delay in milliseconds based on the error type
     * @param throwable The exception to get retry delay for
     * @param attemptNumber The current retry attempt number (1-based)
     * @return Suggested delay in milliseconds
     */
    fun getRetryDelay(throwable: Throwable, attemptNumber: Int): Long {
        val baseDelay = when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    429 -> 5000L // Rate limited - wait longer
                    500, 502, 503, 504 -> 2000L // Server errors
                    else -> 1000L
                }
            }
            is SocketTimeoutException -> 3000L
            is ConnectException, is UnknownHostException -> 5000L
            else -> 1000L
        }
        
        // Exponential backoff with jitter
        return baseDelay * attemptNumber + (Math.random() * 1000).toLong()
    }

    /**
     * Logs errors in a consistent format
     * @param tag The tag for logging
     * @param throwable The exception to log
     * @param context Additional context information
     */
    fun logError(tag: String, throwable: Throwable, context: String = "") {
        val errorMessage = buildString {
            append("[$tag] Error occurred")
            if (context.isNotEmpty()) {
                append(" during: $context")
            }
            append("\n")
            append("Type: ${throwable::class.simpleName}\n")
            append("Message: ${throwable.message}\n")
            if (throwable is HttpException) {
                append("HTTP Code: ${throwable.code()}\n")
                try {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    if (!errorBody.isNullOrBlank()) {
                        append("Response Body: $errorBody\n")
                    }
                } catch (e: Exception) {
                    // Ignore errors when reading error body
                }
            }
        }
        
        // In a real app, you would use proper logging framework like Timber
        println(errorMessage)
        throwable.printStackTrace()
    }

    /**
     * Error categories for better error handling strategies
     */
    enum class ErrorCategory {
        NETWORK,        // Network connectivity issues
        SERVER,         // Server-side errors
        CLIENT,         // Client-side errors (validation, etc.)
        AUTHENTICATION, // Authentication/authorization errors
        UNKNOWN         // Unknown or unexpected errors
    }

    /**
     * Categorizes an error for better handling
     * @param throwable The exception to categorize
     * @return Error category
     */
    fun categorizeError(throwable: Throwable): ErrorCategory {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    in 400..499 -> {
                        when (throwable.code()) {
                            401, 403 -> ErrorCategory.AUTHENTICATION
                            else -> ErrorCategory.CLIENT
                        }
                    }
                    in 500..599 -> ErrorCategory.SERVER
                    else -> ErrorCategory.UNKNOWN
                }
            }
            is SocketTimeoutException, is ConnectException, is UnknownHostException, is IOException -> {
                ErrorCategory.NETWORK
            }
            else -> ErrorCategory.UNKNOWN
        }
    }
}
