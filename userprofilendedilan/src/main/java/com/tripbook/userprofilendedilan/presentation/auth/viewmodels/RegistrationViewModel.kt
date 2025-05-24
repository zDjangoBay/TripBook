package com.tripbook.userprofilendedilan.presentation.auth.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tripbook.userprofilendedilan.domain.model.UserRegistrationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Validation helpers
import androidx.compose.runtime.derivedStateOf
import com.tripbook.userprofilendedilan.util.ValidationResult
import com.tripbook.userprofilendedilan.util.ValidationUtil

//Take a photo
import android.net.Uri
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegistrationViewModel : ViewModel() {



    private val _nameTouched = MutableStateFlow(false)
    val nameTouched: StateFlow<Boolean> = _nameTouched.asStateFlow()

    private val _emailTouched = MutableStateFlow(false)
    val emailTouched: StateFlow<Boolean> = _emailTouched.asStateFlow()

    private val _phoneTouched = MutableStateFlow(false)
    val phoneTouched: StateFlow<Boolean> = _phoneTouched.asStateFlow()

    private val _passwordTouched = MutableStateFlow(false)
    val passwordTouched: StateFlow<Boolean> = _passwordTouched.asStateFlow()

    private val _confirmPasswordTouched = MutableStateFlow(false)
    val confirmPasswordTouched: StateFlow<Boolean> = _confirmPasswordTouched.asStateFlow()

    private val _bioTouched = MutableStateFlow(false)
    val bioTouched: StateFlow<Boolean> = _bioTouched.asStateFlow()
    // Store form data
    private val _registrationData = MutableStateFlow(UserRegistrationData())
    val registrationData: StateFlow<UserRegistrationData> = _registrationData.asStateFlow()

    // Mark field as touched
    fun markNameTouched() {
        _nameTouched.value = true
    }

    fun markEmailTouched() {
        _emailTouched.value = true
    }

    fun markPhoneTouched() {
        _phoneTouched.value = true
    }

    fun markPasswordTouched() {
        _passwordTouched.value = true
    }

    fun markConfirmPasswordTouched() {
        _confirmPasswordTouched.value = true
    }

    fun markBioTouched() {
        _bioTouched.value = true
    }



    // Current registration step
    var currentStep by mutableStateOf(0)
        private set

    // Total number of steps
    val totalSteps = 4

    // Form validation states
    var nameValidation by mutableStateOf(ValidationResult(true))
        private set

    var emailValidation by mutableStateOf(ValidationResult(true))
        private set

    var passwordValidation by mutableStateOf(ValidationResult(true))
        private set
    
     var phoneValidation by mutableStateOf(ValidationResult(true))
        private set

    var confirmPasswordValidation by mutableStateOf(ValidationResult(true))
        private set

    var bioValidation by mutableStateOf(ValidationResult(true))
        private set

    // Computed property to check if current step is valid
    val isCurrentStepValid = derivedStateOf {
        when (currentStep) {
            0 -> nameValidation.isValid &&
                 emailValidation.isValid &&
                 phoneValidation.isValid &&
                 passwordValidation.isValid &&
                 confirmPasswordValidation.isValid
            1 -> true
            2 -> bioValidation.isValid
            3 -> true
            else -> false
        }
    }

    // Update function to handle all fields
    fun updateBasicInfo(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        nameValidation = ValidationUtil.validateName(name)
        emailValidation = ValidationUtil.validateEmail(email)
        phoneValidation = ValidationUtil.validatePhone(phone)
        passwordValidation = ValidationUtil.validatePassword(password)
        confirmPasswordValidation = ValidationUtil.validateConfirmPassword(password, confirmPassword)

        _registrationData.update { currentData ->
            currentData.copy(
                fullName = name,
                email = email,
                phone = phone,
                password = password,
                confirmPassword = confirmPassword
            )
        }
    }

    fun updateBio(bio: String) {
        // Validate bio
        bioValidation = ValidationUtil.validateBio(bio)

        // Update data
        _registrationData.update { currentData ->
            currentData.copy(bio = bio)
        }
    }

    // Only allow navigation to next step if current step is valid
    fun goToNextStep() {
       when (currentStep) {
            0 -> validateAllBasicInfo()
            2 -> validateBioOnBlur(_registrationData.value.bio)
        }
            
        if (isCurrentStepValid.value && currentStep < totalSteps - 1) {
            currentStep++
        }
    }

    fun updateProfilePicture(uri: String?) {
        _registrationData.update { currentData ->
            currentData.copy(profilePictureUri = uri)
        }
    }

    fun updateBirthDate(date: String) {
        _registrationData.update { currentData ->
            currentData.copy(birthDate = date)
        }
    }

    fun updateTravelPreferences(preferences: List<String>) {
        _registrationData.update { currentData ->
            currentData.copy(travelPreferences = preferences)
        }
    }

    fun updateFavoriteDestinations(destinations: List<String>) {
        _registrationData.update { currentData ->
            currentData.copy(favoriteDestinations = destinations)
        }
    }

    fun goToPreviousStep() {
        if (currentStep > 0) {
            currentStep--
        }
    }

     // Validate specific fields on focus loss
    fun validateNameOnBlur(name: String) {
        if (_nameTouched.value) {
            nameValidation = ValidationUtil.validateName(name)
        }
    }

    fun validateEmailOnBlur(email: String) {
        if (_emailTouched.value) {
            emailValidation = ValidationUtil.validateEmail(email)
        }
    }

    fun validatePhoneOnBlur(phone: String) {
        if (_phoneTouched.value) {
            phoneValidation = ValidationUtil.validatePhone(phone)
        }
    }

    fun validatePasswordOnBlur(password: String) {
        if (_passwordTouched.value) {
            passwordValidation = ValidationUtil.validatePassword(password)
        }
    }

    fun validateConfirmPasswordOnBlur(password: String, confirmPassword: String) {
        if (_confirmPasswordTouched.value) {
            confirmPasswordValidation = ValidationUtil.validateConfirmPassword(password, confirmPassword)
        }
    }

    fun validateBioOnBlur(bio: String) {
        if (_bioTouched.value) {
            bioValidation = ValidationUtil.validateBio(bio)
        }
    }

    // Update without immediate validation (store values only)
    fun updateBasicInfoWithoutValidation(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        _registrationData.update { currentData ->
            currentData.copy(
                fullName = name,
                email = email,
                phone = phone,
                password = password,
                confirmPassword = confirmPassword
            )
        }
    }

    // Validate all fields (for next step or submit)
    fun validateAllBasicInfo() {
        val currentData = _registrationData.value
        nameValidation = ValidationUtil.validateName(currentData.fullName)
        emailValidation = ValidationUtil.validateEmail(currentData.email)
        phoneValidation = ValidationUtil.validatePhone(currentData.phone)
        passwordValidation = ValidationUtil.validatePassword(currentData.password)
        confirmPasswordValidation = ValidationUtil.validateConfirmPassword(
            currentData.password, 
            currentData.confirmPassword
        )
        
        // Mark all as touched when validating all
        _nameTouched.value = true
        _emailTouched.value = true
        _phoneTouched.value = true
        _passwordTouched.value = true
        _confirmPasswordTouched.value = true
    }

    fun goToStep(step: Int) {
        if (step in 0 until totalSteps) {
            currentStep = step
        }
    }

    // For later: Function to submit data to Firebase
    fun registerUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val email = registrationData.value.email
        val password = registrationData.value.password

        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User registered successfully
                            onSuccess()
                        } else {
                            // Registration failed
                            onError(task.exception?.message ?: "Registration failed")
                        }
                    }
            } catch (e: Exception) {
                onError(e.message ?: "Registration failed")
            }
        }
    }


    // Create a property to store the latest photo URI
    private var _latestTempUri: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val latestTempUri: StateFlow<Uri?> = _latestTempUri.asStateFlow()

    // Function to create a temporary file for the camera photo
    fun createImageUri(context: Context): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "TRIPBOOK_${timeStamp}_"
    
    val storageDir = context.getExternalFilesDir("profile_pictures")
    val imageFile = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
    
    // Use FileProvider to get content:// URI instead of file:// URI
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
    
    // Save the URI to use when the camera returns a result
    _latestTempUri.value = uri
    return uri
    }
    
    // Function to handle the result of a camera photo
    fun handleCameraResult(success: Boolean) {
        if (success && _latestTempUri.value != null) {
            // If camera capture was successful, update the profile picture with the temp URI
            updateProfilePicture(_latestTempUri.value?.toString())
        }
    }
}
