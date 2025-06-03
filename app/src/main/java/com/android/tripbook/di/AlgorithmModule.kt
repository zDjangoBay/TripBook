package com.android.tripbook.di

import androidx.work.WorkManager
import com.android.tripbook.algorithm.AlgorithmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlgorithmModule {
    
    @Provides
    @Singleton
    fun provideAlgorithmScheduler(workManager: WorkManager): AlgorithmScheduler {
        return AlgorithmScheduler(workManager)
    }
}

