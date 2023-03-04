package com.santimattius.template.data.repositories

import android.content.Context
import com.santimattius.template.data.datasources.implementation.MovieDataSource
import com.santimattius.template.data.datasources.implementation.RoomDataSource
import com.santimattius.template.data.dtoToEntity
import com.santimattius.template.data.entityToDomain
import com.santimattius.template.domain.entities.Movie
import com.santimattius.template.domain.repositories.MovieRepository

internal class TMDbRepository(context: Context) : MovieRepository {

    private val remoteDataSource = MovieDataSource()
    private val localDataSource = RoomDataSource(context)

    override suspend fun getPopular(): List<Movie> {
        if (localDataSource.isEmpty()) {
            val movies = remoteDataSource.getPopularMovies().getOrDefault(emptyList())
            localDataSource.save(movies.dtoToEntity())
        }
        return localDataSource.getAll().entityToDomain()
    }

    override suspend fun fetchPopular() = remoteDataSource.getPopularMovies().fold(onSuccess = {
        localDataSource.save(it.dtoToEntity())
        Result.success(localDataSource.getAll().entityToDomain())
    }, onFailure = {
        Result.failure(RefreshMovieFailed())
    })
}
