package com.android.tripbook.data.managers

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.android.tripbook.data.database.TripBookDatabase
import com.android.tripbook.data.database.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages user authentication and session state
 */
class UserSessionManager private constructor(
    private val context: Context,
    private val database: TripBookDatabase
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    companion object {
        private const val PREFS_NAME = "tripbook_user_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        
        @Volatile
        private var INSTANCE: UserSessionManager? = null
        
        fun getInstance(context: Context, database: TripBookDatabase): UserSessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserSessionManager(context.applicationContext, database)
                INSTANCE = instance
                instance
            }
        }
    }
    
    init {
        // Check if user was previously logged in
        loadSavedSession()
    }
    
    private fun loadSavedSession() {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val userId = prefs.getString(KEY_USER_ID, null)
        
        if (isLoggedIn && userId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = database.userDao().getUserById(userId)
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            _currentUser.value = user
                            _isLoggedIn.value = true
                        } else {
                            // User not found in database, clear session
                            clearSession()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        clearSession()
                    }
                }
            }
        }
    }
    
    /**
     * Login with email and password
     */
    suspend fun login(email: String, password: String): LoginResult {
        _isLoading.value = true
        
        return try {
            val user = database.userDao().getUserByEmail(email)
            
            if (user == null) {
                _isLoading.value = false
                LoginResult.Error("User not found")
            } else if (user.passwordHash != hashPassword(password)) {
                _isLoading.value = false
                LoginResult.Error("Invalid password")
            } else if (!user.isActive) {
                _isLoading.value = false
                LoginResult.Error("Account is deactivated")
            } else {
                // Successful login
                _currentUser.value = user
                _isLoggedIn.value = true
                saveSession(user.id)
                _isLoading.value = false
                LoginResult.Success(user)
            }
        } catch (e: Exception) {
            _isLoading.value = false
            LoginResult.Error("Login failed: ${e.message}")
        }
    }
    
    /**
     * Register new user
     */
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String
    ): RegisterResult {
        _isLoading.value = true
        
        return try {
            // Check if email already exists
            val existingUser = database.userDao().getUserByEmail(email)
            if (existingUser != null) {
                _isLoading.value = false
                return RegisterResult.Error("Email already registered")
            }
            
            // Check if username already exists
            val existingUsername = database.userDao().getUserByUsername(username)
            if (existingUsername != null) {
                _isLoading.value = false
                return RegisterResult.Error("Username already taken")
            }
            
            // Create new user
            val newUser = UserEntity.createNewUser(
                id = generateUserId(),
                username = username,
                email = email,
                firstName = firstName,
                lastName = lastName,
                passwordHash = hashPassword(password)
            )
            
            database.userDao().insertUser(newUser)
            
            // Auto-login after registration
            _currentUser.value = newUser
            _isLoggedIn.value = true
            saveSession(newUser.id)
            _isLoading.value = false
            
            RegisterResult.Success(newUser)
        } catch (e: Exception) {
            _isLoading.value = false
            RegisterResult.Error("Registration failed: ${e.message}")
        }
    }
    
    /**
     * Logout current user
     */
    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        clearSession()
    }
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? {
        return _currentUser.value?.id
    }
    
    private fun saveSession(userId: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    private fun clearSession() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
    
    private fun hashPassword(password: String): String {
        // Simple hash for demo - in production use proper hashing like BCrypt
        return password.hashCode().toString()
    }
    
    private fun generateUserId(): String {
        return "user_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

/**
 * Login result sealed class
 */
sealed class LoginResult {
    data class Success(val user: UserEntity) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

/**
 * Register result sealed class
 */
sealed class RegisterResult {
    data class Success(val user: UserEntity) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}
