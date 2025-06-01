package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.tripbook.data.CommentRepository
import com.android.tripbook.data.PostRepository
import com.android.tripbook.data.TravelAgencyRepository
import com.android.tripbook.data.UserRepository

/**
 * Factory for creating ViewModels that require dependencies
 */
class ViewModelFactory(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository? = null,
    private val commentRepository: CommentRepository? = null,
    private val travelAgencyRepository: TravelAgencyRepository? = null
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {            modelClass.isAssignableFrom(ContentViewModel::class.java) -> {
                ContentViewModel(postRepository) as T
            }            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                requireNotNull(userRepository) { "UserRepository is required for UserViewModel" }
                UserViewModel(userRepository) as T
            }            modelClass.isAssignableFrom(CommentViewModel::class.java) -> {
                requireNotNull(commentRepository) { "CommentRepository is required for CommentViewModel" }
                CommentViewModel(commentRepository) as T
            }
            modelClass.isAssignableFrom(TravelAgencyViewModel::class.java) -> {
                requireNotNull(travelAgencyRepository) { "TravelAgencyRepository is required for TravelAgencyViewModel" }
                TravelAgencyViewModel(travelAgencyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
