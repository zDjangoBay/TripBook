package data

import java.util.regex.Pattern

class PasswordValidator {
    
    fun validatePasswordStrength(password: String): OperationResult {
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
        
        return OperationResult(
            isSuccess = errors.isEmpty(),
            message = if (errors.isEmpty()) "Password is strong" else errors.joinToString(", ")
        )
    }
    
    fun calculateStrength(password: String): PasswordStrength {
        var score = 0
        val feedback = mutableListOf<String>()
        
        if (password.length >= 8) score += 25
        else feedback.add("At least 8 characters")
        
        if (Pattern.compile("[A-Z]").matcher(password).find()) score += 25
        else feedback.add("One uppercase letter")
        
        if (Pattern.compile("[a-z]").matcher(password).find()) score += 25
        else feedback.add("One lowercase letter")
        
        if (Pattern.compile("\\d").matcher(password).find()) score += 25
        else feedback.add("One number")
        
        if (Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) score += 25
        else feedback.add("One special character")
        
        return PasswordStrength(
            score = minOf(score, 100),
            feedback = feedback
        )
    }
}
