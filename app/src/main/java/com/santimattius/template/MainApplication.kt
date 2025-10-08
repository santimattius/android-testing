package com.santimattius.template

import android.app.Application
import android.util.Log
import com.santimattius.core.CoreModule
import com.santimattius.template.di.AppModule
import com.santimattius.template.di.DataModule
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.defineComSantimattiusTemplateUiComposeHomeComposeViewModel
import org.koin.ksp.generated.defineComSantimattiusTemplateUiXmlHomeHomeViewModel
import org.koin.ksp.generated.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class MainApplication : Application(), KoinStartup {

    override fun onKoinStartup(): KoinConfiguration {
        Log.d(this::class.simpleName, "onKoinStartup: ${Thread.currentThread().name}")
        val configuration = MainKoinApplication.koinConfiguration {
            androidContext(this@MainApplication)
            analytics()
            modules(modules = module {
                defineComSantimattiusTemplateUiComposeHomeComposeViewModel()
                defineComSantimattiusTemplateUiXmlHomeHomeViewModel()
            })
        }
        return KoinConfiguration(configuration)
    }
}

@KoinApplication(
    configurations = ["default"],
    modules = [CoreModule::class, DataModule::class, AppModule::class]
)
object MainKoinApplication