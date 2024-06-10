import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    kotlin("kapt")


}

android {
    namespace = "com.bestapp.rice.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

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
        buildConfig = true
    }
}

fun getValue(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)

    // dataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.datastore)
    implementation(libs.protobuf.javalite)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    // OkHttp3
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.27.0" // toml에 정의해서 사용하면 ObjectInstantiationException 발생
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}