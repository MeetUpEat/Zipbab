import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleService)
    alias(libs.plugins.navigationSafeArgs)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.hilt)
    kotlin("kapt")

}

android {
    namespace = "com.bestapp.rice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bestapp.rice"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "FIREBASE_KEY", getValue("firebase_key"))
        buildConfigField("String", "KAKAO_NATIVE_KEY", getValue("kakao_map_native_key"))
        buildConfigField("String", "KAKAO_REST_API_KEY", getValue("kakao_map_rest_api_key"))
        buildConfigField("String", "KAKAO_MAP_BASE_URL", getValue("kakao_map_base_url"))
        buildConfigField("String", "KAKAO_NOTIFY_BASE_URL", getValue("kakao_notify_base_url"))
        buildConfigField("String", "KAKAO_ADMIN_KEY", getValue("kakao_admin_key"))
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
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.fragment)


    // navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    // datastore
    implementation(libs.androidx.datastore.preferences)

    // coil
    implementation(libs.coil.kt)

    // kakao Map
    implementation(libs.kakao.maps)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)
    //
}