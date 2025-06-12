package data

import java.security.MessageDigest
import java.security.SecureRandom
import java.sql.SQLException

class UserRepository {
    
    fun signIn(email: String, password: String): OperationResult {
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement("SELECT password_hash FROM users WHERE email = ?")
                stmt.setString(1, email)
                val rs = stmt.executeQuery()
                
                if (rs.next()) {
                    val storedHash = rs.getString("password_hash")
                    if (verifyPassword(password, storedHash)) {
                        OperationResult(true, "Sign in successful")
                    } else {
                        OperationResult(false, "Invalid email or password")
                    }
                } else {
                    OperationResult(false, "Invalid email or password")
                }
            }
        } catch (e: SQLException) {
            OperationResult(false, "Database error: ${e.message}")
        }
    }
    
    fun createUser(email: String, password: String): OperationResult {
        val passwordValidator = PasswordValidator()
        val validationResult = passwordValidator.validatePasswordStrength(password)
        
        if (!validationResult.isSuccess) {
            return validationResult
        }
        
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement("INSERT INTO users (email, password_hash) VALUES (?, ?)")
                stmt.setString(1, email)
                stmt.setString(2, hashPassword(password))
                stmt.executeUpdate()
                
                OperationResult(true, "User created successfully")
            }
        } catch (e: SQLException) {
            if (e.message?.contains("UNIQUE constraint failed") == true) {
                OperationResult(false, "Email already exists")
            } else {
                OperationResult(false, "Database error: ${e.message}")
            }
        }
    }
    
    private fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = MessageDigest.getInstance("SHA-256")
            .digest((password + salt).toByteArray())
            .joinToString("") { "%02x".format(it) }
        return "$salt:$hash"
    }
    
    private fun verifyPassword(password: String, storedHash: String): Boolean {
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
    
    private fun generateSalt(): String {
        val saltBytes = ByteArray(16)
        SecureRandom().nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }
}
