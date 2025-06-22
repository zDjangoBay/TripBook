package com.android.tripbook.userprofilengolehans

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.tripbook.R

class ChatDialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_empty)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ChatDialogFragment())
            .commit()
    }
}
