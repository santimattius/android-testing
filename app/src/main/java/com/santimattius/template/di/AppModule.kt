package com.santimattius.template.di

import android.content.Context
import com.santimattius.template.BuildConfig
import com.santimattius.template.data.client.database.AppDataBase
import com.santimattius.template.data.client.network.TheMovieDBService
import com.santimattius.template.data.client.network.createRetrofitService
import com.santimattius.template.data.datasources.LocalDataSource
import com.santimattius.template.data.datasources.RemoteDataSource
import com.santimattius.template.data.datasources.implementation.MovieDataSource
import com.santimattius.template.data.datasources.implementation.RoomDataSource
import com.santimattius.template.data.repositories.TMDbRepository
import com.santimattius.template.domain.repositories.MovieRepository
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
    fun provideMovieRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
    ): MovieRepository = TMDbRepository(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    )

    @Provides
    fun provideLocalDataSource(appDataBase: AppDataBase): LocalDataSource {
        return RoomDataSource(dao = appDataBase.dao())
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDataBase.get(context)

    @Provides
    fun provideRemoteDataSource(service: TheMovieDBService): RemoteDataSource {
        return MovieDataSource(service = service)
    }

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