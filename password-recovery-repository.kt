package data

import java.security.SecureRandom
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

class PasswordRecoveryRepository {
    
    fun requestPasswordReset(email: String): OperationResult {
        return try {
            DatabaseManager.getConnection().use { conn ->
                // Check if user exists
                val userStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?")
                userStmt.setString(1, email)
                val userRs = userStmt.executeQuery()
                
                if (!userRs.next()) {
                    return OperationResult(false, "Email not found")
                }
                
                val userId = userRs.getInt("id")
                
                // Generate reset token
                val token = generateResetToken()
                val expiresAt = LocalDateTime.now().plusHours(1)
                
                // Store reset token
                val tokenStmt = conn.prepareStatement("""
                    INSERT INTO reset_tokens (user_id, token, expires_at) 
                    VALUES (?, ?, ?)
                """)
                tokenStmt.setInt(1, userId)
                tokenStmt.setString(2, token)
                tokenStmt.setTimestamp(3, Timestamp.valueOf(expiresAt))
                tokenStmt.executeUpdate()
                
                // Simulate sending email
                sendResetEmail(email, token)
                
                OperationResult(true, "Password reset email sent")
            }
        } catch (e: SQLException) {
            OperationResult(false, "Database error: ${e.message}")
        }
    }
    
    fun validateResetToken(token: String): Pair<Boolean, String> {
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement("""
                    SELECT rt.id, rt.user_id, rt.expires_at, rt.used, u.email
                    FROM reset_tokens rt
                    JOIN users u ON rt.user_id = u.id
                    WHERE rt.token = ?
                """)
                stmt.setString(1, token)
                val rs = stmt.executeQuery()
                
                if (!rs.next()) {
                    return Pair(false, "Invalid token")
                }
                
                val used = rs.getBoolean("used")
                val expiresAt = rs.getTimestamp("expires_at").toLocalDateTime()
                
                if (used) {
                    return Pair(false, "Token has already been used")
                }
                
                if (LocalDateTime.now().isAfter(expiresAt)) {
                    return Pair(false, "Token has expired")
                }
                
                Pair(true, "Token is valid")
            }
        } catch (e: SQLException) {
            Pair(false, "Database error: ${e.message}")
        }
    }
    
    fun resetPassword(token: String, newPassword: String, confirmPassword: String): OperationResult {
        if (newPassword != confirmPassword) {
            return OperationResult(false, "Passwords do not match")
        }
        
        val passwordValidator = PasswordValidator()
        val validationResult = passwordValidator.validatePasswordStrength(newPassword)
        if (!validationResult.isSuccess) {
            return validationResult
        }
        
        val (isValidToken, tokenMessage) = validateResetToken(token)
        if (!isValidToken) {
            return OperationResult(false, tokenMessage)
        }
        
        return try {
            DatabaseManager.getConnection().use { conn ->
                conn.autoCommit = false
                
                try {
                    // Get token info
                    val tokenStmt = conn.prepareStatement("""
                        SELECT rt.id, rt.user_id
                        FROM reset_tokens rt
                        WHERE rt.token = ? AND rt.used = FALSE
                    """)
                    tokenStmt.setString(1, token)
                    val tokenRs = tokenStmt.executeQuery()
                    
                    if (!tokenRs.next()) {
                        return OperationResult(false, "Invalid or used token")
                    }
                    
                    val tokenId = tokenRs.getInt("id")
                    val userId = tokenRs.getInt("user_id")
                    
                    // Update user password
                    val userRepository = UserRepository()
                    val newPasswordHash = hashPassword(newPassword)
                    val updateStmt = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")
                    updateStmt.setString(1, newPasswordHash)
                    updateStmt.setInt(2, userId)
                    updateStmt.executeUpdate()
                    
                    // Mark token as used
                    val markUsedStmt = conn.prepareStatement("UPDATE reset_tokens SET used = TRUE WHERE id = ?")
                    markUsedStmt.setInt(1, tokenId)
                    markUsedStmt.executeUpdate()
                    
                    conn.commit()
                    OperationResult(true, "Password reset successfully")
                    
                } catch (e: Exception) {
                    conn.rollback()
                    throw e
                } finally {
                    conn.autoCommit = true
                }
            }
        } catch (e: SQLException) {
            OperationResult(false, "Database error: ${e.message}")
        }
    }
    
    private fun generateResetToken(): String {
        val tokenBytes = ByteArray(32)
        SecureRandom().nextBytes(tokenBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)
    }
    
    private fun sendResetEmail(email: String, token: String) {
        // Simulate email sending
        println("\n--- PASSWORD RESET EMAIL ---")
        println("To: $email")
        println("Subject: Password Reset Request")
        println("")
        println("Click the following link to reset your password:")
        println("Reset Token: $token")
        println("")
        println("This link will expire in 1 hour.")
        println("--- END EMAIL ---\n")
    }
    
    private fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = java.security.MessageDigest.getInstance("SHA-256")
            .digest((password + salt).toByteArray())
            .joinToString("") { "%02x".format(it) }
        return "$salt:$hash"
    }
    
    private fun generateSalt(): String {
        val saltBytes = ByteArray(16)
        SecureRandom().nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }
}
