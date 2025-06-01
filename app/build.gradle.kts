plugins {
<<<<<<< HEAD
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
=======
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.google.gms.google.services)
//    id("com.google.devtools.ksp") version "1.9.0-1.0.13" // Apply KSP plugin here
>>>>>>> b48963f4f0bc4430205349104323622e54a00c80
}

android {
    namespace = "com.android.tripbook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.tripbook"
<<<<<<< HEAD
        minSdk = 30
        targetSdk = 34
=======
        minSdk = 31
        targetSdk = 35
>>>>>>> b48963f4f0bc4430205349104323622e54a00c80
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Room schema location for KSP
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
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
//        dataBinding= true
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

// KSP configuration for Room
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    //------------------------------------------------------
    //           Default dependencies for the project
    //                  Do not touch them !!!!
    //-----------------------------------------------------
    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.databinding.ktx)
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
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Testing dependencies for Room and coroutines
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.robolectric:robolectric:4.11.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    //---------------------------------------------------------
    //        Custom dependencies
    //---------------------------------------------------------

<<<<<<< HEAD
    // Extended Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    // Calendar
    implementation("com.kizitonwose.calendar:compose:2.4.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
=======
    implementation("com.github.IsmaelDivita:chip-navigation-bar:1.4.0")


    implementation("com.github.bumptech.glide:glide:4.16.0")
       // ksp("com.github.bumptech.glide:ksp:4.16.0") // Glide's KSP processor
        // ... other dependencies ...




}
>>>>>>> b48963f4f0bc4430205349104323622e54a00c80
