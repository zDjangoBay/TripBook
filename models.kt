package data

import java.time.LocalDateTime

data class User(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val createdAt: LocalDateTime
)

data class ResetToken(
    val id: Int,
    val userId: Int,
    val token: String,
    val expiresAt: LocalDateTime,
    val used: Boolean,
    val createdAt: LocalDateTime
)

data class OperationResult(
    val isSuccess: Boolean,
    val message: String
)

data class PasswordStrength(
    val score: Int,
    val feedback: List<String>
)
