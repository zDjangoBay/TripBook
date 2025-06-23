plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.android.tripbook"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.tripbook"
        minSdk = 31

        targetSdk = 34
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
        viewBinding = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Force SDK 34 compatible versions
    configurations.all {
        resolutionStrategy {
            force("androidx.navigation:navigation-compose:2.8.4")
            force("androidx.activity:activity-ktx:1.9.3")
            force("androidx.activity:activity-compose:1.9.3")
            force("androidx.activity:activity:1.9.3")
            force("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
            force("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
        }
    }
}

dependencies {
    //------------------------------------------------------
   //           default dependencies on the project
    //                       do not touch them !!!!!!
    //-----------------------------------------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // Removed problematic navigation-runtime-android
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.maps.compose) // Or latest
    implementation(libs.play.services.maps)

    // Google Maps Compose
    implementation(libs.maps.compose.v430)

// Accompanist for sticky headers (optional if using custom pinned behavior)
    implementation(libs.accompanist.placeholder)

// Compose + lifecycle
//    implementation(libs.androidx.lifecycle.runtime.compose)

// Kotlin coroutines
    implementation(libs.kotlinx.coroutines.android)





    //---------------------------------------------------------
    //      You can add your own dependencies down here
    //---------------------------------------------------------

    // Navigation - SDK 34 compatible version
    implementation(libs.androidx.navigation.compose)
    // Removed problematic navigation-runtime-android dependencies
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)

    implementation(libs.compose.material3)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)


    // Compose dependencies
    implementation(libs.compose.runtime.livedata)
//    implementation(libs.androidx.material3)
    implementation(libs.compose.material.icons.extended)


    // Image loading
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.maps.compose) // Or latest
    implementation(libs.play.services.maps)

    // Layout
    implementation(libs.constraintlayout.compose)
    // ViewModel and Lifecycle - SDK 34 compatible versions
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

// Accompanist for sticky headers (optional if using custom pinned behavior)
    implementation(libs.accompanist.placeholder)

// Compose + lifecycle
//    implementation(libs.androidx.lifecycle.runtime.compose)

    // Chip Navigation Bar
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

//   ------------------------------------------------------------
//              Added some Essential Dependencies
//   ------------------------------------------------------------

    //  DEPENDENCIES - ADDED FOR BASIC FUNCTIONALITY
    implementation(libs.androidx.constraintlayout)  // For ConstraintLayout
    implementation(libs.androidx.recyclerview)  // For RecyclerView

    // ESSENTIAL ANDROID DEPENDENCIES
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // COMPOSE NAVIGATION
    implementation(libs.androidx.navigation.runtime.ktx)

    // COMPOSE FOUNDATION
    implementation("androidx.compose.foundation:foundation:1.5.4")
    implementation("androidx.compose.foundation:foundation-layout:1.5.4")
    implementation("androidx.compose.ui:ui-util:1.5.4")

    // IMAGE LOADING (Missing)
    implementation(libs.glide)

//   ------------------------------------------------------------

    // Room Database - NEW ADDITION FOR LOCAL DATABASE
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Gson for Room type converters - NEW ADDITION
    implementation("com.google.code.gson:gson:2.10.1")

    implementation(libs.androidx.compose.foundation)

    implementation("io.coil-kt:coil-compose:2.4.0")

}