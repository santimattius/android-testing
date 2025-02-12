package com.santimattius.template

import android.app.Application
import io.kotzilla.sdk.analytics.koin.analyticsLogger
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_di_DataModule
import org.koin.ksp.generated.defaultModule

@OptIn(KoinExperimentalAPI::class)
class MainApplication : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration {
        return KoinConfiguration {
            androidContext(this@MainApplication)
            analyticsLogger()
            logger(AndroidLogger())
            modules(
                com_santimattius_template_di_DataModule,
                com_santimattius_template_di_AppModule,
                defaultModule
            )
        }
    }
}