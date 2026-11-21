package com.santimattius.core.data.repositories

import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.dtoToEntity
import com.santimattius.core.data.entityToDomain
import com.santimattius.core.domain.entities.Movie
import com.santimattius.core.domain.repositories.MovieRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TMDbRepository(
    private val movieNetworkDataSource: MovieNetworkDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
) : MovieRepository {

    override val all: Flow<List<Movie>> = movieLocalDataSource.all.map {
        it.entityToDomain()
    }

    override suspend fun getAll(): List<Movie> {
        if (movieLocalDataSource.isEmpty()) {
            val movies = movieNetworkDataSource.getPopularMovies().getOrThrow()
            movieLocalDataSource.save(movies.dtoToEntity()).getOrThrow()
        }
        return movieLocalDataSource.getAll().entityToDomain()
    }

    override suspend fun refresh(): Result<List<Movie>> {
        val networkResult = movieNetworkDataSource.getPopularMovies()
        val movies = networkResult.getOrElse {
            if (it is CancellationException) throw it
            return Result.failure(it)
        }
        return movieLocalDataSource.replace(movies.dtoToEntity()).map { it.entityToDomain() }
    }

    override suspend fun addToFavorite(movieId: Long): Result<Unit> {
        return movieLocalDataSource.addToFavorite(movieId)
    }

    override suspend fun removeFromFavorite(movieId: Long): Result<Unit> {
        return movieLocalDataSource.removeFromFavorite(movieId)
    }
}
