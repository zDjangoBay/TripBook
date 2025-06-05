// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization) apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false // Corrected KSP version to match Kotlin 2.0.0
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false // This version is fine
}


