package com.example.yourappname.ui.profile

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    // LiveData pour les erreurs de validation
    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    // LiveData pour les états de l'UI (succès, erreur générale, chargement)
    private val _profileUpdateStatus = MutableLiveData<ProfileUpdateStatus>()
    val profileUpdateStatus: LiveData<ProfileUpdateStatus> = _profileUpdateStatus

    // Représente l'état actuel des données du profil (pour pré-remplir le formulaire)
    private val _currentProfile = MutableLiveData<Profile>()
    val currentProfile: LiveData<Profile> = _currentProfile

    init {
        // Simuler le chargement des données de profil existantes
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            _profileUpdateStatus.value = ProfileUpdateStatus.Loading
            delay(500) // Simuler un délai réseau
            _currentProfile.value = Profile("utilisateur_exemple", "exemple@domaine.com")
            _profileUpdateStatus.value = ProfileUpdateStatus.Idle // Revenir à l'état inactif après le chargement
        }
    }

    fun validateAndSaveProfile(username: String, email: String) {
        _usernameError.value = null // Réinitialiser les erreurs
        _emailError.value = null

        var isValid = true

        // Validation du nom d'utilisateur
        if (username.isBlank()) {
            _usernameError.value = "Le nom d'utilisateur ne peut pas être vide."
            isValid = false
        } else if (username.length < 3) {
            _usernameError.value = "Le nom d'utilisateur doit contenir au moins 3 caractères."
            isValid = false
        }

        // Validation de l'e-mail
        if (email.isBlank()) {
            _emailError.value = "L'e-mail ne peut pas être vide."
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Veuillez entrer une adresse e-mail valide."
            isValid = false
        }

        if (isValid) {
            _profileUpdateStatus.value = ProfileUpdateStatus.Loading
            viewModelScope.launch {
                try {
                    // Simuler un appel API pour enregistrer le profil
                    delay(1000) // Simuler un délai réseau
                    // Logic to actually save the profile (e.g., to a database or remote server)
                    // If successful:
                    _profileUpdateStatus.value = ProfileUpdateStatus.Success("Profil mis à jour avec succès!")
                    // Mettre à jour le profil local après une sauvegarde réussie
                    _currentProfile.value = Profile(username, email)
                } catch (e: Exception) {
                    _profileUpdateStatus.value = ProfileUpdateStatus.Error("Échec de la mise à jour du profil: ${e.localizedMessage}")
                }
            }
        } else {
            _profileUpdateStatus.value = ProfileUpdateStatus.Idle // Si la validation échoue, revenir à l'état inactif
        }
    }
}

// Classe scellée pour gérer les différents états de la mise à jour du profil
sealed class ProfileUpdateStatus {
    object Idle : ProfileUpdateStatus()
    object Loading : ProfileUpdateStatus()
    data class Success(val message: String) : ProfileUpdateStatus()
    data class Error(val message: String) : ProfileUpdateStatus()
}

// Modèle de données pour le profil
data class Profile(val username: String, val email: String)