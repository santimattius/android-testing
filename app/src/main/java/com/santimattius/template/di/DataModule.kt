package com.santimattius.template.di

import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.datasources.implementation.RetrofitMovieNetworkDataSource
import com.santimattius.core.data.datasources.implementation.RoomMovieLocalDataSource
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
        movieNetworkDataSource: MovieNetworkDataSource,
        movieLocalDataSource: MovieLocalDataSource,
    ): MovieRepository = TMDbRepository(
        movieNetworkDataSource = movieNetworkDataSource,
        movieLocalDataSource = movieLocalDataSource
    )

    @Provides
    fun provideLocalDataSource(theMovieDataBase: TheMovieDataBase): MovieLocalDataSource {
        return RoomMovieLocalDataSource(theMovieDataBase = theMovieDataBase)
    }

    @Provides
    fun provideRemoteDataSource(service: TheMovieDBService): MovieNetworkDataSource {
        return RetrofitMovieNetworkDataSource(service = service)
    }
}