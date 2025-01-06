package com.santimattius.template.ui.home.di

import com.santimattius.shared_test.data.FakeLocalDataSource
import com.santimattius.shared_test.data.FakeRemoteDataSource
import com.santimattius.core.data.datasources.LocalDataSource
import com.santimattius.core.data.datasources.RemoteDataSource
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
        @Named("fake_remote_data_source") remoteDataSource: RemoteDataSource,
        @Named("fake_local_data_source") localDataSource: LocalDataSource,
    ): MovieRepository = TMDbRepository(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    )

    @Named("fake_local_data_source")
    @Provides
    fun provideFakeLocalDataSource(): LocalDataSource {
        return FakeLocalDataSource()
    }

    @Named("fake_remote_data_source")
    @Provides
    fun provideRemoteDataSource(): RemoteDataSource {
        return FakeRemoteDataSource()
    }
}