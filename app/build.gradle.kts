plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.google.gms.google.services) apply false
}

android {
    namespace = "com.android.tripbook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.tripbook"
        minSdk = 31
        targetSdk = 35
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
        //dataBinding= true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //------------------------------------------------------
    //           Default dependencies for the project
    //                  Do not touch them !!!!
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
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    //implementation(libs.support.annotations)
    //implementation(libs.androidx.annotation)
    //implementation(libs.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("com.google.maps.android:maps-compose:4.3.0") // Or latest
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.0")

// Accompanist for sticky headers (optional if using custom pinned behavior)
    implementation("com.google.accompanist:accompanist-placeholder-material:0.32.0")

// Compose + lifecycle
//    implementation(libs.androidx.lifecycle.runtime.compose)

// Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")



    //---------------------------------------------------------
    //        Custom dependencies
    //---------------------------------------------------------

    implementation("com.github.IsmaelDivita:chip-navigation-bar:1.4.0")


    implementation("com.github.bumptech.glide:glide:4.16.0")
        //ksp("com.github.bumptech.glide:ksp:4.16.0") // Glide's KSP processor
        // ... other dependencies ...




}
