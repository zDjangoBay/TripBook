package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tohpoh.MainActivity
import com.example.tohpoh.R
import com.example.tohpoh.database.DataBaseHelper
import com.example.tohpoh.utils.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonRegister: Button
    private lateinit var textLogin: TextView
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // Si l'utilisateur a déjà une session, on le redirige directement
        if (sessionManager.getUserSession() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.register)

        dbHelper = DataBaseHelper(this)

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonRegister = findViewById(R.id.buttonRegister)
        textLogin = findViewById(R.id.textLogin)

        buttonRegister.setOnClickListener { registerUser() }
        textLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val username = editTextUsername.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            return
        }

        val success = dbHelper.registerUser(username, email, password)
        if (success) {
            val userId = dbHelper.getUserId(email)
            if (userId != -1) {
                sessionManager.saveUserSession(userId)
                Toast.makeText(this, "Inscription réussie ! Bienvenue, $username", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Erreur lors de la récupération de l'utilisateur", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Échec de l'inscription. Email ou nom d'utilisateur déjà utilisé.", Toast.LENGTH_SHORT).show()
        }
    }
}
