plugins {
    id("com.android.application")
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.android.tripbook" // Updated based on directory structure
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.tripbook" // Updated based on directory structure
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        dataBinding = true // Enable Data Binding
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    }
}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0") {
            because("Aligning Kotlin version with KSP and Kotlin Gradle Plugin (1.9.0)")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.0") {
            because("Aligning Kotlin version with KSP and Kotlin Gradle Plugin (1.9.0)")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0") {
            because("Aligning Kotlin version with KSP and Kotlin Gradle Plugin (1.9.0)")
        }
    }

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose Dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation") // Often needed with UI elements
    implementation("androidx.compose.material:material") // For Material 2 components (like Card)
    implementation("androidx.compose.material3:material3") // For Material 3 components
    implementation("androidx.activity:activity-compose:1.9.0") // For ComponentActivity.setContent

    val room_version = "2.7.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Lifecycle dependencies
    val lifecycle_version = "2.9.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Google Maps and Places (KTX and Compose parts temporarily removed to simplify build)
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.libraries.places:places:3.5.0") // Requires Kotlin 1.8.0+
    // implementation("com.google.maps.android:maps-compose:4.4.1") // Temporarily removed
    // implementation("com.google.maps.android:maps-ktx:5.1.1") // Temporarily removed
    // implementation("com.google.maps.android:places-ktx:3.3.1") // Temporarily removed

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // Downgraded from 1.7.1

    // Kotlinx DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // TODO: Add your application's dependencies here
    // Example:
    // implementation("androidx.core:core-ktx:1.9.0")
    // implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    // implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // testImplementation("junit:junit:4.13.2")
    // androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val nav_version = "2.7.7" // Use the latest stable version
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
} 