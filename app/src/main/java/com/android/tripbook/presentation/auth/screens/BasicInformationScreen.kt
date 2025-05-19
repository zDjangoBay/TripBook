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

@Composable
fun BasicInfoPage(
    name: String,
    email: String,
    password: String,
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

        OutlinedTextField(
            value = nameState,
            onValueChange = {
                nameState = it
                onInfoUpdated(it, emailState, passwordState)
            },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

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
            )
        )

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
            )
        )
    }
}
