package com.android.tripbook.repository


import com.android.tripbook.data.models.uSERsUPERcONFIG
import com.android.tripbook.ui.uis.User
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val name: String,
    val destination: String
)

class UserRepository {
    private val client = uSERsUPERcONFIG.client

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            val result = client.from("userstrip").select().decodeList<UserDTO>()
            result.map { User(id = it.id, name = it.name, destination = it.destination) }
        } catch (e: Exception) {
            throw Exception("Failed to fetch users: ${e.message}")
        }
    }

    suspend fun updateUserName(userId: Int, newName: String) = withContext(Dispatchers.IO) {
        try {
            client.from("userstrip").update({
                set("name", newName)
            }) {
                filter { eq("id", userId) }
            }
        } catch (e: Exception) {
            throw Exception("Failed to update user name: ${e.message}")
        }
    }

    suspend fun updateUserDestination(userId: Int, newDestination: String) = withContext(Dispatchers.IO) {
        try {
            client.from("userstrip").update({
                set("destination", newDestination)
            }) {
                filter { eq("id", userId) }
            }
        } catch (e: Exception) {
            throw Exception("Failed to update user destination: ${e.message}")
        }
    }

    companion object {
        private var instance: UserRepository? = null

        fun getInstance(): UserRepository {
            if (instance == null) {
                instance = UserRepository()
            }
            return instance!!
        }
    }
}

