package com.android.tripbook.data.services

/**
 * Generic wrapper class for API responses
 * Helps handle success, error, and loading states uniformly
 */
sealed class ServiceResult<out T> {
    data class Success<T>(val data: T) : ServiceResult<T>()
    data class Error(val message: String, val code: Int = 0) : ServiceResult<Nothing>()
    object Loading : ServiceResult<Nothing>()
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
    
    fun getOrNull(): T? = if (this is Success) data else null
    fun getErrorMessage(): String? = if (this is Error) message else null
}