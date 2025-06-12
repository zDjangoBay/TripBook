plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    // Added Safe Args plugin classpath for navigation
    classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
}
