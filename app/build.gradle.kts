// plugins {
//     alias(libs.plugins.android.application)
//     alias(libs.plugins.jetbrains.kotlin.android)
// }

// android {
//     namespace = "com.android.tripbook"
//     compileSdk = 34

//     defaultConfig {
//         applicationId = "com.android.tripbook"
//         minSdk = 31
//         targetSdk = 34
//         versionCode = 1
//         versionName = "1.0"

//         testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//         vectorDrawables {
//             useSupportLibrary = true
//         }
//     }

//     buildTypes {
//         release {
//             isMinifyEnabled = false
//             proguardFiles(
//                 getDefaultProguardFile("proguard-android-optimize.txt"),
//                 "proguard-rules.pro"
//             )
//         }
//     }
//     compileOptions {
//         sourceCompatibility = JavaVersion.VERSION_1_8
//         targetCompatibility = JavaVersion.VERSION_1_8
//     }
//     kotlinOptions {
//         jvmTarget = "1.8"
//     }
//     buildFeatures {
//         compose = true
//     }
//     composeOptions {
//         kotlinCompilerExtensionVersion = "1.5.1"
//     }
//     packaging {
//         resources {
//             excludes += "/META-INF/{AL2.0,LGPL2.1}"
//         }
//     }
// }

// dependencies {
//     //------------------------------------------------------
//    //           default dependencies on the project
//     //                       do not touch them !!!!!!
//     //-----------------------------------------------------
//     implementation(libs.androidx.core.ktx)
//     implementation(libs.androidx.appcompat)
//     implementation(libs.androidx.ui.tooling.preview.android)
//     implementation(libs.material)
//     implementation(libs.androidx.lifecycle.runtime.ktx)
//     implementation(libs.androidx.activity.compose)
//     implementation(platform(libs.androidx.compose.bom))
//     implementation(libs.androidx.ui)
//     implementation(libs.androidx.ui.graphics)
//     implementation(libs.androidx.ui.tooling.preview)
//     implementation(libs.androidx.material3)
//     testImplementation(libs.junit)
//     androidTestImplementation(libs.androidx.junit)
//     androidTestImplementation(libs.androidx.espresso.core)
//     androidTestImplementation(platform(libs.androidx.compose.bom))
//     androidTestImplementation(libs.androidx.ui.test.junit4)
//     debugImplementation(libs.androidx.ui.tooling)
//     debugImplementation(libs.androidx.ui.test.manifest)

//     //---------------------------------------------------------
//     //      You can add your own dependencies down here
//     //---------------------------------------------------------
//     // Coil for Jetpack Compose
// implementation("io.coil-kt:coil-compose:2.2.2") // or latest version
// implementation("io.coil-kt:coil:2.2.2")

// }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
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
    //           Default dependencies on the project
    //                       Do not touch them !!!!!!
    //------------------------------------------------------
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //------------------------------------------------------
    //           Custom dependencies (Feel free to edit)
    //------------------------------------------------------

    // Coil for Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil:2.2.2")

    // Chip Navigation Bar
    implementation("com.github.IsmaelDivita:chip-navigation-bar:1.4.0")

    // Glide for image loading (if used alongside Coil, make sure to avoid conflicts)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // ksp("com.github.bumptech.glide:ksp:4.16.0") // Uncomment if using KSP

}
