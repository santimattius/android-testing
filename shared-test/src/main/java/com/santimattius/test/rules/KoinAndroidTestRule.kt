package com.santimattius.test.rules

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class KoinAndroidTestRule(
    private val modules: List<Module>,
    private val contextProvider: () -> Context = {
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    }
) : TestWatcher() {

    override fun starting(description: Description?) {
        val application = GlobalContext.getKoinApplicationOrNull()
        if (application == null) {
            startKoin {
                androidContext(contextProvider())
                modules(modules)
            }
        } else {
            loadKoinModules(modules = modules)
        }

    }

    override fun finished(description: Description?) {
        GlobalContext.unloadKoinModules(modules)
    }
}