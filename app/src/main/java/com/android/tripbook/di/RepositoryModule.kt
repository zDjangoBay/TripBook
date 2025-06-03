package com.android.tripbook.di

import com.android.tripbook.data.repository.ClassificationRepository
import com.android.tripbook.data.repository.ClassificationRepositoryImpl
import com.android.tripbook.data.repository.InsightsRepository
import com.android.tripbook.data.repository.InsightsRepositoryImpl
import com.android.tripbook.data.repository.TextMiningRepository
import com.android.tripbook.data.repository.TextMiningRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindTextMiningRepository(
        repository: TextMiningRepositoryImpl
    ): TextMiningRepository
    
    @Binds
    @Singleton
    abstract fun bindClassificationRepository(
        repository: ClassificationRepositoryImpl
    ): ClassificationRepository
    
    @Binds
    @Singleton
    abstract fun bindInsightsRepository(
        repository: InsightsRepositoryImpl
    ): InsightsRepository
}


