package com.santimattius.template

import android.app.Application
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.defaultModule

@OptIn(KoinExperimentalAPI::class)
class TestApplication : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration {
        return KoinConfiguration {
            //TODO: kspAndroidTest no generate FakeDataModule
            val fakeDataModule = com.santimattius.template.di.FakeDataModule()
            modules(
                com_santimattius_template_di_AppModule,
                defaultModule,
                module {
                    single { fakeDataModule.provideMovieRepository(get(), get()) }
                    single { fakeDataModule.provideFakeLocalDataSource() }
                    single { fakeDataModule.provideRemoteDataSource() }
                }
            )
        }
    }

}