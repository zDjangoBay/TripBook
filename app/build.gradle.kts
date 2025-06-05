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
        //dataBinding = true
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //implementation(libs.androidx.navigation.compose.jvmstubs)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //---------------------------------------------------------
    //      You can add your own dependencies down here
    //---------------------------------------------------------

    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("com.github.IsmaelDivita:chip-navigation-bar:1.4.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4")) // Check for the latest BoM version
    implementation("com.google.firebase:firebase-database-ktx")

    implementation("com.github.IsmaelDivita:chip-navigation-bar:1.4.0")
    //implementation(libs.androidx.navigation.runtime.android)
 //   implementation(libs.androidx.databinding.ktx)
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    // ksp("com.github.bumptech.glide:ksp:4.16.0") // Glide's KSP processor
    // ... other dependencies ...

    implementation(libs.firebase.database)



    // Navigation - SDK 34 compatible version
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
}

