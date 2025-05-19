package com.android.tripbook.presentation.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.android.tripbook.util.ValidationResult

@Composable
fun BasicInfoPage(
    name: String,
    email: String,
    password: String,
    nameValidation: ValidationResult,
    emailValidation: ValidationResult,
    passwordValidation: ValidationResult,
    onInfoUpdated: (name: String, email: String, password: String) -> Unit

) {
    var nameState by remember { mutableStateOf(name) }
    var emailState by remember { mutableStateOf(email) }
    var passwordState by remember { mutableStateOf(password) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.headlineMedium
        )

        // Name field with validation
        OutlinedTextField(
            value = nameState,
            onValueChange = {
                nameState = it
                onInfoUpdated(it, emailState, passwordState)
            },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = !nameValidation.isValid,
            supportingText = {
                if (!nameValidation.isValid) {
                    Text(
                        text = nameValidation.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        // Email field with validation
        OutlinedTextField(
            value = emailState,
            onValueChange = {
                emailState = it
                onInfoUpdated(nameState, it, passwordState)
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            isError = !emailValidation.isValid,
            supportingText = {
                if (!emailValidation.isValid) {
                    Text(
                        text = emailValidation.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        // Password field with validation
        OutlinedTextField(
            value = passwordState,
            onValueChange = {
                passwordState = it
                onInfoUpdated(nameState, emailState, it)
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isError = !passwordValidation.isValid,
            supportingText = {
                if (!passwordValidation.isValid) {
                    Text(
                        text = passwordValidation.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Use at least 8 characters with letters and numbers")
                }
            }
        ) }
}
