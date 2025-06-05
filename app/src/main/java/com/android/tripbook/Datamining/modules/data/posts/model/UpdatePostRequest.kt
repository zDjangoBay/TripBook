package com.android.Tripbook.Datamining.modules.data.posts.model

import kotlinx.seriaization.*

@Serializable
data class UpdatePostRequest(
    val value: String?, // Seul le texte du post est généralement modifiable
    val Post_mediasUrl: List<String>? = null, // Ou la gestion des médias via des routes dédiées
    val Localisation: String?,
    val Hashtags: List<String>? = null,
    val mention: List<String>? = null
    val visibility: PostVisibility? = null
)