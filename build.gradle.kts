// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
<<<<<<< HEAD
    alias(libs.plugins.jetbrains.kotlin.android) apply false
=======
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
>>>>>>> 51a58f2 (My integration testing implementation)
}