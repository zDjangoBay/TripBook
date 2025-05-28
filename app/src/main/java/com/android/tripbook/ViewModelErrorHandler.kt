package com.android.tripbook

import java.io.IOException
import retrofit2.HttpException

object ViewModelErrorHandler {
    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> "Network error occurred. Please check your connection."
            is HttpException -> "Server error occurred. Please try again later."
            else -> throwable.localizedMessage ?: "An unexpected error occurred"
        }
    }
}