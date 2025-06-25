package com.android.tripbook

import android.app.Application
 feature/user-profile-dependency-injection-Ezekielkalu
import dagger.hilt.android.HiltAndroidApp
 
@HiltAndroidApp
class TripBookApplication : Application() 
=======
import com.google.firebase.FirebaseApp

class TripBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

 userprofile
