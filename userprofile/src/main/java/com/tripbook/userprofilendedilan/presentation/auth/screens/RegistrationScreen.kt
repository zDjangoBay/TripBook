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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.tripbook.userprofilendedilan.presentation.auth.components.RegistrationFooter
import com.tripbook.userprofilendedilan.presentation.auth.viewmodels.RegistrationViewModel
import com.tripbook.userprofilendedilan.presentation.navigation.Screen

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegistrationViewModel = viewModel()
    val registrationData by viewModel.registrationData.collectAsState()
    var showError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
            cameraLauncher.launch(viewModel.createImageUri(context))
        } else {
            showError = "Camera permission is required to take a profile picture"
        }
    }

    val handleTakePhoto = remember {
        {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LinearProgressIndicator(
                progress = { (viewModel.currentStep + 1).toFloat() / viewModel.totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(vertical = 8.dp)
            )
        },
        bottomBar = {
            RegistrationFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding(),
                currentStep = viewModel.currentStep,
                totalSteps = viewModel.totalSteps,
                onPrevious = viewModel::goToPreviousStep,
                onNext = {
                    if (viewModel.currentStep == viewModel.totalSteps - 1) {
                        isLoading = true
                        viewModel.registerUser(
                            onSuccess = {
                                isLoading = false
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            },
                            onError = { error ->
                                isLoading = false
                                showError = error
                            }
                        )
                    } else {
                        viewModel.goToNextStep()
                    }
                },
                isLastStep = viewModel.currentStep == viewModel.totalSteps - 1,
                isNextEnabled = viewModel.isCurrentStepValid.value && !isLoading
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

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
                modifier = Modifier.fillMaxSize()
            ) { step ->
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
                        onFieldTouched = { field: String ->
                            when (field) {
                                "name" -> viewModel.markNameTouched()
                                "email" -> viewModel.markEmailTouched()
                                "phone" -> viewModel.markPhoneTouched()
                                "password" -> viewModel.markPasswordTouched()
                                "confirmPassword" -> viewModel.markConfirmPasswordTouched()
                            }
                        },
                        onFieldBlur = { field: String ->
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
                            viewModel.goToNextStep()
                        },
                        isSignUpEnabled = viewModel.isCurrentStepValid.value
                    )
                    1 -> ProfilePicturePage(
                        currentUri = registrationData.profilePictureUri,
                        onTakePhoto = handleTakePhoto,
                        onPictureSelected = viewModel::updateProfilePicture,
                        onSkip = viewModel::goToNextStep,
                        onBack = viewModel::goToPreviousStep
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

            showError?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { showError = null }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}
