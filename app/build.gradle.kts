import com.automattic.android.measure.reporters.SlowSlowTasksMetricsReporter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.secrets.gradle.plugin)
    alias(libs.plugins.automattic.measure.builds)
}

android {
    namespace = "com.santimattius.template"
    compileSdk = extraString("target_sdk_version").toInt()

    defaultConfig {
        applicationId = extraString("application_id")
        minSdk = extraString("min_sdk_version").toInt()
        targetSdk = extraString("target_sdk_version").toInt()
        versionCode = extraString("version_code").toInt()
        versionName = extraString("version_name")

        testInstrumentationRunner = "com.santimattius.template.InstrumentationTestRunner"
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
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        managedDevices {
            localDevices {
                create("pixel9api35") {
                    device = "Pixel 9"
                    apiLevel = 35
                    systemImageSource = "aosp-atd"
                }
            }
        }
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
        unitTests.all {
            testCoverage {
                version = "0.8.16"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
        }
    }
}

secrets {
    defaultPropertiesFileName = "local.defaults.properties"
}

composeCompiler {
    reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
}

fun extraString(key: String): String {
    return extra[key] as String
}

detekt {
    config.from("${project.rootDir}/config/detekt/detekt.yml")
    baseline = file("$rootDir/detekt-baseline.xml")
}

// Detekt 1.23 rejects newer JDK targets, so pin it to the project's Java target.
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
}

configurations {
    androidTestImplementation {
        exclude("io.mockk", "mockk-agent-jvm")
    }
}

// compose-bom:2026.03.00 strictly pins monitor:1.6.0, but espresso-core:3.7.0 was compiled
// against monitor:1.8.0 (ReflectiveMethod(Class,String,Class[]) constructor added in 1.8.0).
// Force 1.8.0 across all androidTest configurations to override the BOM strict constraint
// and prevent NoSuchMethodError at runtime.
configurations.matching { it.name.contains("AndroidTest") || it.name.contains("androidTest") }
    .configureEach {
        resolutionStrategy {
            force("androidx.test:monitor:1.8.0")
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

koinCompiler {
    // Fails the build when a definition cannot be resolved (replaces KOIN_CONFIG_CHECK).
    compileSafety = true
}

dependencies {
    implementation(project(":core"))
    implementation(libs.core.ktx)

    implementation(libs.bundles.ui)
    implementation(libs.bundles.lifecycle)
    implementation(libs.activity.compose)

    testImplementation(libs.lifecycle.viewmodel.testing)

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

    //Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.startup)

    implementation(libs.koin.annotations.core)

    testImplementation(libs.koin.test.core)
    testImplementation(libs.koin.test.junit4)

    androidTestUtil(libs.test.orchestrator)
    androidTestImplementation(libs.coil.test)
    androidTestImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.koin.android.test)

    //Unit Testing
    testImplementation(project(path = ":shared-test"))
    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.bundles.robolectric)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.okHttp3.idling.resource)

    //Android Testing
    debugImplementation(libs.androidx.fragment.testing.manifest)
    implementation(libs.androidx.fragment.testing)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.bundles.androidTesting)
    androidTestImplementation(libs.coroutine.test)
    androidTestImplementation(project(path = ":shared-test"))

}