package com.santimattius.template.di

import com.santimattius.test.data.FakeMovieLocalDataSource
import com.santimattius.test.data.FakeMovieNetworkDataSource
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.core.domain.repositories.MovieRepository
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named

@Module
class FakeDataModule {

    @Factory
    fun provideMovieRepository(
        @Named("_fake_remote_data_source") movieNetworkDataSource: MovieNetworkDataSource,
        @Named("_fake_local_data_source") movieLocalDataSource: MovieLocalDataSource,
    ): MovieRepository = TMDbRepository(
        movieNetworkDataSource = movieNetworkDataSource,
        movieLocalDataSource = movieLocalDataSource
    )

    @Named("_fake_local_data_source")
    @Factory
    fun provideFakeLocalDataSource(): MovieLocalDataSource {
        return FakeMovieLocalDataSource()
    }

    @Named("_fake_remote_data_source")
    @Factory
    fun provideRemoteDataSource(): MovieNetworkDataSource {
        return FakeMovieNetworkDataSource()
    }
}