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
        maven {
            url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/")
        }
    }
}

rootProject.name = "Zipbab"
include(":app")
include(":data")
