package com.example.tripbooktest.data.repository

import com.example.tripbooktest.data.ApiService
import com.example.tripbooktest.data.User

class UserRepository(private val apiService: ApiService) {
    suspend fun fetchUser(id: String): User {
        return apiService.getUserById(id)
    }
}
