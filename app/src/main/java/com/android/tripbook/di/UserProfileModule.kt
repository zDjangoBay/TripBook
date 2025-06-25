package com.android.tripbook.di

import com.android.tripbook.data.UserDataSource
import com.android.tripbook.data.UserRepository
import com.android.tripbook.data.repository.UserRepositoryImpl
import com.android.tripbook.data.source.RemoteUserDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserDataSource(
        remoteUserDataSource: RemoteUserDataSource
    ): UserDataSource
}
