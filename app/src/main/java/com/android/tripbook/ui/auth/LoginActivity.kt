package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tohpoh.MainActivity
import com.example.tohpoh.R
import com.example.tohpoh.database.DataBaseHelper
import com.example.tohpoh.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textRegister: TextView
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser session
        sessionManager = SessionManager(this)

        // Rediriger vers MainActivity si l'utilisateur est déjà connecté
        val currentUserId = sessionManager.getUserSession()
        if (currentUserId != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.login)

        // Initialisation des composants UI
        dbHelper = DataBaseHelper(this)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textRegister = findViewById(R.id.text_register)

        // Connexion utilisateur
        buttonLogin.setOnClickListener { loginUser() }

        // Redirection vers page d'enregistrement
        textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val user = dbHelper.getUserByEmail(email, password)

        if (user != null) {
            sessionManager.saveUserSession(user.id) // Persiste l’ID utilisateur
            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
        }
    }
}
