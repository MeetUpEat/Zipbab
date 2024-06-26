pluginManagement {
    repositories {
        maven {
            url = uri("https://dl.google.com/dl/android/maven2/")
            content {
                includeGroup("com.android")
                includeGroup("com.google")
                includeGroup("androidx")
            }
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repository.map.naver.com/archive/maven")
    }
}

rootProject.name = "Zipbab"
include(":app")
include(":data")