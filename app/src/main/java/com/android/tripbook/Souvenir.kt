package com.android.tripbook

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "souvenirs")
data class Souvenir(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val description: String,
    val timestamp: Long
)