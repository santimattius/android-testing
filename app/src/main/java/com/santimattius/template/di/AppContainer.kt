package com.santimattius.template.di

import android.app.Application
import android.content.Context
import com.santimattius.template.BuildConfig
import com.santimattius.template.data.client.database.AppDataBase
import com.santimattius.template.data.client.network.RetrofitServiceCreator
import com.santimattius.template.data.client.network.TheMovieDBService
import com.santimattius.template.data.datasources.LocalDataSource
import com.santimattius.template.data.datasources.RemoteDataSource
import com.santimattius.template.data.datasources.implementation.MovieDataSource
import com.santimattius.template.data.datasources.implementation.RoomDataSource
import com.santimattius.template.data.repositories.TMDbRepository
import com.santimattius.template.ui.home.HomeViewModel

internal object AppContainer {

    @Deprecated(
        message = "Is bad implementation",
        replaceWith = ReplaceWith(""),
        level = DeprecationLevel.WARNING
    )

    fun getRepository(application: Application) = TMDbRepository(
        remoteDataSource = MovieDataSource(service = provideMovieDBService()),
        localDataSource = RoomDataSource(dao = provideAppDatabase(application).dao())
    )

    fun provideViewModel(context: Context): HomeViewModel {
        //local datasource
        val appDataBase = provideAppDatabase(context)
        val localDataSource = provideLocalDataSource(appDataBase)
        //remote datasource
        val service = RetrofitServiceCreator.create<TheMovieDBService>(BuildConfig.API_KEY)
        val remoteDataSource = provideRemoteDataSource(service)
        val movieRepository = provideMovieRepository(remoteDataSource, localDataSource)
        return HomeViewModel(movieRepository)
    }

    private fun provideMovieRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
    ) = TMDbRepository(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    )

    private fun provideLocalDataSource(appDataBase: AppDataBase): LocalDataSource {
        return RoomDataSource(dao = appDataBase.dao())
    }

    private fun provideAppDatabase(context: Context) = AppDataBase.get(context)

    private fun provideRemoteDataSource(service: TheMovieDBService): RemoteDataSource {
        return MovieDataSource(service = service)
    }

    private fun provideMovieDBService(): TheMovieDBService =
        RetrofitServiceCreator.create(BuildConfig.API_KEY)


}