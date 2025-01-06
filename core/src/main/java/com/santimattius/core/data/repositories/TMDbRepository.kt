package com.santimattius.core.data.repositories

import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.core.data.entityToDomain
import com.santimattius.core.domain.entities.Movie
import com.santimattius.core.domain.repositories.MovieRepository

class TMDbRepository(
    private val movieNetworkDataSource: MovieNetworkDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
) : MovieRepository {

    override suspend fun getAll(): List<Movie> {
        if (movieLocalDataSource.isEmpty()) {
            val movies = movieNetworkDataSource.getPopularMovies().getOrDefault(emptyList())
            movieLocalDataSource.save(movies.dtoToEntity())
        }
        return movieLocalDataSource.getAll().entityToDomain()
    }

    override suspend fun refresh() = movieNetworkDataSource.getPopularMovies().fold(onSuccess = {
        movieLocalDataSource.save(it.dtoToEntity())
        Result.success(movieLocalDataSource.getAll().entityToDomain())
    }, onFailure = {
        Result.failure(RefreshMovieFailed())
    })
}
