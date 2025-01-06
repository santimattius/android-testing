package com.santimattius.core.data.repositories

import com.santimattius.core.data.datasources.LocalDataSource
import com.santimattius.core.data.datasources.RemoteDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.core.data.entityToDomain
import com.santimattius.core.domain.entities.Movie
import com.santimattius.core.domain.repositories.MovieRepository

class TMDbRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : MovieRepository {

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
