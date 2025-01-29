plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

apply("$rootDir/gradle/report.gradle")

android {
    namespace = "com.santimattius.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    room {
        schemaDirectory("$projectDir/schemas")
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
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
        unitTests.all {
            testCoverage {
                version = "0.8.8"
            }
        }
    }

    libraryVariants.forEach { variant ->
        variant.sourceSets.forEach {
            it.javaDirectories += files("build/generated/ksp/${variant.name}/kotlin")
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.app.compart)
    implementation(libs.material)

    //Networking
    implementation(libs.bundles.coroutine)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.serializable)

    //Storage
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    //Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    testImplementation(project(path = ":shared-test"))
    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.bundles.robolectric)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.kotlin)

    testImplementation(libs.koin.test.core)
    testImplementation(libs.koin.test.junit4)

    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.test.espresso)
}