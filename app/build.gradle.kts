plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.android.tripbook"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.tripbook"
        minSdk = 28
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
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.android)    // Navigation - SDK 34 compatible version
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation(libs.firebase.database)

    // Compose dependencies
    implementation("androidx.compose.runtime:runtime-livedata:1.5.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Image loading
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    // Layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // ViewModel and Lifecycle - SDK 34 compatible versions
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.compose.foundation)

    implementation("io.coil-kt:coil-compose:2.4.0")

}


