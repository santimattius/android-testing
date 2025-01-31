package com.santimattius.template.di

import android.content.Context
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.template.BuildConfig
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton


@Module
class AppModule {

    @Factory
    fun provideAppDatabase(context: Context): TheMovieDataBase =
        TheMovieDataBase.get(context)

    @Factory
    fun provideMovieDBService(serviceCreator: RetrofitServiceCreator): TheMovieDBService =
        serviceCreator.createService(TheMovieDBService::class.java)


    @Singleton
    fun provideRetrofit(): RetrofitServiceCreator {
        return RetrofitServiceCreator(
            baseUrl = "https://api.themoviedb.org",
            apiKey = BuildConfig.apiKey
        )
    }
}