package com.santimattius.template.di

import android.content.Context
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.template.BuildConfig
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton


@Module
class AppModule {

    @Singleton
    fun provideAppDatabase(context: Context): TheMovieDataBase =
        TheMovieDataBase.get(context)

    @Singleton
    fun provideMovieDBService(serviceCreator: RetrofitServiceCreator): TheMovieDBService =
        serviceCreator.createService(TheMovieDBService::class.java)


    @Singleton
    fun provideRetrofit(@Named("base_url") baseUrl: String): RetrofitServiceCreator {
        return RetrofitServiceCreator(
            baseUrl = baseUrl,
            apiKey = BuildConfig.apiKey
        )
    }

    @Named("base_url")
    @Singleton
    fun provideBaseUrl() = "https://api.themoviedb.org"
}