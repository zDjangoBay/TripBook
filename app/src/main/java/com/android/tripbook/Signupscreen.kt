// SignupScreenWithRoom.kt
package com.android.tripbook.signup

import android.app.Application
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- Data Layer ---
@Entity(tableName = "signup_users")
data class SignupUser(
    @PrimaryKey val email: String,
    val name: String,
    val password: String
)

@Dao
interface SignupUserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: SignupUser)

    @Query("SELECT * FROM signup_users WHERE email = :email")
    suspend fun getUserByEmail(email: String): SignupUser?
}

@Database(entities = [SignupUser::class], version = 1)
abstract class SignupAppDatabase : RoomDatabase() {
    abstract fun userDao(): SignupUserDao

    companion object {
        @Volatile
        private var INSTANCE: SignupAppDatabase? = null

        fun getInstance(context: android.content.Context): SignupAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SignupAppDatabase::class.java,
                    "signup_user_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- ViewModel ---
class SignupViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = SignupAppDatabase.getInstance(application).userDao()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var success by mutableStateOf(false)
        private set

    var nameError by mutableStateOf<String?>(null)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set

    fun registerUser(name: String, email: String, password: String) {
        clearFieldErrors()
        var hasError = false

        if (name.isBlank()) {
            nameError = "Name cannot be empty"
            hasError = true
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Enter a valid email"
            hasError = true
        }
        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            hasError = true
        }

        if (hasError) return

        isLoading = true
        errorMessage = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = userDao.getUserByEmail(email)
                if (existing != null) {
                    errorMessage = "Email already registered."
                    isLoading = false
                    return@launch
                }
                userDao.insertUser(SignupUser(email, name, password))
                success = true
            } catch (e: Exception) {
                errorMessage = "Database error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun clearFieldErrors() {
        nameError = null
        emailError = null
        passwordError = null
    }

    fun clearStatus() {
        errorMessage = null
        success = false
        clearFieldErrors()
    }
}

// --- ViewModel Factory ---
class SignupViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// --- UI Layer ---
@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: SignupViewModel = viewModel(factory = SignupViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    if (viewModel.success) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
            viewModel.clearStatus()
            onRegisterSuccess()
        }
    }

    viewModel.errorMessage?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearStatus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
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
                    "Create your account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Sign up to get started with TripBook",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                    isError = viewModel.nameError != null,
                    supportingText = {
                        viewModel.nameError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    isError = viewModel.emailError != null,
                    supportingText = {
                        viewModel.emailError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
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
                    isError = viewModel.passwordError != null,
                    supportingText = {
                        viewModel.passwordError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.registerUser(name.trim(), email.trim(), password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !viewModel.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Sign Up", fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row {
                    Text("Already have an account? ")
                    Text(
                        "Login",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    MaterialTheme {
        SignupScreen()
    }
}
