// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.koin.compiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.google.secrets.gradle.plugin) apply false
    alias(libs.plugins.automattic.measure.builds) apply false
    alias(libs.plugins.room) apply false
}

buildscript {
    dependencies {
        classpath(libs.dep.google.secrets.gradle.plugin)
    }
}
// Fails when line coverage drops below the committed baseline (minus a small tolerance), which is
// the regression check the Android CI guidance recommends instead of an absolute threshold.
// Generate reports first: createDebugUnitTestCoverageReport / createDebugAndroidTestCoverageReport.
// Select kinds with -PcoverageKinds=test,androidTest (default: both).
tasks.register("coverageRegressionCheck") {
    group = "verification"
    description = "Fails if line coverage regressed against config/coverage/baseline.properties."

    val baselineFile = layout.projectDirectory.file("config/coverage/baseline.properties")
    val kinds = providers.gradleProperty("coverageKinds").orElse("test,androidTest")
        .map { it.split(",").map(String::trim) }
    val modules = listOf("app", "core")
    val reportDirs = modules.associateWith { module ->
        layout.projectDirectory.dir("$module/build/reports/coverage")
    }
    val tolerance = 1.0

    doLast {
        val baseline = java.util.Properties().apply { baselineFile.asFile.inputStream().use(::load) }
        val failures = mutableListOf<String>()
        for (module in modules) {
            for (kind in kinds.get()) {
                val relative = if (kind == "test") "test/debug/report.xml"
                else "androidTest/debug/connected/report.xml"
                val report = reportDirs.getValue(module).file(relative).asFile
                if (!report.exists()) {
                    failures += "$module.$kind: coverage report not found at ${report.path}"
                    continue
                }
                // The report-level counters are the last LINE counter in the document.
                val line = Regex("""<counter type="LINE" missed="(\d+)" covered="(\d+)"/>""")
                    .findAll(report.readText()).last()
                val (missed, covered) = line.destructured
                val actual = 100.0 * covered.toInt() / (missed.toInt() + covered.toInt())
                val expected = baseline.getProperty("$module.$kind").toDouble()
                val status = if (actual + tolerance < expected) "REGRESSED" else "ok"
                println("$module.$kind: %.1f%% (baseline %.1f%%) %s".format(actual, expected, status))
                if (status == "REGRESSED") {
                    failures += "$module.$kind dropped to %.1f%% (baseline %.1f%%)".format(actual, expected)
                }
            }
        }
        if (failures.isNotEmpty()) throw GradleException(failures.joinToString("\n"))
    }
}
