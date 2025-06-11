package com.yourpackage.home

import android.app.Activity

class HomeNavigator(private val activity: Activity) {
    fun goBackHome() {
        activity.finish()
    }
}
