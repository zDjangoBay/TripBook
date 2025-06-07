// Top-level build file where you can add configuration options common to all sub-projects/modules.

//
//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.jetbrains.kotlin.android) apply false
//}

// build.gradle.kts (Project level)
plugins {
    // Updates the Android Gradle Plugin version to 8.9.0
    id("com.android.application") version "8.9.3" apply false
    id("com.android.library") version "8.9.3" apply false // Include if you have library modules

    // Recommended Kotlin version for AGP 8.9.0 and Compose
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    // Hilt version (2.51 is recent and compatible)
    id("com.google.dagger.hilt.android") version "2.51" apply false
}