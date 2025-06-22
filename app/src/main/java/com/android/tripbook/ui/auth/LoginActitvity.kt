package com.android.tripbook

import com.example.tohpoh.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tohpoh.MainActivity
import com.example.tohpoh.database.DataBaseHelper
import com.example.tohpoh.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        dbHelper = DataBaseHelper(this)
        sessionManager = SessionManager(this)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener { loginUser() }
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
            sessionManager.saveUserSession(user.id)
            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java)) // Redirection vers la page connectée
            finish()
        } else {
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
        }
    }
}
