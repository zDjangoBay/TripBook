plugins {
    alias(libs.plugins.android.application)
<<<<<<< HEAD
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.android.tripbook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.tripbook"
        minSdk = 28
        targetSdk = 34
=======
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.tripbooktest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tripbooktest"
        minSdk = 24
        targetSdk = 35
>>>>>>> 51a58f2 (My integration testing implementation)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
<<<<<<< HEAD
        vectorDrawables {
            useSupportLibrary = true
        }
=======
>>>>>>> 51a58f2 (My integration testing implementation)
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
<<<<<<< HEAD
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
=======
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
>>>>>>> 51a58f2 (My integration testing implementation)
    }
    buildFeatures {
        compose = true
    }
<<<<<<< HEAD
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
=======
}

dependencies {

    implementation(libs.androidx.core.ktx)
>>>>>>> 51a58f2 (My integration testing implementation)
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
<<<<<<< HEAD

    //---------------------------------------------------------
    //      You can add your own dependencies down here
    //---------------------------------------------------------
<<<<<<< HEAD


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
=======
=======
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.1")

    // Coroutine support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Retrofit (for API service)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Kotlin coroutines test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // or your project's coroutines version

// MockK for mocking
    testImplementation("io.mockk:mockk:1.13.5") // Use a version compatible with your Kotlin version


>>>>>>> 51a58f2 (My integration testing implementation)
}
>>>>>>> 0857550e (My integration testing implementation)
