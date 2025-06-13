package com.tripbook.userprofileManfoIngrid.domain.repository

import com.tripbook.userprofileManfoIngrid.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getMediaItems(): Flow<List<MediaItem>>
    suspend fun deleteMedia(mediaId: String): Result<Unit>
    suspend fun updateMediaName(mediaId: String, newName: String): Result<MediaItem>
    suspend fun shareMedia(mediaItem: MediaItem, platform: String): Result<Unit>
}