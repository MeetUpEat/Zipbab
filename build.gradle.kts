// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.androidx.navigation.safe.args)
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.navigationSafeArgs) apply false
    alias(libs.plugins.kotlinParcelize) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.googleService) apply false
    alias(libs.plugins.protobuf) apply false
}