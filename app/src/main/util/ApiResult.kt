package com.yourcompany.yourappname.util

// A sealed class to represent the state of an API call
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String?, val statusCode: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}