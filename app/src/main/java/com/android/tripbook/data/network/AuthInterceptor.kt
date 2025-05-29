package com.android.tripbook.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp Interceptor to add the Authorization header to requests.
 */
class AuthInterceptor : Interceptor {

    // Placeholder for token storage/retrieval mechanism
    // In a real app, this would likely come from SharedPreferences, a secure storage, or an auth manager.
    private var authToken: String? = "YOUR_PLACEHOLDER_AUTH_TOKEN" // TODO: Replace with actual token retrieval logic

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Don't add header if token is null or empty
        if (authToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val requestBuilder = originalRequest.newBuilder()
            .header("Authorization", "Bearer $authToken")
            .method(originalRequest.method, originalRequest.body)

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }

    // Optional: Method to update the token if it changes (e.g., after login/refresh)
    fun updateToken(newToken: String?) {
        this.authToken = newToken
    }
}
