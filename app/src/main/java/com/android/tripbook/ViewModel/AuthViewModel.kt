//package com.android.tripbook.ViewModel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.android.tripbook.auth.FirebaseAuthHelper
//
//class AuthViewModel : ViewModel() {
//
//    private val _authResult = MutableLiveData<Pair<Boolean, String?>>()
//    val authResult: LiveData<Pair<Boolean, String?>> = _authResult
//
//    fun login(email: String, password: String) {
//        FirebaseAuthHelper.loginUser(email, password) { success, message ->
//            _authResult.postValue(Pair(success, message))
//        }
//    }
//
//    fun register(email: String, password: String) {
//        FirebaseAuthHelper.registerUser(email, password) { success, message ->
//            _authResult.postValue(Pair(success, message))
//        }
//    }
//
//    fun logout() {
//        FirebaseAuthHelper.logout()
//    }
//
//    fun isLoggedIn(): Boolean {
//        return FirebaseAuthHelper.getCurrentUser() != null
//    }
//}
package com.android.tripbook.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tripbook.auth.FirebaseAuthHelper
import com.android.tripbook.utils.UserProgress

class AuthViewModel : ViewModel() {

    private val _authResult = MutableLiveData<Pair<Boolean, String?>>()
    val authResult: LiveData<Pair<Boolean, String?>> = _authResult

    fun login(email: String, password: String) {
        FirebaseAuthHelper.loginUser(email, password) { success, message ->
            if (success) {
                UserProgress.tracker.isAuthenticated = true
            }
            _authResult.postValue(Pair(success, message))
        }
    }

    fun register(email: String, password: String) {
        FirebaseAuthHelper.registerUser(email, password) { success, message ->
            if (success) {
                UserProgress.tracker.isAuthenticated = true
            }
            _authResult.postValue(Pair(success, message))
        }
    }

    fun logout() {
        FirebaseAuthHelper.logout()
        UserProgress.tracker.isAuthenticated = false  // Reset on logout
    }

    fun isLoggedIn(): Boolean {
        return FirebaseAuthHelper.getCurrentUser() != null
    }
}
