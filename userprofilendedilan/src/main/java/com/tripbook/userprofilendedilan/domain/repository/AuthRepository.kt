package com.tripbook.userprofilendedilan.domain.repository

interface AuthRepository {
    suspend fun registerUser(email: String, password: String): Result<Unit>
}
