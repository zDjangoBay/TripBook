package com.android.tripbook.src.main.kotlin.userprofile.model

import com.android.tripbook.src.main.kotlin.userprofile.model.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import redis.clients.jedis.Jedis
import java.util.UUID

class UserProfileServiceImpl(
    private val db: CoroutineDatabase,
    private val redis: Jedis,
    private val json: Json
) : UserProfileService {

    private val collection = db.getCollection<UserProfile>("UserProfiles")

    override suspend fun createUserProfile(request: CreateUserProfileRequest): UserProfile {
        val newUser = UserProfile(
            userId = UUID.randomUUID().toString(),
            fullName = request.fullName,
            email = request.email,
            phoneNumber = request.phoneNumber,
            age = request.age,
            gender = request.gender,
            profileImageUrl = request.profileImageUrl
        )
        collection.insertOne(newUser)
        redis.setex("userprofile:${newUser.userId}", 3600, json.encodeToString(UserProfile.serializer(), newUser))
        return newUser
    }

    override suspend fun getUserProfileById(userId: String): UserProfile? {
        val cached = redis.get("userprofile:$userId")
        if (cached != null) {
            return json.decodeFromString(UserProfile.serializer(), cached)
        }
        val profile = collection.findOneById(userId)
        profile?.let {
            redis.setex("userprofile:$userId", 3600, json.encodeToString(UserProfile.serializer(), it))
        }
        return profile
    }

    override suspend fun updateUserProfile(userId: String, request: UpdateUserProfileRequest): UserProfile? {
        val existing = collection.findOneById(userId) ?: return null
        val updated = existing.copy(
            fullName = request.fullName ?: existing.fullName,
            phoneNumber = request.phoneNumber ?: existing.phoneNumber,
            age = request.age ?: existing.age,
            gender = request.gender ?: existing.gender,
            profileImageUrl = request.profileImageUrl ?: existing.profileImageUrl
        )
        collection.updateOneById(userId, updated)
        redis.setex("userprofile:$userId", 3600, json.encodeToString(UserProfile.serializer(), updated))
        return updated
    }

    override suspend fun deleteUserProfile(userId: String): Boolean {
        val deleted = collection.deleteOneById(userId)
        redis.del("userprofile:$userId")
        return deleted.deletedCount > 0
    }

    override suspend fun getAllUserProfiles(page: Int, pageSize: Int): List<UserProfile> {
        return collection.find()
            .skip((page - 1) * pageSize)
            .limit(pageSize)
            .toList()
    }
}
