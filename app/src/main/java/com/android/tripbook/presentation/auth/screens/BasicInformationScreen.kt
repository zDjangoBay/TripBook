package com.android.tripbook.presentation.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.util.ValidationResult
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KFunction3

@Composable
fun BasicInfoPage(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    phone: String,
    password: String,
    confirmPassword: String,
    nameValidation: ValidationResult,
    emailValidation: ValidationResult,
    phoneValidation: ValidationResult,
    passwordValidation: ValidationResult,
    confirmPasswordValidation: ValidationResult,
    nameTouched: StateFlow<Boolean>,
    emailTouched: StateFlow<Boolean>,
    phoneTouched: StateFlow<Boolean>,
    passwordTouched: StateFlow<Boolean>,
    confirmPasswordTouched: StateFlow<Boolean>,
    onInfoUpdated: (name: String, email: String, phone: String, password: String, confirmPassword: String) -> Unit,
    onFieldTouched: (field: String) -> Unit,
    onFieldBlur: (field: String) -> Unit,
    onSignUp: () -> Unit,
    isSignUpEnabled: Boolean
) {
    var nameState by remember { mutableStateOf(name) }
    var emailState by remember { mutableStateOf(email) }
    var phoneState by remember { mutableStateOf(phone) }
    var passwordState by remember { mutableStateOf(password) }
    var confirmPasswordState by remember { mutableStateOf(confirmPassword) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Collect touched states
    val isNameTouched by nameTouched.collectAsState()
    val isEmailTouched by emailTouched.collectAsState()
    val isPhoneTouched by phoneTouched.collectAsState()
    val isPasswordTouched by passwordTouched.collectAsState()
    val isConfirmPasswordTouched by confirmPasswordTouched.collectAsState()

   

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(
            modifier = modifier // Apply the modifier passed from the parent
                .fillMaxSize()

            ){

        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the Box
                .verticalScroll(scrollState) // Make this Column scrollable
                .padding(horizontal = 24.dp, vertical = 16.dp), // Add overall padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Join TripBook and start your journey!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Name
            OutlinedTextField(
                value = nameState,
                onValueChange = {
                    nameState = it
                    onInfoUpdated(it, emailState, phoneState, passwordState, confirmPasswordState)
                    if (!isNameTouched) onFieldTouched("name")
                },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("Full Name") },
                singleLine = true,
                isError = !nameValidation.isValid  && isNameTouched,
                supportingText = {
                if (!nameValidation.isValid && isNameTouched) {
                    Text(nameValidation.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .onFocusChanged { 
                    if (!it.isFocused && isNameTouched) {
                        onFieldBlur("name")
                    }
                },
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
      
            )

            // Email
            OutlinedTextField(
                value = emailState,
                onValueChange = { 
                emailState = it
                onInfoUpdated(nameState, it, phoneState, passwordState, confirmPasswordState)
                if (!isEmailTouched) onFieldTouched("email")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .onFocusChanged { 
                    if (!it.isFocused && isEmailTouched) {
                        onFieldBlur("email")
                    }
                },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            label = { Text("Email") },
            singleLine = true,
            isError = !emailValidation.isValid && isEmailTouched,
            supportingText = {
                if (!emailValidation.isValid && isEmailTouched) {
                    Text(emailValidation.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            },
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            )

            // Phone
            OutlinedTextField(
                value = phoneState,
               onValueChange = { 
                phoneState = it
                onInfoUpdated(nameState, emailState, it, passwordState, confirmPasswordState)
                if (!isPhoneTouched) onFieldTouched("phone")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .onFocusChanged { 
                    if (!it.isFocused && isPhoneTouched) {
                        onFieldBlur("phone")
                    }
                },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            label = { Text("Phone Number") },
            singleLine = true,
            isError = !phoneValidation.isValid && isPhoneTouched,
            supportingText = {
                if (!phoneValidation.isValid && isPhoneTouched) {
                    Text(phoneValidation.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            },
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
            )

            // Password
            OutlinedTextField(
                value = passwordState,
                onValueChange = { 
                passwordState = it
                onInfoUpdated(nameState, emailState, phoneState, it, confirmPasswordState)
                if (!isPasswordTouched) onFieldTouched("password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .onFocusChanged { 
                    if (!it.isFocused && isPasswordTouched) {
                        onFieldBlur("password")
                    }
                },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            isError = !passwordValidation.isValid && isPasswordTouched,
            supportingText = {
                if (!passwordValidation.isValid && isPasswordTouched) {
                    Text(passwordValidation.errorMessage, color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Use at least 8 characters with letters and numbers")
                }
            },
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
            )

            // Confirm Password
            OutlinedTextField(
                value = confirmPasswordState,
                onValueChange = { 
                confirmPasswordState = it
                onInfoUpdated(nameState, emailState, phoneState, passwordState, it)
                if (!isConfirmPasswordTouched) onFieldTouched("confirmPassword")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .onFocusChanged { 
                    if (!it.isFocused && isConfirmPasswordTouched) {
                        onFieldBlur("confirmPassword")
                    }
                },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            isError = !confirmPasswordValidation.isValid && isConfirmPasswordTouched,
            supportingText = {
                if (!confirmPasswordValidation.isValid && isConfirmPasswordTouched) {
                    Text(confirmPasswordValidation.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            },
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Or sign up with
            Text(
                text = "or sign up with",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Social icons row (placeholders, implement click handlers as needed)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { /* Google */ }) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Google",
                        tint = Color(0xFFEA4335)
                    )
                }

                Text(
                    text = "Google Authentication",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
        }
    }
}
