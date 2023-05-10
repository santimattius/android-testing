@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.santimattius.shared_test"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    implementation(project(":app"))
    implementation(libs.bundles.coroutine)
    implementation(libs.coroutine.test)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.serializable)

    implementation(libs.hilt.android)
    implementation(libs.hilt.test)
    kapt(libs.hilt.compiler)
    implementation(libs.bundles.unitTesting)
//    implementation("org.hamcrest:hamcrest-core:2.2")
}