// UserProfileScreenWithRoom.kt
package com.android.tripbook.profile

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.signup.SignupAppDatabase
import com.android.tripbook.signup.SignupUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface SignupUserDaoExtended {
    suspend fun updateUser(user: SignupUser)
}

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = SignupAppDatabase.getInstance(application).userDao()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var user by mutableStateOf<SignupUser?>(null)
        private set

    fun loadUser(email: String) {
        isLoading = true
        errorMessage = null
        successMessage = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val foundUser = userDao.getUserByEmail(email)
                if (foundUser != null) {
                    user = foundUser
                } else {
                    errorMessage = "User not found"
                }
            } catch (e: Exception) {
                errorMessage = "Error loading user: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUser(name: String, password: String) {
        val currentUser = user ?: return
        if (name.isBlank()) {
            errorMessage = "Name cannot be empty"
            return
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return
        }

        isLoading = true
        errorMessage = null
        successMessage = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedUser = SignupUser(currentUser.email, name, password)
                userDao.updateUser(updatedUser)
                user = updatedUser
                successMessage = "Profile updated successfully"
            } catch (e: Exception) {
                errorMessage = "Error updating user: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}

class UserProfileViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            return UserProfileViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun UserProfileScreen(
    email: String,
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = viewModel(factory = UserProfileViewModelFactory(LocalContext.current.applicationContext as Application)),
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val user = viewModel.user

    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(email) {
        viewModel.loadUser(email)
    }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            password = it.password
        }
    }

    if (viewModel.errorMessage != null) {
        LaunchedEffect(viewModel.errorMessage) {
            Toast.makeText(context, viewModel.errorMessage ?: "", Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    if (viewModel.successMessage != null) {
        LaunchedEffect(viewModel.successMessage) {
            Toast.makeText(context, viewModel.successMessage ?: "", Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Your Profile",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))

                // Profile Image
                Image(
                    painter = rememberAsyncImagePainter("https://i.pravatar.cc/300"),
                    contentDescription = "User Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    "Email: ${user?.email ?: "..."}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.updateUser(name.trim(), password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Save Changes", fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Logout",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onLogoutClick() }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    MaterialTheme {
        UserProfileScreen(email = "user@example.com")
    }
}
