package com.android.tripbook.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * OkHttp Interceptor to automatically retry requests that fail due to transient network issues.
 */
class RetryInterceptor(private val maxRetries: Int = 3, private val initialDelayMillis: Long = 1000) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        var exception: IOException? = null
        var tryCount = 0
        var currentDelay = initialDelayMillis

        while (tryCount < maxRetries) {
            try {
                response = chain.proceed(chain.request())
                // Retry on server errors (5xx)
                if (response.isSuccessful || response.code < 500) {
                    return response
                } else {
                    // Close the previous response body to avoid resource leaks
                    response.close()
                    // Fall through to retry logic
                }
            } catch (e: IOException) {
                exception = e
                // Only retry on specific exceptions like SocketTimeoutException
                if (e !is SocketTimeoutException) {
                    throw e // Don't retry for other IOExceptions
                }
                // Fall through to retry logic
            }

            tryCount++
            if (tryCount < maxRetries) {
                println("RetryInterceptor: Retrying request (${tryCount}/${maxRetries}) after delay ${currentDelay}ms")
                // Use runBlocking for simplicity here, consider a coroutine scope in a real app
                runBlocking { delay(currentDelay) }
                currentDelay *= 2 // Exponential backoff
            } else {
                 println("RetryInterceptor: Max retries reached (${maxRetries})")
            }
        }

        // If response is still null after retries, throw the last caught exception
        if (response == null && exception != null) {
            throw exception
        }

        // Return the last response even if it was an error after retries
        return response!!
    }
}
