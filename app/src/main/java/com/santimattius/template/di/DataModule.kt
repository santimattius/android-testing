package com.santimattius.template.di

import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.datasources.implementation.RetrofitMovieNetworkDataSource
import com.santimattius.core.data.datasources.implementation.RoomMovieLocalDataSource
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.core.domain.repositories.MovieRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class DataModule {

    @Singleton
    fun provideMovieRepository(
        movieNetworkDataSource: MovieNetworkDataSource,
        movieLocalDataSource: MovieLocalDataSource,
    ): MovieRepository = TMDbRepository(
        movieNetworkDataSource = movieNetworkDataSource,
        movieLocalDataSource = movieLocalDataSource
    )

    @Singleton
    fun provideLocalDataSource(theMovieDataBase: TheMovieDataBase): MovieLocalDataSource {
        return RoomMovieLocalDataSource(theMovieDataBase = theMovieDataBase)
    }

    @Singleton
    fun provideRemoteDataSource(service: TheMovieDBService): MovieNetworkDataSource {
        return RetrofitMovieNetworkDataSource(service = service)
    }
}