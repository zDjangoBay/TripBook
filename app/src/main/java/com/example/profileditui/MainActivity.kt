package com.example.profileditui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.profileditui.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.view.View
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import androidx.core.animation.doOnEnd

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Garder l'état du profil actuel directement dans l'activité
    private var currentUsername: String = "utilisateur_initial"
    private var currentEmail: String = "initial@domaine.com"

    // Scope pour les coroutines dans l'activité.
    // Attention: les coroutines lancées ici ne survivront pas aux changements de configuration.
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pré-remplir les champs avec les données initiales
        binding.etUsername.setText(currentUsername)
        binding.etEmail.setText(currentEmail)

        // Gérer le clic sur le bouton de sauvegarde
        binding.btnSaveProfile.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            validateAndSaveProfile(username, email)
        }

        // Gérer le clic sur le bouton d'annulation
        binding.btnCancel.setOnClickListener {
            resetForm()
        }

        // Animation d'entrée pour les cartes
        animateCardsEntrance()
    }

    // Fonction de validation et de sauvegarde (logique intégrée)
    private fun validateAndSaveProfile(username: String, email: String) {
        // Réinitialiser les erreurs précédentes
        binding.tilUsername.error = null
        binding.tilUsername.isErrorEnabled = false
        binding.tilEmail.error = null
        binding.tilEmail.isErrorEnabled = false

        var isValid = true

        // Validation du nom d'utilisateur
        if (username.isBlank()) {
            binding.tilUsername.error = "Le nom d'utilisateur ne peut pas être vide."
            binding.tilUsername.isErrorEnabled = true
            isValid = false
        } else if (username.length < 3) {
            binding.tilUsername.error = "Le nom d'utilisateur doit contenir au moins 3 caractères."
            binding.tilUsername.isErrorEnabled = true
            isValid = false
        }

        // Validation de l'e-mail
        if (email.isBlank()) {
            binding.tilEmail.error = "L'e-mail ne peut pas être vide."
            binding.tilEmail.isErrorEnabled = true
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Veuillez entrer une adresse e-mail valide."
            binding.tilEmail.isErrorEnabled = true
            isValid = false
        }

        if (isValid) {
            // Afficher le chargement avec animation
            showLoadingState()

            activityScope.launch {
                try {
                    // Simuler un appel API pour enregistrer le profil
                    delay(2000) // Simuler un délai réseau plus réaliste

                    // Logic to actually save the profile (e.g., to a database or remote server)
                    // Mettre à jour les données locales si la sauvegarde est réussie
                    currentUsername = username
                    currentEmail = email

                    withContext(Dispatchers.Main) {
                        hideLoadingState()
                        showSuccessMessage()
                        Toast.makeText(this@MainActivity, "Profil mis à jour avec succès!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        hideLoadingState()
                        Toast.makeText(this@MainActivity, "Échec de la mise à jour du profil: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            // Si la validation échoue, s'assurer que les indicateurs de chargement sont cachés
            hideLoadingState()
        }
    }

    // Réinitialiser le formulaire aux valeurs initiales
    private fun resetForm() {
        binding.etUsername.setText(currentUsername)
        binding.etEmail.setText(currentEmail)

        // Réinitialiser les erreurs
        binding.tilUsername.error = null
        binding.tilUsername.isErrorEnabled = false
        binding.tilEmail.error = null
        binding.tilEmail.isErrorEnabled = false

        // Cacher le message de succès s'il est visible
        hideSuccessMessage()

        Toast.makeText(this, "Formulaire réinitialisé", Toast.LENGTH_SHORT).show()
    }

    // Afficher l'état de chargement
    private fun showLoadingState() {
        binding.loadingCard.visibility = View.VISIBLE
        binding.btnSaveProfile.isEnabled = false
        binding.btnCancel.isEnabled = false

        // Animation d'apparition
        binding.loadingCard.alpha = 0f
        binding.loadingCard.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    // Cacher l'état de chargement
    private fun hideLoadingState() {
        binding.loadingCard.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.loadingCard.visibility = View.GONE
                binding.btnSaveProfile.isEnabled = true
                binding.btnCancel.isEnabled = true
            }
            .start()
    }

    // Afficher le message de succès
    private fun showSuccessMessage() {
        binding.successCard.visibility = View.VISIBLE
        binding.successCard.alpha = 0f
        binding.successCard.translationY = 50f

        binding.successCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .withEndAction {
                // Cacher automatiquement après 3 secondes
                binding.successCard.postDelayed({
                    hideSuccessMessage()
                }, 3000)
            }
            .start()
    }

    // Cacher le message de succès
    private fun hideSuccessMessage() {
        if (binding.successCard.visibility == View.VISIBLE) {
            binding.successCard.animate()
                .alpha(0f)
                .translationY(50f)
                .setDuration(300)
                .withEndAction {
                    binding.successCard.visibility = View.GONE
                }
                .start()
        }
    }

    // Animation d'entrée pour les cartes
    private fun animateCardsEntrance() {
        // Initialiser les cartes pour l'animation
        binding.headerCard.alpha = 0f
        binding.headerCard.translationY = -50f
        binding.formCard.alpha = 0f
        binding.formCard.translationY = 50f

        // Animer l'header
        binding.headerCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .start()

        // Animer le formulaire avec un délai
        binding.formCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(200)
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Nettoyer les animations en cours si nécessaire
        binding.headerCard.clearAnimation()
        binding.formCard.clearAnimation()
        binding.loadingCard.clearAnimation()
        binding.successCard.clearAnimation()
    }
}