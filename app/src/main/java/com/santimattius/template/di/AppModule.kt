package com.santimattius.template.di

import android.content.Context
import com.santimattius.template.BuildConfig
import com.santimattius.template.data.client.database.AppDataBase
import com.santimattius.template.data.client.network.TheMovieDBService
import com.santimattius.template.data.client.network.createRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase =
        AppDataBase.get(context)

    @Provides
    fun provideMovieDBService(retrofit: Retrofit): TheMovieDBService =
        retrofit.create()


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return createRetrofitService(
            baseUrl = "https://api.themoviedb.org",
            apiKey = BuildConfig.API_KEY
        )
    }
}