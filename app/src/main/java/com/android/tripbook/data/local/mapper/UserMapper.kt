package com.android.tripbook.data.local.mapper

import com.android.tripbook.data.local.entity.UserEntity
import com.android.tripbook.data.model.User

/**
 * Mapper class to convert between User domain model and UserEntity Room entity
 */
object UserMapper {
    /**
     * Convert from domain model to entity
     */
    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            username = user.username,
            email = user.email,
            displayName = user.displayName,
            profileImage = user.profileImage,
            bio = user.bio,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    /**
     * Convert from entity to domain model
     */
    fun fromEntity(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            profileImage = entity.profileImage,
            bio = entity.bio,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Convert a list of entities to domain models
     */
    fun fromEntities(entities: List<UserEntity>): List<User> {
        return entities.map { fromEntity(it) }
    }
}
