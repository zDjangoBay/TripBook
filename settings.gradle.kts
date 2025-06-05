pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://repo.maven.apache.org/maven2/") }
    }
    plugins {
        id("com.android.application") version "8.X.X"
        id("org.jetbrains.kotlin.android") version "1.9.XX"
        id("com.google.devtools.ksp") version "1.9.XX-1.0.YY"
        id("androidx.navigation.safeargs.kotlin") version "2.7.7"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://repo.maven.apache.org/maven2/") }
    }
}

rootProject.name = "TripBook"
include(":app")
