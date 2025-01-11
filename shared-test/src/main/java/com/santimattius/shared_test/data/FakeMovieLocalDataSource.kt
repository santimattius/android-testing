package com.santimattius.shared_test.data

import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.datasources.implementation.MovieNoExists
import com.santimattius.core.data.datasources.implementation.MovieNoSaved
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeMovieLocalDataSource : MovieLocalDataSource {

    private val mutex = Mutex()
    private val movies = mutableListOf<MovieEntity>()

    private val moviesFlow = MutableStateFlow<List<MovieEntity>>(movies)

    override val all: Flow<List<MovieEntity>>
        get() = moviesFlow.asStateFlow()

    override suspend fun getAll(): List<MovieEntity> {
        return mutex.withLock {
            movies
        }
    }

    override suspend fun isEmpty(): Boolean {
        return mutex.withLock {
            movies.isEmpty()
        }
    }

    override suspend fun save(movies: List<MovieEntity>): Result<Boolean> {
        return mutex.withLock {
            val result = this@FakeMovieLocalDataSource.movies.addAll(movies)
            if (result) {
                moviesFlow.value = this@FakeMovieLocalDataSource.movies
                Result.success(value = true)
            } else {
                Result.failure(MovieNoSaved())
            }
        }
    }

    override suspend fun find(id: Int): Result<MovieEntity> {
        return mutex.withLock {
            val movie = movies.firstOrNull { it.id == id }
            if (movie == null) Result.failure(MovieNoExists()) else Result.success(movie)
        }
    }

    override suspend fun delete(movie: MovieEntity): Result<Boolean> {
        return mutex.withLock {
            val result = movies.remove(movie)
            if (result) {
                moviesFlow.value = this@FakeMovieLocalDataSource.movies
                Result.success(true)
            } else {
                Result.failure(MovieNoExists())
            }
        }
    }

    override suspend fun update(movie: MovieEntity): Result<Boolean> {
        return mutex.withLock {
            val index = movies.indexOfFirst { it.id == movie.id }
            if (index == -1) {
                Result.failure(MovieNoExists())
            } else {
                movies.removeAt(index)
                movies.add(movie)
                moviesFlow.value = this@FakeMovieLocalDataSource.movies
                Result.success(true)
            }
        }
    }

}