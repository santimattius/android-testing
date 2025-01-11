package com.santimattius.template.di

import android.content.Context
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.template.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): TheMovieDataBase =
        TheMovieDataBase.get(context)

    @Provides
    fun provideMovieDBService(serviceCreator: RetrofitServiceCreator): TheMovieDBService =
        serviceCreator.createService(TheMovieDBService::class.java)


    @Provides
    @Singleton
    fun provideRetrofit(): RetrofitServiceCreator {
        return RetrofitServiceCreator(
            baseUrl = "https://api.themoviedb.org",
            apiKey = BuildConfig.apiKey
        )
    }
}