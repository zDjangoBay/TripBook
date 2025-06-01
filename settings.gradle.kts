// pluginManagement {
//     repositories {
//         google {
//             content {
//                 includeGroupByRegex("com\\.android.*")
//                 includeGroupByRegex("com\\.google.*")
//                 includeGroupByRegex("androidx.*")
//             }
//         }
//         mavenCentral()
//         gradlePluginPortal()
//     }
// }
// dependencyResolutionManagement {
//     repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//     repositories {
//         google()
//         mavenCentral()
//     }
// }

// rootProject.name = "TripBook"
// include(":app")
pluginManagement {
    repositories {
        google() // ✅ Removed content filters to allow full access
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TripBook"
include(":app")
