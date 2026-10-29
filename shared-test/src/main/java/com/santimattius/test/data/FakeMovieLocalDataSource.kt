package com.santimattius.test.data

import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.implementation.MovieNoExists
import com.santimattius.core.data.datasources.implementation.MovieNoSaved
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory [MovieLocalDataSource] that publishes an immutable snapshot on every change,
 * so collectors never observe later mutations of a list they already received.
 */
class FakeMovieLocalDataSource : MovieLocalDataSource {

    private val mutex = Mutex()
    private val moviesFlow = MutableStateFlow<List<MovieEntity>>(emptyList())

    override val all: Flow<List<MovieEntity>>
        get() = moviesFlow.asStateFlow()

    override suspend fun getAll(): List<MovieEntity> {
        return mutex.withLock { moviesFlow.value }
    }

    override suspend fun isEmpty(): Boolean {
        return mutex.withLock { moviesFlow.value.isEmpty() }
    }

    override suspend fun save(movies: List<MovieEntity>): Result<Boolean> {
        return mutex.withLock {
            if (movies.isEmpty()) {
                Result.failure(MovieNoSaved())
            } else {
                moviesFlow.value = moviesFlow.value + movies
                Result.success(value = true)
            }
        }
    }

    override suspend fun find(id: Long): Result<MovieEntity> {
        return mutex.withLock { findLocked(id) }
    }

    override suspend fun delete(movie: MovieEntity): Result<Boolean> {
        return mutex.withLock {
            val current = moviesFlow.value
            if (movie in current) {
                moviesFlow.value = current - movie
                Result.success(true)
            } else {
                Result.failure(MovieNoExists())
            }
        }
    }

    override suspend fun update(movie: MovieEntity): Result<Boolean> {
        return mutex.withLock { updateLocked(movie) }
    }

    override suspend fun addToFavorite(movieId: Long): Result<Unit> {
        return setFavorite(movieId, favorite = true)
    }

    override suspend fun removeFromFavorite(movieId: Long): Result<Unit> {
        return setFavorite(movieId, favorite = false)
    }

    private suspend fun setFavorite(movieId: Long, favorite: Boolean): Result<Unit> {
        return mutex.withLock {
            findLocked(movieId).fold(
                onSuccess = { updateLocked(it.copy(favorite = favorite)).map { } },
                onFailure = { Result.failure(it) },
            )
        }
    }

    private fun findLocked(id: Long): Result<MovieEntity> {
        val movie = moviesFlow.value.firstOrNull { it.id == id }
        return if (movie == null) Result.failure(MovieNoExists()) else Result.success(movie)
    }

    private fun updateLocked(movie: MovieEntity): Result<Boolean> {
        val current = moviesFlow.value
        val index = current.indexOfFirst { it.id == movie.id }
        return if (index == -1) {
            Result.failure(MovieNoExists())
        } else {
            moviesFlow.value = current.toMutableList().also { it[index] = movie }
            Result.success(true)
        }
    }
}
