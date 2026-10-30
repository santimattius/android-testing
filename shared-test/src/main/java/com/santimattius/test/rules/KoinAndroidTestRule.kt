package com.santimattius.test.rules

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Applies [configuration] to the running Koin application, starting one if none exists.
 *
 * A Koin application started by this rule is stopped when the test finishes. If the application
 * already runs (for example from a test `Application`), the configuration is applied on top of it
 * and left in place, so later tests keep a working container.
 */
class KoinAndroidTestRule(
    private val configuration: KoinApplication.() -> Unit,
    private val contextProvider: () -> Context = {
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    }
) : TestWatcher() {

    private var startedByRule = false

    override fun starting(description: Description?) {
        val application = GlobalContext.getKoinApplicationOrNull()
        if (application == null) {
            startKoin {
                androidContext(contextProvider())
                configuration()
            }
            startedByRule = true
        } else {
            application.configuration()
        }
    }

    override fun finished(description: Description?) {
        if (startedByRule) {
            stopKoin()
            startedByRule = false
        }
    }
}
