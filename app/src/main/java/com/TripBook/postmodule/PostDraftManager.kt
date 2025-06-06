package com.TripBook.postmodule

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Manages draft saving and loading for post creation.
 * Provides automatic and manual draft management capabilities.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostDraftManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "post_drafts", Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    companion object {
        private const val DRAFT_KEY_PREFIX = "draft_"
        private const val LAST_DRAFT_KEY = "last_draft_id"
        private const val AUTO_SAVE_INTERVAL = 30_000L // 30 seconds
        private const val MAX_DRAFTS = 10
    }
    
    /**
     * Saves a draft of the current post state
     */
    suspend fun saveDraft(
        title: String,
        description: String,
        images: List<Uri>,
        location: PostLocationData?,
        category: String,
        tags: List<String>,
        visibility: String,
        draftId: String? = null
    ): String = withContext(Dispatchers.IO) {
        val id = draftId ?: generateDraftId()
        
        val draft = PostDraft(
            id = id,
            title = title,
            description = description,
            imageUris = images.map { it.toString() },
            location = location,
            category = category,
            tags = tags,
            visibility = visibility,
            createdAt = System.currentTimeMillis(),
            lastModified = System.currentTimeMillis()
        )
        
        val draftJson = json.encodeToString(draft)
        prefs.edit()
            .putString("$DRAFT_KEY_PREFIX$id", draftJson)
            .putString(LAST_DRAFT_KEY, id)
            .apply()
        
        cleanupOldDrafts()
        id
    }
    
    /**
     * Loads a draft by ID
     */
    suspend fun loadDraft(draftId: String): PostDraft? = withContext(Dispatchers.IO) {
        val draftJson = prefs.getString("$DRAFT_KEY_PREFIX$draftId", null)
        draftJson?.let { 
            try {
                json.decodeFromString<PostDraft>(it)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Loads the last saved draft
     */
    suspend fun loadLastDraft(): PostDraft? = withContext(Dispatchers.IO) {
        val lastDraftId = prefs.getString(LAST_DRAFT_KEY, null)
        lastDraftId?.let { loadDraft(it) }
    }
    
    /**
     * Gets all saved drafts
     */
    suspend fun getAllDrafts(): List<PostDraft> = withContext(Dispatchers.IO) {
        val drafts = mutableListOf<PostDraft>()
        
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(DRAFT_KEY_PREFIX) && value is String) {
                try {
                    val draft = json.decodeFromString<PostDraft>(value)
                    drafts.add(draft)
                } catch (e: Exception) {
                    // Ignore corrupted drafts
                }
            }
        }
        
        drafts.sortedByDescending { it.lastModified }
    }
    
    /**
     * Deletes a draft by ID
     */
    suspend fun deleteDraft(draftId: String): Boolean = withContext(Dispatchers.IO) {
        val editor = prefs.edit()
        val existed = prefs.contains("$DRAFT_KEY_PREFIX$draftId")
        
        editor.remove("$DRAFT_KEY_PREFIX$draftId")
        
        // If this was the last draft, clear the reference
        if (prefs.getString(LAST_DRAFT_KEY, null) == draftId) {
            editor.remove(LAST_DRAFT_KEY)
        }
        
        editor.apply()
        existed
    }
    
    /**
     * Deletes all drafts
     */
    suspend fun deleteAllDrafts(): Int = withContext(Dispatchers.IO) {
        val draftKeys = prefs.all.keys.filter { it.startsWith(DRAFT_KEY_PREFIX) }
        val count = draftKeys.size
        
        val editor = prefs.edit()
        draftKeys.forEach { editor.remove(it) }
        editor.remove(LAST_DRAFT_KEY)
        editor.apply()
        
        count
    }
    
    /**
     * Checks if a draft exists
     */
    fun hasDraft(draftId: String): Boolean {
        return prefs.contains("$DRAFT_KEY_PREFIX$draftId")
    }
    
    /**
     * Checks if any drafts exist
     */
    fun hasAnyDrafts(): Boolean {
        return prefs.all.keys.any { it.startsWith(DRAFT_KEY_PREFIX) }
    }
    
    /**
     * Gets the number of saved drafts
     */
    fun getDraftCount(): Int {
        return prefs.all.keys.count { it.startsWith(DRAFT_KEY_PREFIX) }
    }
    
    /**
     * Auto-saves a draft if enough time has passed
     */
    suspend fun autoSaveDraft(
        title: String,
        description: String,
        images: List<Uri>,
        location: PostLocationData?,
        category: String,
        tags: List<String>,
        visibility: String,
        lastAutoSaveTime: Long
    ): String? {
        val currentTime = System.currentTimeMillis()
        
        return if (currentTime - lastAutoSaveTime >= AUTO_SAVE_INTERVAL) {
            // Only auto-save if there's meaningful content
            if (title.isNotBlank() || description.isNotBlank() || images.isNotEmpty()) {
                saveDraft(title, description, images, location, category, tags, visibility)
            } else {
                null
            }
        } else {
            null
        }
    }
    
    /**
     * Generates a unique draft ID
     */
    private fun generateDraftId(): String {
        return "draft_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
    }
    
    /**
     * Cleans up old drafts to maintain storage limits
     */
    private fun cleanupOldDrafts() {
        val allDrafts = prefs.all.entries
            .filter { it.key.startsWith(DRAFT_KEY_PREFIX) }
            .mapNotNull { entry ->
                try {
                    val draft = json.decodeFromString<PostDraft>(entry.value as String)
                    entry.key to draft
                } catch (e: Exception) {
                    null
                }
            }
            .sortedByDescending { it.second.lastModified }
        
        if (allDrafts.size > MAX_DRAFTS) {
            val editor = prefs.edit()
            allDrafts.drop(MAX_DRAFTS).forEach { (key, _) ->
                editor.remove(key)
            }
            editor.apply()
        }
    }
    
    /**
     * Exports draft data for backup
     */
    suspend fun exportDrafts(): String = withContext(Dispatchers.IO) {
        val drafts = getAllDrafts()
        json.encodeToString(drafts)
    }
    
    /**
     * Imports draft data from backup
     */
    suspend fun importDrafts(jsonData: String): Int = withContext(Dispatchers.IO) {
        try {
            val drafts = json.decodeFromString<List<PostDraft>>(jsonData)
            val editor = prefs.edit()
            
            drafts.forEach { draft ->
                val draftJson = json.encodeToString(draft)
                editor.putString("$DRAFT_KEY_PREFIX${draft.id}", draftJson)
            }
            
            editor.apply()
            drafts.size
        } catch (e: Exception) {
            0
        }
    }
}

/**
 * Represents a saved post draft
 */
@Serializable
data class PostDraft(
    val id: String,
    val title: String,
    val description: String,
    val imageUris: List<String>,
    val location: PostLocationData?,
    val category: String,
    val tags: List<String>,
    val visibility: String,
    val createdAt: Long,
    val lastModified: Long
) {
    /**
     * Converts URI strings back to Uri objects
     */
    fun getImageUris(): List<Uri> {
        return imageUris.mapNotNull { 
            try {
                Uri.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Checks if the draft has meaningful content
     */
    fun hasContent(): Boolean {
        return title.isNotBlank() || 
               description.isNotBlank() || 
               imageUris.isNotEmpty() ||
               location != null ||
               tags.isNotEmpty()
    }
    
    /**
     * Gets a preview of the draft content
     */
    fun getPreview(): String {
        return when {
            title.isNotBlank() -> title.take(50)
            description.isNotBlank() -> description.take(50)
            else -> "Untitled Draft"
        }
    }
}
