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
    }
    plugins {
        id("com.google.gms.google-services") version "4.4.1"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

<<<<<<< HEAD
rootProject.name = "TripBook"
include(":app")
<<<<<<< HEAD
include(":userprofile")
=======
=======
rootProject.name = "TripBookTest"
include(":app")
 
>>>>>>> 51a58f2 (My integration testing implementation)
>>>>>>> 0857550e (My integration testing implementation)
