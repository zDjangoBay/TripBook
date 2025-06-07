package com.tripbook.reservation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation de base
        setupUI()
    }

    private fun setupUI() {
        // Configuration de l'interface utilisateur
        supportActionBar?.title = getString(R.string.app_name)
    }
}