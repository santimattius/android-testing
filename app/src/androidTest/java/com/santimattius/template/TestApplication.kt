package com.santimattius.template

import android.app.Application
import com.santimattius.template.di.DataModule
import com.santimattius.template.di.FakeDataModule
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.plugin.module.dsl.modules

@OptIn(KoinExperimentalAPI::class)
class TestApplication : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration {
        return KoinConfiguration {
            // FakeDataModule is loaded last so its definitions override the real data layer.
            modules(DataModule::class, FakeDataModule::class)
        }
    }
}
