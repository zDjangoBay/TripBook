package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        findViewById<Button>(R.id.signupButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<TextView>(R.id.goToLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
