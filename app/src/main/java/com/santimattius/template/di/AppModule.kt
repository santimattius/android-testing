package com.santimattius.template.di

import com.santimattius.template.BuildConfig
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single


@Module
class AppModule {

    @Single(createdAtStart = true)
    @Named("baseUrl")
    fun provideBaseUrl() = "https://api.themoviedb.org"

    @Single(createdAtStart = true)
    @Named("apiKey")
    fun provideApiKey() = BuildConfig.apiKey
}