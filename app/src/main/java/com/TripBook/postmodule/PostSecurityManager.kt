package com.TripBook.postmodule

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

/**
 * Security manager for post-related operations.
 * Handles content filtering, data encryption, and security validations.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostSecurityManager(private val context: Context) {
    
    companion object {
        private const val ENCRYPTION_ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/ECB/PKCS1Padding"
        private const val HASH_ALGORITHM = "SHA-256"
        private const val MAX_CONTENT_LENGTH = 10000
        private const val MAX_LOCATION_PRECISION = 3 // decimal places
        
        // Security patterns
        private val SUSPICIOUS_PATTERNS = listOf(
            "(?i)\\b(hack|crack|exploit|malware|virus)\\b".toRegex(),
            "(?i)\\b(spam|scam|phishing|fraud)\\b".toRegex(),
            "(?i)\\b(password|credit card|ssn|social security)\\b".toRegex()
        )
        
        private val PROFANITY_WORDS = setOf(
            "spam", "fake", "scam", "inappropriate", "offensive",
            "harassment", "bullying", "hate", "discrimination"
        )
        
        private val SAFE_DOMAINS = setOf(
            "tripbook.com", "google.com", "maps.google.com",
            "wikipedia.org", "tripadvisor.com", "booking.com"
        )
    }
    
    /**
     * Validates and sanitizes post content for security
     */
    fun validatePostContent(
        title: String,
        description: String,
        tags: List<String>
    ): ContentValidationResult {
        val issues = mutableListOf<SecurityIssue>()
        
        // Check content length
        if (title.length + description.length > MAX_CONTENT_LENGTH) {
            issues.add(SecurityIssue.CONTENT_TOO_LONG)
        }
        
        // Check for suspicious patterns
        val allText = "$title $description ${tags.joinToString(" ")}"
        SUSPICIOUS_PATTERNS.forEach { pattern ->
            if (pattern.containsMatchIn(allText)) {
                issues.add(SecurityIssue.SUSPICIOUS_CONTENT)
            }
        }
        
        // Check for profanity
        if (containsProfanity(allText)) {
            issues.add(SecurityIssue.INAPPROPRIATE_CONTENT)
        }
        
        // Check for potential spam
        if (isSpamLike(title, description, tags)) {
            issues.add(SecurityIssue.SPAM_DETECTED)
        }
        
        // Check for personal information
        if (containsPersonalInfo(allText)) {
            issues.add(SecurityIssue.PERSONAL_INFO_DETECTED)
        }
        
        return ContentValidationResult(
            isValid = issues.isEmpty(),
            issues = issues,
            sanitizedTitle = sanitizeText(title),
            sanitizedDescription = sanitizeText(description),
            sanitizedTags = tags.map { sanitizeText(it) }
        )
    }
    
    /**
     * Validates location data for privacy and security
     */
    fun validateLocationData(
        latitude: Double,
        longitude: Double,
        locationName: String?
    ): LocationValidationResult {
        val issues = mutableListOf<SecurityIssue>()
        
        // Check coordinate bounds
        if (!PostUtils.isValidCoordinate(latitude, longitude)) {
            issues.add(SecurityIssue.INVALID_COORDINATES)
        }
        
        // Check for overly precise coordinates (privacy concern)
        val latPrecision = getDecimalPlaces(latitude)
        val lonPrecision = getDecimalPlaces(longitude)
        
        if (latPrecision > MAX_LOCATION_PRECISION || lonPrecision > MAX_LOCATION_PRECISION) {
            issues.add(SecurityIssue.LOCATION_TOO_PRECISE)
        }
        
        // Validate location name
        locationName?.let { name ->
            if (containsPersonalInfo(name) || containsProfanity(name)) {
                issues.add(SecurityIssue.INAPPROPRIATE_LOCATION_NAME)
            }
        }
        
        return LocationValidationResult(
            isValid = issues.isEmpty(),
            issues = issues,
            sanitizedLatitude = roundCoordinate(latitude),
            sanitizedLongitude = roundCoordinate(longitude),
            sanitizedLocationName = locationName?.let { sanitizeText(it) }
        )
    }
    
    /**
     * Encrypts sensitive data for storage
     */
    fun encryptSensitiveData(data: String, key: String? = null): String {
        return try {
            val secretKey = key?.let { 
                SecretKeySpec(it.toByteArray(), ENCRYPTION_ALGORITHM) 
            } ?: generateSecretKey()
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val encryptedBytes = cipher.doFinal(data.toByteArray())
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            data // Return original if encryption fails
        }
    }
    
    /**
     * Decrypts sensitive data
     */
    fun decryptSensitiveData(encryptedData: String, key: String): String {
        return try {
            val secretKey = SecretKeySpec(key.toByteArray(), ENCRYPTION_ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            
            val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            encryptedData // Return original if decryption fails
        }
    }
    
    /**
     * Generates a hash for content integrity verification
     */
    fun generateContentHash(content: String): String {
        return try {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            val hashBytes = digest.digest(content.toByteArray())
            Base64.encodeToString(hashBytes, Base64.DEFAULT).trim()
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Validates URLs for safety
     */
    fun validateUrl(url: String): UrlValidationResult {
        val issues = mutableListOf<SecurityIssue>()
        
        try {
            val uri = java.net.URI(url)
            val host = uri.host?.lowercase()
            
            // Check if HTTPS
            if (uri.scheme != "https") {
                issues.add(SecurityIssue.INSECURE_URL)
            }
            
            // Check against safe domains
            if (host != null && !SAFE_DOMAINS.any { host.endsWith(it) }) {
                issues.add(SecurityIssue.UNTRUSTED_DOMAIN)
            }
            
            // Check for suspicious URL patterns
            if (url.contains("bit.ly") || url.contains("tinyurl") || url.contains("t.co")) {
                issues.add(SecurityIssue.SHORTENED_URL)
            }
            
        } catch (e: Exception) {
            issues.add(SecurityIssue.MALFORMED_URL)
        }
        
        return UrlValidationResult(
            isValid = issues.isEmpty(),
            issues = issues
        )
    }
    
    /**
     * Generates a secure session token
     */
    fun generateSecureToken(length: Int = 32): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = SecureRandom()
        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }
    
    /**
     * Validates image metadata for security
     */
    fun validateImageMetadata(imagePath: String): ImageValidationResult {
        val issues = mutableListOf<SecurityIssue>()
        
        // Check file extension
        val extension = imagePath.substringAfterLast('.', "").lowercase()
        val allowedExtensions = setOf("jpg", "jpeg", "png", "webp")
        
        if (extension !in allowedExtensions) {
            issues.add(SecurityIssue.INVALID_FILE_TYPE)
        }
        
        // Check for suspicious file names
        if (imagePath.contains("..") || imagePath.contains("script") || imagePath.contains("exe")) {
            issues.add(SecurityIssue.SUSPICIOUS_FILENAME)
        }
        
        return ImageValidationResult(
            isValid = issues.isEmpty(),
            issues = issues
        )
    }
    
    private fun containsProfanity(text: String): Boolean {
        val lowerText = text.lowercase()
        return PROFANITY_WORDS.any { lowerText.contains(it) }
    }
    
    private fun containsPersonalInfo(text: String): Boolean {
        // Simple patterns for common personal info
        val patterns = listOf(
            "\\b\\d{3}-\\d{2}-\\d{4}\\b".toRegex(), // SSN pattern
            "\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b".toRegex(), // Credit card pattern
            "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b".toRegex() // Email pattern
        )
        
        return patterns.any { it.containsMatchIn(text) }
    }
    
    private fun isSpamLike(title: String, description: String, tags: List<String>): Boolean {
        // Check for excessive repetition
        val allText = "$title $description ${tags.joinToString(" ")}"
        val words = allText.split("\\s+".toRegex())
        val uniqueWords = words.distinct()
        
        // If less than 50% unique words, might be spam
        if (words.size > 10 && uniqueWords.size.toDouble() / words.size < 0.5) {
            return true
        }
        
        // Check for excessive capitalization
        val upperCaseCount = allText.count { it.isUpperCase() }
        if (upperCaseCount > allText.length * 0.7) {
            return true
        }
        
        // Check for excessive special characters
        val specialCharCount = allText.count { !it.isLetterOrDigit() && !it.isWhitespace() }
        if (specialCharCount > allText.length * 0.3) {
            return true
        }
        
        return false
    }
    
    private fun sanitizeText(text: String): String {
        return text
            .replace(Regex("[<>\"'&]"), "") // Remove potentially dangerous characters
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()
    }
    
    private fun getDecimalPlaces(number: Double): Int {
        val str = number.toString()
        val decimalIndex = str.indexOf('.')
        return if (decimalIndex == -1) 0 else str.length - decimalIndex - 1
    }
    
    private fun roundCoordinate(coordinate: Double): Double {
        val factor = 10.0.pow(MAX_LOCATION_PRECISION.toDouble())
        return round(coordinate * factor) / factor
    }
    
    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM)
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }
}

enum class SecurityIssue {
    CONTENT_TOO_LONG,
    SUSPICIOUS_CONTENT,
    INAPPROPRIATE_CONTENT,
    SPAM_DETECTED,
    PERSONAL_INFO_DETECTED,
    INVALID_COORDINATES,
    LOCATION_TOO_PRECISE,
    INAPPROPRIATE_LOCATION_NAME,
    INSECURE_URL,
    UNTRUSTED_DOMAIN,
    SHORTENED_URL,
    MALFORMED_URL,
    INVALID_FILE_TYPE,
    SUSPICIOUS_FILENAME
}

data class ContentValidationResult(
    val isValid: Boolean,
    val issues: List<SecurityIssue>,
    val sanitizedTitle: String,
    val sanitizedDescription: String,
    val sanitizedTags: List<String>
)

data class LocationValidationResult(
    val isValid: Boolean,
    val issues: List<SecurityIssue>,
    val sanitizedLatitude: Double,
    val sanitizedLongitude: Double,
    val sanitizedLocationName: String?
)

data class UrlValidationResult(
    val isValid: Boolean,
    val issues: List<SecurityIssue>
)

data class ImageValidationResult(
    val isValid: Boolean,
    val issues: List<SecurityIssue>
)
