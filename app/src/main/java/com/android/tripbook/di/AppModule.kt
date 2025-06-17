// AppModule.kt
package com.android.tripbook.di

import com.android.tripbook.data.repositories.MockServiceRepository
import com.android.tripbook.data.repositories.ServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Marks this class as a Dagger Hilt module
@InstallIn(SingletonComponent::class) // Specifies the scope (Singleton means it lives as long as the app)
abstract class AppModule { // Abstract class for @Binds methods

    @Singleton // Ensures only one instance of ServiceRepository exists
    @Binds // Tells Hilt that when ServiceRepository is requested, provide MockServiceRepository
    abstract fun bindServiceRepository(
        mockServiceRepository: MockServiceRepository // Hilt will figure out how to create MockServiceRepository
    ): ServiceRepository
}