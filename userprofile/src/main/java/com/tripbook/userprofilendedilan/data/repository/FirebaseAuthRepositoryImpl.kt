package com.tripbook.userprofilendedilan.data.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tripbook.userprofilendedilan.domain.repository.AuthRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val maxRetries = 3
    private val delayBetweenRetries = 2000L // 2 seconds

    override suspend fun registerUser(email: String, password: String): Result<Unit> =
        suspendCoroutine { continuation ->
            var retryCount = 0

            fun attemptRegistration() {
                Log.d("FirebaseAuth", "Attempt ${retryCount + 1} to register user: $email")

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d("FirebaseAuth", "Successfully registered user: ${it.user?.uid}")
                        continuation.resume(Result.success(Unit))
                    }
                    .addOnFailureListener { exception ->
                        when {
                            exception is FirebaseNetworkException && retryCount < maxRetries -> {
                                retryCount++
                                Log.w("FirebaseAuth", "Network error, retrying... (Attempt $retryCount)")
                                Thread.sleep(delayBetweenRetries)
                                attemptRegistration()
                            }
                            else -> {
                                Log.e("FirebaseAuth", "Failed to register user after $retryCount retries", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
            }

            attemptRegistration()
        }
}
