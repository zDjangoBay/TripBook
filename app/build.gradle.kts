//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.jetbrains.kotlin.android)
////    kotlin("kapt")
//}
//
//android {
//    namespace = "com.android.tripbook"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.android.tripbook"
//        minSdk = 31
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        vectorDrawables {
//            useSupportLibrary = true
//        }
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.1"
//    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
//}
//
//dependencies {
//    //------------------------------------------------------
//   //           default dependencies on the project
//    //                       do not touch them !!!!!!
//    //-----------------------------------------------------
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.androidx.ui.tooling.preview.android)
//    implementation(libs.material)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//    implementation(libs.androidx.room.compiler.processing.testing)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//
//
//    //---------------------------------------------------------
//    //      You can add your own dependencies down here
//    //---------------------------------------------------------
//
//  // viewmodel
//  implementation(libs.androidx.lifecycle.viewmodel.compose)
//
//  // navigation compose
//    implementation(libs.androidx.navigation.compose)
//    //Hilt (for dependency injection)
//    implementation(libs.hilt.android)
//    implementation(libs.androidx.hilt.navigation.compose)
//    // Hilt (for dependency injection)
//    implementation(libs.hilt.android)
//    implementation(libs.androidx.hilt.navigation.compose.v110)
//
//    //coil (image loading)
//    implementation(libs.coil.compose)
//
//    //material icons extended (for icons.e.g. search, etc.)
//    implementation(libs.androidx.material.icons.extended)
//
//    //testing dependencies
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit.v121)
//    androidTestImplementation(libs.androidx.espresso.core.v361)
//    androidTestImplementation(libs.androidx.x023.x0.x0)
//    androidTestImplementation(libs.ui.test.junit4)
//    debugImplementation(libs.ui.test.manifest)
//    androidTestImplementation(libs.ui.tooling)
//
//    implementation(libs.gson)
//
//
//
//
//}


// app/build.gradle.kts (App module)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Required for Hilt annotation processing
//    id("com.google.dagger.hilt.android") // Hilt plugin
}

android {
    namespace = "com.tripbook.travelagency" // Ensure this matches your package structure
    compileSdk = 35 // Updated to latest API level (Android 15, as of June 2025)

    defaultConfig {
        applicationId = "com.tripbook.travelagency"
        minSdk = 24 // Common minimum SDK
        targetSdk = 35 // Targetting Android 15
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
        // Use Java 1.8 for source and target compatibility, common for Android projects
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        // Enable Jetpack Compose features
        compose = true
    }
    composeOptions {
        // Specify the Kotlin Compiler Extension version compatible with Kotlin 1.9.0 and Compose BOM 2024.04.00
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            // Exclude specific files to avoid conflicts during build
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Android KTX and Lifecycle
    implementation("androidx.core:core-ktx:1.13.1") // Latest stable
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0") // Latest stable for runtime
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0") // For ViewModel in Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0") // For Compose-specific lifecycle
    implementation("androidx.activity:activity-compose:1.9.0") // Latest stable for Activity integration with Compose

    // Jetpack Compose BOM (Bill of Materials) for consistent Compose library versions
    // Using 2024.04.00 for compatibility with Kotlin 1.9.0 and AGP 8.9.0
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // Material 3 design system

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.51")
    kapt(libs.hilt.compiler)
    kapt("androidx.hilt:hilt-compiler:1.2.0") // For Hilt integration with Android components

    // Retrofit & Gson (Networking and JSON Parsing)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation(libs.converter.gson)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Core HTTP client
    implementation(libs.logging.interceptor) // For network request logging
    implementation (libs.material.v1xx) // Use the latest stable version

    // Coil (Image Loading Library)
    implementation(libs.coil.compose.v260) // Latest stable version for Compose

    // Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core.v361)
    androidTestImplementation(libs.compose.bom.v20240400)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)

    implementation (libs.androidx.constraintlayout.v2xx) // Use the latest stable version

}