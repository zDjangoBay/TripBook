package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            startActivity(Intent(this, ViewProfileActivity::class.java))
        }

        findViewById<TextView>(R.id.goToSignUp).setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }
}
