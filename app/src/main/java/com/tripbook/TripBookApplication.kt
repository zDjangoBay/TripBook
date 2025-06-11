package com.tripbook

import android.app.Application
import com.tripbook.data.api.ImageUploadApiService
import com.tripbook.data.api.PostApiService
import com.tripbook.data.repository.PostRepository
import com.tripbook.data.repository.PostRepositoryImpl
import com.tripbook.di.NetworkModule
import com.tripbook.domain.usecase.PostUseCase
import com.tripbook.domain.validator.PostValidator
import com.tripbook.utils.ImageUploader

/**
 * Application class that initializes dependencies for the entire app.
 * This acts as a simple dependency injection container.
 */
class TripBookApplication : Application() {

    // Network layer
    private val okHttpClient by lazy { NetworkModule.provideOkHttpClient() }
    private val retrofit by lazy { NetworkModule.provideRetrofit(okHttpClient) }
    
    // API Services
    val postApiService: PostApiService by lazy { NetworkModule.providePostApiService(retrofit) }
    val imageUploadApiService: ImageUploadApiService by lazy { NetworkModule.provideImageUploadApiService(retrofit) }
    
    // Repository layer
    val postRepository: PostRepository by lazy { PostRepositoryImpl(postApiService) }
    
    // Domain layer
    val postValidator: PostValidator by lazy { PostValidator() }
    val imageUploader: ImageUploader by lazy { ImageUploader(this, imageUploadApiService) }
    val postUseCase: PostUseCase by lazy { PostUseCase(postRepository, postValidator, imageUploader) }

    override fun onCreate() {
        super.onCreate()
        // Initialize any global configurations here
        initializeApp()
    }

    private fun initializeApp() {
        // Add any global initialization logic here
        // For example: crash reporting, analytics, etc.
    }

    companion object {
        /**
         * Extension function to get the TripBookApplication instance from any Context
         */
        fun from(context: android.content.Context): TripBookApplication {
            return context.applicationContext as TripBookApplication
        }
    }
}
