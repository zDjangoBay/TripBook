// AppModule.kt
package com.android.tripbook.di

import com.android.tripbook.data.repositories.MockServiceRepository
import com.android.tripbook.data.repositories.ServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module 
@InstallIn(SingletonComponent::class) 
abstract class AppModule { 

    @Singleton 
    @Binds 
    abstract fun bindServiceRepository(
        mockServiceRepository: MockServiceRepository 
    ): ServiceRepository
}