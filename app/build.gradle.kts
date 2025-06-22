plugins {
    alias(libs.plugins.android.application)
<<<<<<< HEAD
    alias(libs.plugins.kotlin.android)
=======
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
>>>>>>> 4712c46cf27c3ef86b98da24e08e999274614675
}

android {
    namespace = "com.example.profileditui"
    compileSdk = 35

    defaultConfig {
<<<<<<< HEAD
        applicationId = "com.example.profileditui"
        minSdk = 24
        targetSdk = 35
=======
        applicationId = "com.android.tripbook"
        minSdk = 28
        targetSdk = 34
>>>>>>> 4712c46cf27c3ef86b98da24e08e999274614675
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // --- CHANGE THIS BLOCK ---
    buildFeatures {
        viewBinding = true // <--- CHANGE THIS LINE: Use '=' for assignment in Kotlin DSL
    }
    // --- END CHANGE ---

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.kotlinx.coroutines.core.v173)
    implementation(libs.kotlinx.coroutines.android.v173)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
<<<<<<< HEAD
}
=======
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //---------------------------------------------------------
    //      You can add your own dependencies down here
    //---------------------------------------------------------


    // Compose
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // SavedState -
    implementation(libs.androidx.savedstate)
    implementation(libs.androidx.savedstate.ktx)

    implementation(libs.androidx.datastore.preferences)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Date picker
    implementation(libs.androidx.compose.material3.material3)

    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation(project(":userprofile"))

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-auth-ktx")
}
>>>>>>> 4712c46cf27c3ef86b98da24e08e999274614675
