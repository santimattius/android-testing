package com.santimattius.template.ui.di

import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.core.domain.repositories.MovieRepository
import com.santimattius.test.data.FakeMovieLocalDataSource
import com.santimattius.test.data.FakeMovieNetworkDataSource
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named

@Module
class FakeDataModule {

    @Factory
    fun provideMovieRepository(
        @Named("fake_remote_data_source") movieNetworkDataSource: MovieNetworkDataSource,
        @Named("fake_local_data_source") movieLocalDataSource: MovieLocalDataSource,
    ): MovieRepository = TMDbRepository(
        movieNetworkDataSource = movieNetworkDataSource,
        movieLocalDataSource = movieLocalDataSource
    )

    @Named("fake_local_data_source")
    @Factory
    fun provideFakeLocalDataSource(): MovieLocalDataSource {
        return FakeMovieLocalDataSource()
    }

    @Named("fake_remote_data_source")
    @Factory
    fun provideRemoteDataSource(): MovieNetworkDataSource {
        return FakeMovieNetworkDataSource()
    }
}