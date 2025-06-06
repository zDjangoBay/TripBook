package com.TripBook.postmodule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

/**
 * Utility functions for post-related operations.
 * Provides helper methods for formatting, calculations, and data processing.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
object PostUtils {
    
    /**
     * Formats a timestamp to a human-readable string
     */
    fun formatTimestamp(timestamp: Long, pattern: String = "MMM dd, yyyy HH:mm"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * Gets a relative time string (e.g., "2 hours ago")
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000} minutes ago"
            diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
            diff < 604_800_000 -> "${diff / 86_400_000} days ago"
            diff < 2_592_000_000 -> "${diff / 604_800_000} weeks ago"
            else -> formatTimestamp(timestamp, "MMM dd, yyyy")
        }
    }
    
    /**
     * Calculates the distance between two geographic points in kilometers
     */
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2).pow(2) + 
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * 
                sin(dLon / 2).pow(2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * Formats distance to human-readable string
     */
    fun formatDistance(distanceKm: Double): String {
        return when {
            distanceKm < 1.0 -> "${(distanceKm * 1000).roundToInt()} m"
            distanceKm < 10.0 -> "${"%.1f".format(distanceKm)} km"
            else -> "${distanceKm.roundToInt()} km"
        }
    }
    
    /**
     * Extracts hashtags from text
     */
    fun extractHashtags(text: String): List<String> {
        val hashtagRegex = "#\\w+".toRegex()
        return hashtagRegex.findAll(text)
            .map { it.value }
            .distinct()
            .toList()
    }
    
    /**
     * Removes hashtags from text
     */
    fun removeHashtags(text: String): String {
        return text.replace("#\\w+".toRegex(), "").trim()
    }
    
    /**
     * Generates a preview text from description
     */
    fun generatePreview(text: String, maxLength: Int = 100): String {
        return if (text.length <= maxLength) {
            text
        } else {
            text.take(maxLength - 3) + "..."
        }
    }
    
    /**
     * Validates coordinate bounds
     */
    fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
    
    /**
     * Formats coordinates to string
     */
    fun formatCoordinates(latitude: Double, longitude: Double): String {
        val latDirection = if (latitude >= 0) "N" else "S"
        val lonDirection = if (longitude >= 0) "E" else "W"
        
        return "${"%.6f".format(abs(latitude))}°$latDirection, " +
               "${"%.6f".format(abs(longitude))}°$lonDirection"
    }
    
    /**
     * Compresses an image bitmap
     */
    fun compressImage(
        context: Context,
        imageUri: Uri,
        quality: Int = PostConstants.MediaLimits.IMAGE_COMPRESSION_QUALITY
    ): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Gets file size from URI
     */
    fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.available().toLong()
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * Formats file size to human-readable string
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
    
    /**
     * Validates image file
     */
    fun isValidImageFile(context: Context, uri: Uri): Boolean {
        return try {
            val mimeType = context.contentResolver.getType(uri)
            mimeType?.startsWith("image/") == true &&
            getFileSize(context, uri) <= PostConstants.MediaLimits.MAX_IMAGE_SIZE_BYTES
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Generates a unique filename
     */
    fun generateUniqueFilename(extension: String = "jpg"): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "post_${timestamp}_${random}.$extension"
    }
    
    /**
     * Sanitizes text for safe storage/display
     */
    fun sanitizeText(text: String): String {
        return text.trim()
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .replace(Regex("[\\r\\n]+"), "\n") // Normalize line breaks
    }
    
    /**
     * Counts words in text
     */
    fun countWords(text: String): Int {
        return text.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .size
    }
    
    /**
     * Estimates reading time in minutes
     */
    fun estimateReadingTime(text: String, wordsPerMinute: Int = 200): Int {
        val wordCount = countWords(text)
        return maxOf(1, (wordCount / wordsPerMinute.toDouble()).roundToInt())
    }
    
    /**
     * Generates tag suggestions based on text content
     */
    fun generateTagSuggestions(text: String): List<String> {
        val words = text.lowercase()
            .split(Regex("\\W+"))
            .filter { it.length > 3 }
            .distinct()
        
        val suggestions = mutableListOf<String>()
        
        // Check against popular tags
        PostConstants.PopularTags.ACTIVITY_TAGS.forEach { tag ->
            if (words.any { it.contains(tag) || tag.contains(it) }) {
                suggestions.add(tag)
            }
        }
        
        PostConstants.PopularTags.MOOD_TAGS.forEach { tag ->
            if (words.any { it.contains(tag) || tag.contains(it) }) {
                suggestions.add(tag)
            }
        }
        
        return suggestions.take(5)
    }
    
    /**
     * Validates and formats a hashtag
     */
    fun formatHashtag(tag: String): String {
        val cleaned = tag.replace(Regex("[^a-zA-Z0-9_]"), "")
        return if (cleaned.isNotEmpty()) "#$cleaned" else ""
    }
    
    /**
     * Checks if text contains profanity (basic implementation)
     */
    fun containsProfanity(text: String): Boolean {
        val profanityWords = setOf("spam", "fake", "scam", "inappropriate")
        val lowerText = text.lowercase()
        return profanityWords.any { lowerText.contains(it) }
    }
    
    /**
     * Generates a color based on category
     */
    fun getCategoryColor(category: String): String {
        return when (category.lowercase()) {
            "adventure" -> "#FF5722"
            "beach" -> "#03A9F4"
            "city break" -> "#9C27B0"
            "cultural" -> "#795548"
            "food & drink" -> "#FF9800"
            "nature" -> "#4CAF50"
            "photography" -> "#607D8B"
            "relaxation" -> "#E91E63"
            "road trip" -> "#F44336"
            "shopping" -> "#9E9E9E"
            "sports" -> "#2196F3"
            "wildlife" -> "#8BC34A"
            else -> "#757575"
        }
    }
    
    /**
     * Converts milliseconds to duration string
     */
    fun formatDuration(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> "${days}d ${hours % 24}h"
            hours > 0 -> "${hours}h ${minutes % 60}m"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
}
