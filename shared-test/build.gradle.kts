@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.santimattius.shared_test"
    compileSdk = 36

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
    libraryVariants.forEach { variant ->
        variant.sourceSets.forEach {
            it.javaDirectories += files("build/generated/ksp/${variant.name}/kotlin")
        }
    }
}

dependencies {

    implementation(project(":core"))
    implementation(libs.bundles.coroutine)
    implementation(libs.coroutine.test)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.serializable)

    implementation(libs.androidx.fragment.testing.manifest)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.test.core)
    implementation(libs.koin.android)

    implementation(libs.bundles.unitTesting)
}