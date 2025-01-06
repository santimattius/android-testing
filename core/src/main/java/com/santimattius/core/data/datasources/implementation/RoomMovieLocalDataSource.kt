package com.santimattius.core.data.datasources.implementation

import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.database.MovieDao
import com.santimattius.core.data.datasources.MovieLocalDataSource
import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.flow.Flow

class RoomMovieLocalDataSource(
    theMovieDataBase: TheMovieDataBase,
) : MovieLocalDataSource {

    private val dao = theMovieDataBase.dao()

    override val all: Flow<List<MovieEntity>>
        get() = dao.searchAll()

    override suspend fun getAll(): List<MovieEntity> = dao.getAll()

    override suspend fun isEmpty() = runSafe { count() == 0 }
        .getOrDefault(defaultValue = true)

    override suspend fun save(movies: List<MovieEntity>): Result<Boolean> =
        runSafe {
            deleteAndInsert(*movies.toTypedArray()); true
        }

    override suspend fun find(id: Int) = runSafe {
        findById(id)
    }.fold(onSuccess = {
        if (it == null) Result.failure(MovieNoExists())
        else Result.success(it)
    }, onFailure = {
        Result.failure(MovieNoExists())
    })

    override suspend fun delete(movie: MovieEntity) = runSafe { delete(movie); true }

    override suspend fun update(movie: MovieEntity) = runSafe { update(movie); true }

    private suspend fun <R> runSafe(block: suspend MovieDao.() -> R) =
        dao.runCatching { block() }
}