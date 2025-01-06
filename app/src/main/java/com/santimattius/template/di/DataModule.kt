package com.santimattius.template.di

import com.santimattius.core.data.client.database.AppDataBase
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.core.data.datasources.LocalDataSource
import com.santimattius.core.data.datasources.RemoteDataSource
import com.santimattius.core.data.datasources.implementation.MovieDataSource
import com.santimattius.core.data.datasources.implementation.RoomDataSource
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.core.domain.repositories.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

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
        return RoomDataSource(appDataBase = appDataBase)
    }

    @Provides
    fun provideRemoteDataSource(service: TheMovieDBService): RemoteDataSource {
        return MovieDataSource(service = service)
    }
}