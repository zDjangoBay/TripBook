package com.android.tripbook.data.local.mapper

import com.android.tripbook.data.local.entity.CommentEntity
import com.android.tripbook.data.model.Comment

/**
 * Mapper class to convert between Comment domain model and CommentEntity Room entity
 */
object CommentMapper {
    /**
     * Convert from domain model to entity
     */
    fun toEntity(comment: Comment): CommentEntity {
        return CommentEntity(
            id = comment.id,
            postId = comment.postId,
            userId = comment.userId,
            content = comment.content,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt
        )
    }

    /**
     * Convert from entity to domain model
     */
    fun fromEntity(entity: CommentEntity): Comment {
        return Comment(
            id = entity.id,
            postId = entity.postId,
            userId = entity.userId,
            content = entity.content,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Convert a list of entities to domain models
     */
    fun fromEntities(entities: List<CommentEntity>): List<Comment> {
        return entities.map { fromEntity(it) }
    }
}
