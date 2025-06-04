package com.android.Tripbook.Datamining.modules.data.tripcatalog.models

import java.io.Serializable

data class Trip(
    val id: Int,
    val title: String,
    val caption: String,
    val description: String,
    val imageUrl: List<String>
) : Serializable
