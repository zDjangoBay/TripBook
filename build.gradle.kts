plugins {
    kotlin("jvm") version "1.9.23" // Use 'jvm' plugin
    kotlin("plugin.serialization") version "1.9.23"
}

group = "com.tripscheduler" // Or whatever makes sense for your backend
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlinx Serialization for JSON conversion
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // MongoDB Driver
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")

    // Redis Client
    implementation("redis.clients:jedis:5.1.3")

    // Kotlin-Faker for data generation
    implementation("io.github.serpro69:kotlin-faker:1.15.0")

    // For Instant/LocalDateTime if needed (though java.time is part of JDK)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
}

kotlin {
    jvmToolchain(21) // Or your preferred JVM version compatible with your server environment
}

application { // Add this if you want to run it as a standalone executable JAR
    mainClass.set("com.tripscheduler.app.TripSchedulingServiceKt") // Point to your main function
}