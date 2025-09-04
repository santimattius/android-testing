package com.santimattius.template.di

import android.content.Context
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.template.BuildConfig
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.koin.core.annotation.Singleton


@Module(createdAtStart = true)
class AppModule {

    @Single
    fun provideAppDatabase(context: Context): TheMovieDataBase =
        TheMovieDataBase.get(context)

    @Single
    fun provideMovieDBService(): TheMovieDBService {
        return RetrofitServiceCreator.createService(
            baseUrl = "https://api.themoviedb.org", apiKey = BuildConfig.apiKey
        )
    }

    /*@Single
    fun provideMovieDBService(serviceCreator: RetrofitServiceCreator): TheMovieDBService =
        serviceCreator.createService(TheMovieDBService::class.java)


    //TODO: com.santimattius.core.data.client.network.RetrofitServiceCreator
       resolution performance is slow regarding the number of dependencies to retrieve.
    @Single(createdAtStart = true)
    fun provideRetrofit(): RetrofitServiceCreator {
        return RetrofitServiceCreator(
            baseUrl = "https://api.themoviedb.org",
            apiKey = BuildConfig.apiKey
        )
    }*/
}