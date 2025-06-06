package com.android.tripbook.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: Int,
    val name: String,
    val icon: ImageVector? = null
)