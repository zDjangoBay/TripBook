package com.tripbook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tripbook.domain.usecase.PostUseCase

/**
 * Factory for creating ViewModels with dependencies.
 * This ensures proper dependency injection for ViewModels.
 */
class PostViewModelFactory(
    private val postUseCase: PostUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(postUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

/**
 * Extension function to create PostViewModel with proper dependencies
 */
fun androidx.lifecycle.ViewModelProvider.Factory.Companion.create(
    postUseCase: PostUseCase
): ViewModelProvider.Factory {
    return PostViewModelFactory(postUseCase)
}
