package com.android.tripbook.data.repository

import com.android.tripbook.data.UserDataSource
import com.android.tripbook.data.UserRepository
import javax.inject.Inject
 
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    // Implement user repository functions here
} 