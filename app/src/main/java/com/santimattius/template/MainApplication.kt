package com.santimattius.template

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
@KoinApplication
class MainApplication : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration {
        return koinConfiguration<MainApplication> {
            androidContext(this@MainApplication)
            logger(AndroidLogger())
        }
    }
}
