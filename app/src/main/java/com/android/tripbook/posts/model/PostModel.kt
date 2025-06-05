package com.android.tripbook.posts.model

import com.android.tripbook.data.model.TravelLocation // Assure-toi que cet import est présent
import java.time.Instant
import java.util.UUID

data class PostModel(
    val id: String = UUID.randomUUID().toString(), // Génère un ID par défaut (depuis la branche posts)
    val userId: String,
    val username: String,
    val userAvatar: String? = null, // Valeur par défaut (depuis posts)
    val isVerified: Boolean = false, // Valeur par défaut (depuis posts)
    val title: String,
    val description: String,
    val images: List<ImageModel> = emptyList(), 
    val location: TravelLocation, 
    val tags: List<TagModel> = emptyList(), 
    val hashtags: List<String> = emptyList(), 
    val createdAt: Instant = Instant.now(),
    val lastEditedAt: Instant? = null, 
    val visibility: PostVisibility, 
    val collaborators: List<UserMinimal>? = emptyList(), 
    val isEphemeral: Boolean = false,
    val ephemeralDurationMillis: Long? = null,
    val likes: List<String> = emptyList(), 
    val comments: List<Comment> = emptyList() 
)

enum class PostVisibility {
    PUBLIC, FRIENDS, PRIVATE
}

data class UserMinimal(
    val id: String,
    val username: String
)

/*
Note : La classe 'Category' est définie dans TagModel.kt si elle est utilisée.
J'ai supprimé 'categories: List<Category>' de cette PostModel car elle n'était pas utilisée
dans les screens de posts que nous avons développés (nous utilisions 'tags').
Si 'categories' est censée être une liste de catégories pour le post lui-même,
alors il faudrait la rajouter ici et adapter en conséquence.
*/