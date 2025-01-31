package com.santimattius.template.rules

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.getKoinApplicationOrNull
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class KoinAndroidTestRule(
    private val modules: List<Module>
) : TestWatcher() {

    override fun starting(description: Description?) {
        val application = getKoinApplicationOrNull()
        if (application == null) {
            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
                modules(modules)
            }
        } else {
            loadKoinModules(modules = modules)
        }

    }

    override fun finished(description: Description?) {
        unloadKoinModules(modules)
    }
}