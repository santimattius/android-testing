package com.santimattius.template.di

import android.content.Context
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.template.BuildConfig
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single


@Module
class AppModule {

    @Single
    fun provideAppDatabase(context: Context): TheMovieDataBase =
        TheMovieDataBase.get(context)

    @Single
    fun provideMovieDBService(serviceCreator: RetrofitServiceCreator): TheMovieDBService =
        serviceCreator.createService(TheMovieDBService::class.java)


    @Single
    fun provideRetrofit(@Named("base_url") baseUrl: String): RetrofitServiceCreator {
        return RetrofitServiceCreator(
            baseUrl = baseUrl,
            apiKey = BuildConfig.apiKey
        )
    }

    @Named("base_url")
    @Single
    @Suppress("FunctionOnlyReturningConstant")
    fun provideBaseUrl() = "https://api.themoviedb.org"
}