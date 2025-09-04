package com.santimattius.template

import android.app.Application
import android.util.Log
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.com_santimattius_core_CoreModule
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_di_DataModule
import org.koin.ksp.generated.defaultModule

@OptIn(KoinExperimentalAPI::class)
class MainApplication : Application(), KoinStartup {

    override fun onKoinStartup(): KoinConfiguration {
        Log.d(this::class.simpleName, "onKoinStartup: ${Thread.currentThread().name}")
        return KoinConfiguration {
            androidContext(this@MainApplication)
            analytics()
            modules(
                com_santimattius_core_CoreModule,
                com_santimattius_template_di_DataModule,
                com_santimattius_template_di_AppModule,
                defaultModule
            )
        }
    }
}