<<<<<<< HEAD
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
=======
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
>>>>>>> aefca7459800b1c04b5a49a71628bc9f0f4cee67

//         testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//         vectorDrawables {
//             useSupportLibrary = true
//         }
//     }

<<<<<<< HEAD
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
//      // Navigation
//     implementation("androidx.navigation:navigation-compose:2.7.5")
    
//     // ViewModel
//     implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
//     // Image loading
//     implementation("io.coil-kt:coil-compose:2.5.0")
    
//     // Permissions
//     implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
//     // Testing
//     testImplementation("junit:junit:4.13.2")
//     androidTestImplementation("androidx.test.ext:junit:1.1.5")
//     androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//     androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
//     androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//     debugImplementation("androidx.compose.ui:ui-tooling")
//     debugImplementation("androidx.compose.ui:ui-test-manifest")
// }
// }
=======
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //---------------------------------------------------------
    //      You can add your own dependencies down here
    //---------------------------------------------------------
<<<<<<< HEAD
}
>>>>>>> aefca7459800b1c04b5a49a71628bc9f0f4cee67
=======
}
>>>>>>> aefca7459800b1c04b5a49a71628bc9f0f4cee67
