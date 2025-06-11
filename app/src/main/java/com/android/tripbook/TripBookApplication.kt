package com.android.tripbook

import android.app.Application
import com.google.firebase.FirebaseApp

class TripBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
