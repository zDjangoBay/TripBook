package com.android.tripbook.presentation.auth.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.tripbook.presentation.auth.components.RegistrationFooter
import com.android.tripbook.presentation.auth.viewmodels.RegistrationViewModel
import com.android.tripbook.presentation.navigation.Screen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegistrationViewModel = viewModel()
    val registrationData by viewModel.registrationData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = (viewModel.currentStep + 1).toFloat() / viewModel.totalSteps.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Content with animation
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = viewModel.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        // Moving forward
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(tween(300)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(300)
                                ) + fadeOut(tween(300))
                    } else {
                        // Moving backward
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeIn(tween(300)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(300)
                                ) + fadeOut(tween(300))
                    }
                }
            ) { step ->
                when (step) {
                    0 -> BasicInfoPage(
                        name = registrationData.fullName,
                        email = registrationData.email,
                        password = registrationData.password,
                        nameValidation = viewModel.nameValidation,
                        emailValidation = viewModel.emailValidation,
                        passwordValidation = viewModel.passwordValidation,
                        onInfoUpdated = viewModel::updateBasicInfo
                    )
                    1 -> ProfilePicturePage(
                        currentUri = registrationData.profilePictureUri,
                        onPictureSelected = viewModel::updateProfilePicture
                    )
                    2 -> PersonalDetailsPage(
                        birthDate = registrationData.birthDate,
                        onBirthDateChanged = viewModel::updateBirthDate,
                        bio = registrationData.bio,
                        bioValidation = viewModel.bioValidation,
                        onBioChanged = viewModel::updateBio
                    )
                    3 -> TravelPreferencesPage(
                        preferences = registrationData.travelPreferences,
                        onPreferencesUpdated = viewModel::updateTravelPreferences,
                        destinations = registrationData.favoriteDestinations,
                        onDestinationsUpdated = viewModel::updateFavoriteDestinations
                    )
                }
            }
        }

        // Navigation buttons
        RegistrationFooter(
            currentStep = viewModel.currentStep,
            totalSteps = viewModel.totalSteps,
            onPrevious = viewModel::goToPreviousStep,
            onNext = {
                if (viewModel.currentStep == viewModel.totalSteps - 1) {
                    // On last step, register user
                    viewModel.registerUser(
                        onSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        onError = { /* Handle error */ }
                    )
                } else {
                    // Go to next step
                    viewModel.goToNextStep()
                }
            },
            isLastStep = viewModel.currentStep == viewModel.totalSteps - 1
        )
    }
}
