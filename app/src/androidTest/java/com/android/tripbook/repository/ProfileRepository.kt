package com.tripbook.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tripbook.data.model.TravelerProfile
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getProfile(): TravelerProfile? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("profiles").document(uid).get().await()
        return doc.toObject(TravelerProfile::class.java)
    }

    suspend fun updateProfile(profile: TravelerProfile) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("profiles").document(uid).set(profile).await()
    }
}
