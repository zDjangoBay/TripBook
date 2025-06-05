package com.example.tripbooktest.data

interface ApiService {
    suspend fun getUserById(id: String): User
    suspend fun getTrips(): List<Trip>
}
