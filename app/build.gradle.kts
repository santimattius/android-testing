import com.automattic.android.measure.reporters.SlowSlowTasksMetricsReporter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.secrets.gradle.plugin)
    alias(libs.plugins.automattic.measure.builds)
}
apply("$rootDir/gradle/report.gradle")

android {
    namespace = "com.santimattius.template"
    compileSdk = extraString("target_sdk_version").toInt()

    defaultConfig {
        applicationId = extraString("application_id")
        minSdk = extraString("min_sdk_version").toInt()
        targetSdk = extraString("target_sdk_version").toInt()
        versionCode = extraString("version_code").toInt()
        versionName = extraString("version_name")

        testInstrumentationRunner = "com.santimattius.template.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

composeCompiler {
    enableStrongSkippingMode = true
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

fun extraString(key: String): String {
    return extra[key] as String
}

detekt {
    toolVersion = "1.22.0"
    config = files("${project.rootDir}/config/detekt/detekt.yml")
    baseline = file("$rootDir/detekt-baseline.xml")
    autoCorrect = true
}

configurations {
    androidTestImplementation {
        exclude(  "io.mockk",  "mockk-agent-jvm")
    }
}


measureBuilds {
    enable = true
    attachGradleScanId =
        false // `false`, if no Enterprise plugin applied OR don't want to attach build scan id
    onBuildMetricsReadyListener {
        SlowSlowTasksMetricsReporter.report(this)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)

    implementation(libs.bundles.ui)
    implementation(libs.bundles.lifecycle)

    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    //Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit)
    testImplementation(libs.bundles.compose.debug)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit)

    implementation(libs.glide.core)
    implementation(libs.coil.core)

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // For Robolectric tests.
    testImplementation(libs.hilt.test)
    kspTest(libs.hilt.android.compiler)

    // For instrumented tests.
    androidTestImplementation(libs.hilt.test)
    kspAndroidTest(libs.hilt.android.compiler)

    //Unit Testing
    testImplementation(project(path = ":shared-test"))
    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.bundles.robolectric)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)

    //Android Testing
    debugImplementation(libs.androidx.fragment.testing.manifest)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.bundles.androidTesting)
    androidTestImplementation(libs.coroutine.test)
    androidTestImplementation(project(path = ":shared-test"))

}