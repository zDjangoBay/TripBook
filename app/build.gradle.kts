plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.android.tripbook"
    compileSdk = 34 // Keep this as 34 for now

    defaultConfig {
        applicationId = "com.android.tripbook"
        minSdk = 31 // Keep this as 31
        targetSdk = 34 // Keep this as 34

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Match your Kotlin version or use a recent stable one
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //------------------------------------------------------
    //           Default Android/Compose Dependencies
    //-----------------------------------------------------
    implementation(libs.androidx.core.ktx)
    // If you are fully Compose, you often don't need appcompat and material (MD2)
    // implementation(libs.androidx.appcompat)
    // implementation(libs.material) // REMOVE this one if you're using Material3

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // Keep this, it manages versions
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Ensure this is using your updated version in libs.versions.toml
    implementation(libs.androidx.material.icons.extended) // For Icons.Default.Notifications
    // Good to have for Compose layouts
    implementation(libs.play.services.location) // Keep this for location services

    // Testing dependencies (keep as is)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    //---------------------------------------------------------
    //      Your Custom/Additional Dependencies
    //---------------------------------------------------------

    implementation(libs.coil.compose) // Keep (image loading)
    implementation(libs.androidx.navigation.compose) // Keep (navigation)
//    implementation(libs.material)

    // Google Maps dependencies - Consolidate these:
    implementation(libs.play.services.maps) // Core Google Maps SDK
    implementation(libs.maps.compose) // Compose integration for Maps (assuming this is the generic reference)
    // Only keep maps.compose.v430 if it's a *different* library or specific requirement.
    // If 'maps.compose' is already the right one, remove maps.compose.v430.
    // If maps.compose.v430 is the one you *intend* to use, ensure 'maps.compose' is commented out.
    // For clarity, let's assume `maps.compose` is the correct alias for the main compose maps lib:
    // implementation(libs.maps.compose.v430) // Consider removing if redundant with maps.compose

    implementation(libs.maps.utils.ktx) // Good for map utilities
    implementation(libs.kotlinx.coroutines.play.services) // For coroutines with Play Services

    // Accompanist (keep if needed, but remove duplicate)
    implementation(libs.accompanist.placeholder)

    // Kotlin coroutines (remove duplicate)
    implementation(libs.kotlinx.coroutines.android)


    // You don't need these lines again if they are already above and part of `libs.versions.toml`:
    // implementation(libs.material.icons.extended) // Duplicated
    // implementation(libs.maps.compose.v430) // Duplicated/potentially redundant
    // implementation(libs.accompanist.placeholder) // Duplicated
    // implementation(libs.kotlinx.coroutines.android) // Duplicated
    // implementation(libs.play.services.maps) // Duplicated

    implementation(libs.androidx.compose.foundation)



}


    // If you were specifically trying to add Material3 again for some reason,
    // it should be handled by `implementation(libs.androidx.material3)` above.
    // implementation(libs.material3) // Duplicated
