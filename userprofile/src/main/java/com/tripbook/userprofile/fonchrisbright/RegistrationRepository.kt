package com.tripbook.userprofile.fonchrisbright

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RegistrationRepository(private val api: RegistrationApi) {
    suspend fun registerUser(email: String, password: String, name: String): RegistrationState {
        return api.registerUser(email, password, name)
    }
}

class RegistrationApi(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    suspend fun registerUser(email: String, password: String, name: String): RegistrationState {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            user?.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            if (user != null) {
                RegistrationState.Success(user.uid)
            } else {
                RegistrationState.Error("Registration failed")
            }
        } catch (e: Exception) {
            RegistrationState.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}

