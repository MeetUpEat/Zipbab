import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleService)
    alias(libs.plugins.navigationSafeArgs)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id ("kotlin-parcelize")
    kotlin("kapt")
}

android {
    namespace = "com.bestapp.zipbab"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bestapp.zipbab"
        minSdk = 26
        targetSdk = 34
        versionCode = 8
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "FIREBASE_KEY", getValue("firebase_key"))
        buildConfigField("String", "KAKAO_NATIVE_KEY", getValue("kakao_map_native_key"))
        buildConfigField("String", "KAKAO_REST_API_KEY", getValue("kakao_map_rest_api_key"))
        buildConfigField("String", "KAKAO_MAP_BASE_URL", getValue("kakao_map_base_url"))
        buildConfigField("String", "KAKAO_NOTIFY_BASE_URL", getValue("kakao_notify_base_url"))
        buildConfigField("String", "KAKAO_ADMIN_KEY", getValue("kakao_admin_key"))
        buildConfigField("String", "GOOGLE_TOKEN_BASE_URL", getValue("google_token_base_url"))
        buildConfigField("String", "GOOGLE_REFRESH_BASE_URL", getValue("google_refresh_base_url"))
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

fun getValue(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.play.services.location)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.hilt.work)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // BottomSheet
    implementation(libs.androidx.material3.android)

    // hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.fragment)

    // navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    // datastore
    implementation(libs.androidx.datastore.preferences)

    // coil
    implementation(libs.coil.kt)

    // 네이버 지도 SDK
    implementation(libs.map.sdk)
    // FusedLocationSource
    implementation(libs.play.services.location)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)
}