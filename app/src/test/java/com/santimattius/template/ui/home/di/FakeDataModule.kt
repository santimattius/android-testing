package com.santimattius.template.ui.home.di

import com.santimattius.shared_test.data.FakeMovieLocalDataSource
import com.santimattius.shared_test.data.FakeMovieNetworkDataSource
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.template.di.DataModule
import com.santimattius.core.domain.repositories.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
class FakeDataModule {

    @Provides
    fun provideMovieRepository(
        @Named("fake_remote_data_source") movieNetworkDataSource: MovieNetworkDataSource,
        @Named("fake_local_data_source") movieLocalDataSource: MovieLocalDataSource,
    ): MovieRepository = TMDbRepository(
        movieNetworkDataSource = movieNetworkDataSource,
        movieLocalDataSource = movieLocalDataSource
    )

    @Named("fake_local_data_source")
    @Provides
    fun provideFakeLocalDataSource(): MovieLocalDataSource {
        return FakeMovieLocalDataSource()
    }

    @Named("fake_remote_data_source")
    @Provides
    fun provideRemoteDataSource(): MovieNetworkDataSource {
        return FakeMovieNetworkDataSource()
    }
}