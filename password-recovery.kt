import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.mutableListOf

data class User(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class ResetToken(
    val id: Int,
    val userId: Int,
    val token: String,
    val expiresAt: LocalDateTime,
    val used: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

class PasswordRecoverySystem {
    private val users = mutableListOf<User>()
    private val resetTokens = mutableListOf<ResetToken>()
    private var nextUserId = 1
    private var nextTokenId = 1
    private val secureRandom = SecureRandom()
    
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = MessageDigest.getInstance("SHA-256")
            .digest((password + salt).toByteArray())
            .joinToString("") { "%02x".format(it) }
        return "$salt:$hash"
    }
    
    private fun generateSalt(): String {
        val saltBytes = ByteArray(16)
        secureRandom.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }
    
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val (salt, hash) = storedHash.split(":")
            val computedHash = MessageDigest.getInstance("SHA-256")
                .digest((password + salt).toByteArray())
                .joinToString("") { "%02x".format(it) }
            computedHash == hash
        } catch (e: Exception) {
            false
        }
    }
    
    fun validatePasswordStrength(password: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (password.length < 8) {
            errors.add("Password must be at least 8 characters long")
        }
        
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter")
        }
        
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter")
        }
        
        if (!Pattern.compile("\\d").matcher(password).find()) {
            errors.add("Password must contain at least one number")
        }
        
        if (!Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) {
            errors.add("Password must contain at least one special character")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    private fun generateResetToken(): String {
        val tokenBytes = ByteArray(32)
        secureRandom.nextBytes(tokenBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)
    }
    
    fun createUser(email: String, password: String): ValidationResult {
        val passwordValidation = validatePasswordStrength(password)
        if (!passwordValidation.isValid) {
            return passwordValidation
        }
        
        if (users.any { it.email == email }) {
            return ValidationResult(false, listOf("Email already exists"))
        }
        
        val passwordHash = hashPassword(password)
        val user = User(nextUserId++, email, passwordHash)
        users.add(user)
        
        println("User created successfully with ID: ${user.id}")
        return ValidationResult(true, listOf("User created successfully"))
    }
    
    fun requestPasswordReset(email: String): ValidationResult {
        val user = users.find { it.email == email }
            ?: return ValidationResult(false, listOf("Email not found"))
        
        val token = generateResetToken()
        val expiresAt = LocalDateTime.now().plusHours(1)
        
        val resetToken = ResetToken(nextTokenId++, user.id, token, expiresAt)
        resetTokens.add(resetToken)
        
        sendResetEmail(email, token)
        
        println("Password reset requested for $email")
        println("Reset token: $token")
        return ValidationResult(true, listOf("Password reset email sent"))
    }
    
    private fun sendResetEmail(email: String, token: String) {
        val resetUrl = "http://localhost:3000/auth/reset-password?token=$token"
        
        println("\n--- PASSWORD RESET EMAIL ---")
        println("To: $email")
        println("Subject: Password Reset Request")
        println("")
        println("Click the following link to reset your password:")
        println(resetUrl)
        println("")
        println("This link will expire in 1 hour.")
        println("--- END EMAIL ---\n")
    }
    
    fun validateResetToken(token: String): Pair<Boolean, Any> {
        val resetToken = resetTokens.find { it.token == token }
            ?: return Pair(false, "Invalid token")
        
        if (resetToken.used) {
            return Pair(false, "Token has already been used")
        }
        
        if (LocalDateTime.now().isAfter(resetToken.expiresAt)) {
            return Pair(false, "Token has expired")
        }
        
        val user = users.find { it.id == resetToken.userId }
            ?: return Pair(false, "User not found")
        
        return Pair(true, mapOf(
            "tokenId" to resetToken.id,
            "userId" to user.id,
            "email" to user.email
        ))
    }
    
    fun resetPassword(token: String, newPassword: String, confirmPassword: String): ValidationResult {
        if (newPassword != confirmPassword) {
            return ValidationResult(false, listOf("Passwords do not match"))
        }
        
        val passwordValidation = validatePasswordStrength(newPassword)
        if (!passwordValidation.isValid) {
            return passwordValidation
        }
        
        val (isValidToken, tokenData) = validateResetToken(token)
        if (!isValidToken) {
            return ValidationResult(false, listOf(tokenData as String))
        }
        
        val tokenInfo = tokenData as Map<String, Any>
        val userId = tokenInfo["userId"] as Int
        val tokenId = tokenInfo["tokenId"] as Int
        val email = tokenInfo["email"] as String
        
        // Update user password
        val userIndex = users.indexOfFirst { it.id == userId }
        if (userIndex != -1) {
            val user = users[userIndex]
            val newPasswordHash = hashPassword(newPassword)
            users[userIndex] = user.copy(passwordHash = newPasswordHash)
        }
        
        // Mark token as used
        val tokenIndex = resetTokens.indexOfFirst { it.id == tokenId }
        if (tokenIndex != -1) {
            val resetToken = resetTokens[tokenIndex]
            resetTokens[tokenIndex] = resetToken.copy(used = true)
        }
        
        println("Password reset successfully for $email")
        return ValidationResult(true, listOf("Password reset successfully"))
    }
    
    fun cleanupExpiredTokens(): Int {
        val now = LocalDateTime.now()
        val initialSize = resetTokens.size
        
        resetTokens.removeAll { it.expiresAt.isBefore(now) || it.used }
        
        val deletedCount = initialSize - resetTokens.size
        println("Cleaned up $deletedCount expired/used tokens")
        return deletedCount
    }
    
    fun printSystemStatus() {
        println("\n=== SYSTEM STATUS ===")
        println("Total users: ${users.size}")
        println("Active reset tokens: ${resetTokens.count { !it.used && LocalDateTime.now().isBefore(it.expiresAt) }}")
        println("Used/expired tokens: ${resetTokens.count { it.used || LocalDateTime.now().isAfter(it.expiresAt) }}")
        println("=====================\n")
    }
}

// Demo usage
fun main() {
    val prs = PasswordRecoverySystem()
    
    // Create a demo user
    println("=== Creating Demo User ===")
    val createResult = prs.createUser("user@example.com", "SecurePass123!")
    println("Result: ${createResult.isValid}, Messages: ${createResult.errors}")
    
    // Request password reset
    println("\n=== Requesting Password Reset ===")
    val resetResult = prs.requestPasswordReset("user@example.com")
    println("Result: ${resetResult.isValid}, Messages: ${resetResult.errors}")
    
    // Get the latest token for demo purposes
    println("\n=== Validating Token ===")
    val latestToken = prs.resetTokens.lastOrNull()?.token
    
    if (latestToken != null) {
        val (isValid, result) = prs.validateResetToken(latestToken)
        println("Token valid: $isValid, Result: $result")
        
        // Reset password
        println("\n=== Resetting Password ===")
        val passwordResetResult = prs.resetPassword(latestToken, "NewSecurePass456!", "NewSecurePass456!")
        println("Result: ${passwordResetResult.isValid}, Messages: ${passwordResetResult.errors}")
    }
    
    // Show system status
    prs.printSystemStatus()
    
    // Clean up expired tokens
    println("=== Cleaning Up Tokens ===")
    val cleaned = prs.cleanupExpiredTokens()
    
    // Final system status
    prs.printSystemStatus()
}
