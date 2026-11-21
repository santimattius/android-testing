package com.santimattius.core.data.datasources.implementation

import com.santimattius.core.data.client.database.MovieDao
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class RoomMovieLocalDataSource internal constructor(
    private val dao: MovieDao,
) : MovieLocalDataSource {

    constructor(theMovieDataBase: TheMovieDataBase) : this(theMovieDataBase.dao())

    override val all: Flow<List<MovieEntity>>
        get() = dao.searchAll()

    override suspend fun getAll(): List<MovieEntity> = dao.getAll()

    override suspend fun isEmpty() = runSafe { count() == 0 }
        .getOrDefault(defaultValue = true)

    override suspend fun save(movies: List<MovieEntity>): Result<Boolean> = runSafe {
        upsert(movies); true
    }

    override suspend fun replace(movies: List<MovieEntity>): Result<List<MovieEntity>> = runSafe {
        replace(movies)
    }

    override suspend fun find(id: Long) = runSafe {
        findById(id)
    }.fold(onSuccess = {
        if (it == null) Result.failure(MovieNoExists())
        else Result.success(it)
    }, onFailure = {
        Result.failure(MovieNoExists())
    })

    override suspend fun delete(movie: MovieEntity) = runSafe { delete(movie); true }

    override suspend fun update(movie: MovieEntity) = runSafe { update(movie); true }

    override suspend fun addToFavorite(movieId: Long): Result<Unit> {
        return runSafe { addToFavorite(movieId) }
    }

    override suspend fun removeFromFavorite(movieId: Long): Result<Unit> {
        return runSafe { removeFromFavorite(movieId) }
    }

    private suspend fun <R> runSafe(block: suspend MovieDao.() -> R): Result<R> = try {
        Result.success(dao.block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (failure: Throwable) {
        Result.failure(failure)
    }
}