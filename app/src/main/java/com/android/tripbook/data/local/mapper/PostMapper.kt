package com.android.tripbook.data.local.mapper

import com.android.tripbook.data.local.entity.PostEntity
import com.android.tripbook.data.model.Post

/**
 * Mapper class to convert between Post domain model and PostEntity Room entity
 */
object PostMapper {
    /**
     * Convert from domain model to entity
     */
    fun toEntity(post: Post): PostEntity {
        return PostEntity(
            id = post.id,
            userId = post.userId,
            title = post.title,
            description = post.description,
            images = post.images,
            location = post.location,
            latitude = post.latitude,
            longitude = post.longitude,
            tags = post.tags,
            agencyId = post.agencyId,
            likes = post.likes,
            comments = post.comments,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
    }

    /**
     * Convert from entity to domain model
     */
    fun fromEntity(entity: PostEntity): Post {
        return Post(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            description = entity.description,
            images = entity.images,
            location = entity.location,
            latitude = entity.latitude,
            longitude = entity.longitude,
            tags = entity.tags,
            agencyId = entity.agencyId,
            likes = entity.likes,
            comments = entity.comments,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Convert a list of entities to domain models
     */
    fun fromEntities(entities: List<PostEntity>): List<Post> {
        return entities.map { fromEntity(it) }
    }
}
