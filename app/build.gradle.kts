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
        minSdk = 30
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
    implementation(libs.androidx.navigation.runtime.android)
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

    // Navigation - SDK 34 compatible version
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.android)    // Navigation - SDK 34 compatible version
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


    // Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Layout
    implementation(libs.constraintlayout.compose)
    // ViewModel and Lifecycle - SDK 34 compatible versions
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.compose.foundation)

    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.compose.material3:material3:1.1.2")
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")
}