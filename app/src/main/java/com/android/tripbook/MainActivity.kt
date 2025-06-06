package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immediately launch HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

        // Finish MainActivity so user can't navigate back to it
        finish()
    }
}
