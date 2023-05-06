package com.santimattius.template.data.repositories.fakes

import com.santimattius.template.data.datasources.LocalDataSource
import com.santimattius.template.data.datasources.implementation.MovieNoExists
import com.santimattius.template.data.datasources.implementation.MovieNoSaved
import com.santimattius.template.data.entities.MovieEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeLocalDataSource : LocalDataSource {

    private val mutex = Mutex()
    private val movies = mutableListOf<MovieEntity>()

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
            val result = this@FakeLocalDataSource.movies.addAll(movies)
            if (result) {
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
            if (result) Result.success(true) else Result.failure(MovieNoExists())
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
                Result.success(true)
            }
        }
    }

}