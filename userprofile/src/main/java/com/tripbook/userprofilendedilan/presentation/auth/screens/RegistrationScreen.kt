package com.tripbook.userprofilendedilan.presentation.auth.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding // Import for keyboard padding
import androidx.compose.foundation.layout.navigationBars // For specific navigation bar insets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars // For specific status bar insets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tripbook.userprofilendedilan.presentation.auth.components.RegistrationFooter
import com.tripbook.userprofilendedilan.presentation.auth.viewmodels.RegistrationViewModel
import com.tripbook.userprofilendedilan.presentation.navigation.Screen

//Take a photo
import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// @OptIn(ExperimentalAnimationApi::class) // Already present, if you use it inside
@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegistrationViewModel = viewModel()
    val registrationData by viewModel.registrationData.collectAsState()

    val context = LocalContext.current

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.handleCameraResult(success)
    }
    
    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, launch camera
            cameraLauncher.launch(viewModel.createImageUri(context))
        } else {
            // Permission denied 
            //TODO
        }
    }
    
    
    
    // Function to handle taking photo
    val handleTakePhoto = remember {
        {
            // Request camera permission
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Scaffold helps manage top bars, bottom bars, content areas, and insets.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Progress indicator - directly under the status bar
            LinearProgressIndicator(
                progress = { (viewModel.currentStep + 1).toFloat() / viewModel.totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars) // Push it below the status bar
                    .padding(vertical = 8.dp) // Your existing padding
            )
        },
        bottomBar = {
            // Navigation buttons - fixed at the bottom, above system navigation
            RegistrationFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    // This pushes the footer up by the height of the system navigation bar
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    // This pushes the footer up when the keyboard is shown
                    .imePadding(),
                currentStep = viewModel.currentStep,
                totalSteps = viewModel.totalSteps,
                onPrevious = viewModel::goToPreviousStep,
                onNext = {
                    if (viewModel.currentStep == viewModel.totalSteps - 1) {
                        viewModel.registerUser(
                            onSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            },
                            onError = { /* Handle error */ }
                        )
                    } else {
                        viewModel.goToNextStep()
                    }
                },
                isLastStep = viewModel.currentStep == viewModel.totalSteps - 1,
                isNextEnabled = viewModel.isCurrentStepValid.value
            )
        }

    ) { innerPadding -> // This padding is provided by Scaffold for its content area
        // Content with animation, placed within the Scaffold's content area
        Box(

            modifier = Modifier
                .padding(innerPadding) // Respects Scaffold's top/bottom bar space
                .fillMaxSize()

                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = viewModel.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                                fadeIn(tween(300)) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                                fadeOut(tween(300))
                    } else {
                        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                                fadeIn(tween(300)) togetherWith
                                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                                fadeOut(tween(300))
                    }
                },
                // Make AnimatedContent fill the available space within the Box
                modifier = Modifier.fillMaxSize()
            ) { step ->
                // Your pages - ensure they handle their own internal scrolling and padding
                // if they contain lists or content that needs to go to the edge.
                when (step) {
                   0 -> BasicInfoPage(
                       name = registrationData.fullName,
                       email = registrationData.email,
                       phone = registrationData.phone,
                       password = registrationData.password,
                       confirmPassword = registrationData.confirmPassword,
                       nameValidation = viewModel.nameValidation,
                       emailValidation = viewModel.emailValidation,
                       phoneValidation = viewModel.phoneValidation,
                       passwordValidation = viewModel.passwordValidation,
                       confirmPasswordValidation = viewModel.confirmPasswordValidation,
                       nameTouched = viewModel.nameTouched,
                       emailTouched = viewModel.emailTouched,
                       phoneTouched = viewModel.phoneTouched,
                       passwordTouched = viewModel.passwordTouched,
                       confirmPasswordTouched = viewModel.confirmPasswordTouched,
                       onInfoUpdated = viewModel::updateBasicInfoWithoutValidation,
                       onFieldTouched = { field ->
                           when (field) {
                               "name" -> viewModel.markNameTouched()
                               "email" -> viewModel.markEmailTouched()
                               "phone" -> viewModel.markPhoneTouched()
                               "password" -> viewModel.markPasswordTouched()
                               "confirmPassword" -> viewModel.markConfirmPasswordTouched()
                           }
                       },
                       onFieldBlur = { field ->
                           val data = registrationData
                           when (field) {
                               "name" -> viewModel.validateNameOnBlur(data.fullName)
                               "email" -> viewModel.validateEmailOnBlur(data.email)
                               "phone" -> viewModel.validatePhoneOnBlur(data.phone)
                               "password" -> viewModel.validatePasswordOnBlur(data.password)
                               "confirmPassword" -> viewModel.validateConfirmPasswordOnBlur(
                                   data.password,
                                   data.confirmPassword
                               )
                           }
                       },
                       onSignUp = {
                           // Validate all fields and go to next step if valid
                           viewModel.goToNextStep()
                       },
                       isSignUpEnabled = viewModel.isCurrentStepValid.value
                   )
                    1 -> ProfilePicturePage(
                        currentUri = registrationData.profilePictureUri,
                        onPictureSelected = viewModel::updateProfilePicture,
                        onSkip = viewModel::goToNextStep,
                        onTakePhoto = handleTakePhoto, 
                        onBack = if (viewModel.currentStep > 0) viewModel::goToPreviousStep else null

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
                    // It's good practice to have an else or default case
                    else -> {
                        // Handle unknown step, maybe show an error or empty state
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            // Text("Unknown registration step")
                        }
                    }
                }
            }
        }
    }
}
